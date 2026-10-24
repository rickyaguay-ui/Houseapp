"""
gate.py  —  Layer-0 load-time bundle validator  (v1, 2026-06-16)

WHAT THIS IS
    A deterministic, pure static analyzer that proves a state-machine BUNDLE
    is well-formed BEFORE a single event flows or any instance is created.
    Same input -> same findings, every time. No randomness, no I/O, no state.

THE ONE LAW (closed this session, supersedes three older per-state checks)
    A reachable, non-terminal state that cannot reach any terminal state is
    TRAPPED.  A bundle containing any trapped state is rejected.
    This single property subsumes, as instances:
        - DEAD_END      : a trapped state with no outgoing transitions
        - SELF_TRAP     : a trapped state whose only exit loops to itself
        - SEALED_CLUSTER: a trapped strongly-connected group of >= 2 states
                          that only cycle among themselves
    It is PRECISE, not blunt: a loop WITH an exit to a terminal is permitted.
    Strongly-connected-component analysis (Tarjan) is used only to NAME the
    trapped structure in the fault, so every rejection is auditable.

WHAT THIS DOES NOT YET CATCH  (known open gap — see INDEX.md)
    Logical guard contradiction: two transition guards that can both be true
    by different logic. The gate catches STRUCTURAL nondeterminism, not
    semantic guard overlap. Not implemented here on purpose — no placeholder.

BUNDLE SCHEMA
    bundle = {
        "name":        str,
        "initial":     state_id,                       # the start state
        "terminal":    iterable[state_id],             # explicit accepting states
        "transitions": list[ (from_id, event, to_id) ],
        "states":      iterable[state_id]   # OPTIONAL; derived from the above
    }
    state_id and event are strings.

PUBLIC API
    validate(bundle)  -> list[Finding]   (empty list == bundle passes the gate)
    is_valid(bundle)  -> bool
    format_report(findings) -> str       (human-readable)

CONTROLLED FAULT VOCABULARY
    REACHABILITY     state unreachable from initial (dead code in the bundle)
    DEAD_END         trapped: non-terminal state with no exit
    SELF_TRAP        trapped: only exit loops to itself
    SEALED_CLUSTER   trapped: a >=2-state group with no exit to a terminal
    FEEDS_TRAP       trapped: has exits, but every path leads only into a trap
"""

from typing import Dict, List, Set, Tuple, NamedTuple, Iterable


class Finding(NamedTuple):
    code: str            # one of the controlled fault vocabulary above
    states: Tuple[str, ...]   # the involved state(s), sorted


# ---------------------------------------------------------------- bundle utils
def _states_of(bundle: Dict) -> Set[str]:
    s: Set[str] = set(bundle.get("states", ()))
    s.add(bundle["initial"])
    s |= set(bundle["terminal"])
    for src, _evt, dst in bundle["transitions"]:
        s.add(src)
        s.add(dst)
    return s


def _out_targets(bundle: Dict, state: str) -> List[str]:
    """Targets of transitions leaving `state`, sorted for determinism."""
    return sorted(dst for (src, _evt, dst) in bundle["transitions"] if src == state)


def _forward_reachable(bundle: Dict) -> Set[str]:
    """States reachable from the initial state by following transitions."""
    seen: Set[str] = set()
    stack = [bundle["initial"]]
    while stack:
        s = stack.pop()
        if s in seen:
            continue
        seen.add(s)
        stack.extend(_out_targets(bundle, s))
    return seen


def _terminal_coreachable(bundle: Dict) -> Set[str]:
    """States from which at least one terminal state is reachable.

    Computed by breadth-first search over REVERSED edges, seeded from every
    terminal state. A state in this set can still 'get out' to an ending.
    """
    reverse: Dict[str, List[str]] = {}
    for src, _evt, dst in bundle["transitions"]:
        reverse.setdefault(dst, []).append(src)
    seen: Set[str] = set()
    stack = list(bundle["terminal"])
    while stack:
        s = stack.pop()
        if s in seen:
            continue
        seen.add(s)
        stack.extend(sorted(reverse.get(s, ())))
    return seen


# ------------------------------------------------------------- Tarjan SCC (det.)
def _strongly_connected_components(bundle: Dict) -> List[List[str]]:
    """Tarjan's algorithm, iterative, deterministic ordering.

    Returns a list of components; each component is a sorted list of states.
    Used solely to name the structure of a trap for the fault report.
    """
    nodes = sorted(_states_of(bundle))
    adjacency = {n: _out_targets(bundle, n) for n in nodes}
    index: Dict[str, int] = {}
    lowlink: Dict[str, int] = {}
    on_stack: Dict[str, bool] = {}
    stack: List[str] = []
    components: List[List[str]] = []
    counter = [0]

    def connect(root: str) -> None:
        work: List[Tuple[str, int]] = [(root, 0)]
        while work:
            node, child_ptr = work[-1]
            if child_ptr == 0:
                index[node] = lowlink[node] = counter[0]
                counter[0] += 1
                stack.append(node)
                on_stack[node] = True
            recurse = False
            successors = adjacency[node]
            i = child_ptr
            while i < len(successors):
                nxt = successors[i]
                if nxt not in index:
                    work[-1] = (node, i + 1)
                    work.append((nxt, 0))
                    recurse = True
                    break
                elif on_stack.get(nxt):
                    lowlink[node] = min(lowlink[node], index[nxt])
                i += 1
            if recurse:
                continue
            if lowlink[node] == index[node]:
                component: List[str] = []
                while True:
                    member = stack.pop()
                    on_stack[member] = False
                    component.append(member)
                    if member == node:
                        break
                components.append(sorted(component))
            work.pop()
            if work:
                parent = work[-1][0]
                lowlink[parent] = min(lowlink[parent], lowlink[node])

    for n in nodes:
        if n not in index:
            connect(n)
    return components


# ----------------------------------------------------------------- the gate
def validate(bundle: Dict) -> List[Finding]:
    """Return a sorted list of Findings. Empty list means the bundle passes."""
    findings: List[Finding] = []
    all_states = _states_of(bundle)
    reachable = _forward_reachable(bundle)
    terminals = set(bundle["terminal"])

    # 1. REACHABILITY — orphan states (dead code in the bundle).
    for s in sorted(all_states - reachable):
        findings.append(Finding("REACHABILITY", (s,)))

    # 2. THE LAW — any reachable, non-terminal state that cannot reach a
    #    terminal is trapped. Name the structure via SCC for the report.
    can_end = _terminal_coreachable(bundle)
    trapped = {s for s in reachable if s not in terminals and s not in can_end}
    if trapped:
        for component in _strongly_connected_components(bundle):
            inside = tuple(s for s in component if s in trapped)
            if not inside:
                continue
            if len(component) >= 2:
                findings.append(Finding("SEALED_CLUSTER", inside))
            else:
                s = component[0]
                outs = _out_targets(bundle, s)
                if not outs:
                    findings.append(Finding("DEAD_END", (s,)))
                elif all(t == s for t in outs):
                    findings.append(Finding("SELF_TRAP", (s,)))
                else:
                    findings.append(Finding("FEEDS_TRAP", (s,)))

    return findings


def is_valid(bundle: Dict) -> bool:
    return not validate(bundle)


def format_report(findings: List[Finding]) -> str:
    if not findings:
        return "PASS — no faults."
    lines = ["FAIL — {} fault(s):".format(len(findings))]
    for f in findings:
        lines.append("  {:<15} {{{}}}".format(f.code, ", ".join(f.states)))
    return "\n".join(lines)


if __name__ == "__main__":
    # minimal smoke check so the file is runnable standalone
    good = {
        "name": "smoke_good", "initial": "START", "terminal": {"DONE"},
        "transitions": [("START", "begin", "WORK"), ("WORK", "finish", "DONE")],
    }
    trap = {
        "name": "smoke_trap", "initial": "START", "terminal": {"DONE"},
        "transitions": [("START", "ok", "DONE"), ("START", "go", "A"),
                        ("A", "x", "B"), ("B", "y", "A")],
    }
    print("good bundle:", format_report(validate(good)))
    print("trap bundle:", format_report(validate(trap)))
