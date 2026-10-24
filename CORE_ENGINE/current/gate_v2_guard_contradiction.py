"""
gate_v2_guard_contradiction.py — Logical guard contradiction detector
Closes the documented Gate-1 open gap: "two transition guards that can both
be true by different logic." gate_v1.py catches STRUCTURAL nondeterminism
(unreachable/trapped states); this catches SEMANTIC nondeterminism (two
transitions on the same metric whose conditions can be simultaneously true).

STATUS: Fixed and regression-tested this session (8/8 cases pass).

THE BUG THIS SESSION FOUND AND FIXED
    The original draft approximated `!=` as the full real line (-inf, inf)
    — "overlaps with everything." That is wrong for the most natural case:
    x == 5  and  x != 5  are mutually exclusive BY DEFINITION, but the
    blanket approximation flagged them as a contradiction (false positive).
    A false positive here is worse than a miss: it teaches the user to
    distrust every GUARD_CONTRADICTION finding, which undermines the
    tool's credibility for regulated-industry use.

THE FIX
    != is now handled as an explicit special case computed from the actual
    logical relationship, not a blanket approximation:
      - != vs ==  : overlap unless they pin the exact same point
      - != vs !=  : always overlap (removing one point from an infinite
                    domain twice still leaves room) over continuous metrics
      - != vs bounded (<,<=,>,>=) : still overlaps; removing a single
                    point can't empty an infinite range over continuous
                    metrics
    All four bounded comparison operators (<, <=, >, >=) were independently
    verified correct, including exact boundary-touch cases, BEFORE this fix
    — that logic is untouched.
"""

from collections import defaultdict
from typing import Dict, List, NamedTuple, Tuple


class Fault(NamedTuple):
    code: str
    message: str
    location: Tuple[str, str]


def get_bounds(op: str, val):
    """Convert a comparison operator into an (low, high, inc_low, inc_high)
    interval. Only used for the non-!= operators; != is handled separately
    in c1_overlaps_c2 because it cannot be expressed as a single interval.
    """
    inf = float('inf')
    if op == "<":   return (-inf, val, False, False)
    if op == "<=":  return (-inf, val, False, True)
    if op == ">":   return (val, inf, False, False)
    if op == ">=":  return (val, inf, True, False)
    if op == "==":  return (val, val, True, True)
    return (-inf, inf, False, False)  # should not be reached for "!="


def c1_overlaps_c2(c1: dict, c2: dict) -> bool:
    """True if there exists some value of the metric that satisfies both
    conditions simultaneously — i.e., the two guards are nondeterministic
    against each other. Pure function, no I/O, deterministic.
    """
    op1, v1 = c1["operator"], c1["value"]
    op2, v2 = c2["operator"], c2["value"]

    # --- != is handled explicitly, never approximated ------------------
    if op1 == "!=" or op2 == "!=":
        if op1 == "!=" and op2 == "!=":
            # Removing two single points from an infinite continuous
            # domain always leaves overlap.
            return True
        ne_val = v1 if op1 == "!=" else v2
        other_op, other_val = (op2, v2) if op1 == "!=" else (op1, v1)
        if other_op == "==":
            # The only way these conflict is if they pin the same point.
            return other_val != ne_val
        # other_op in {<,<=,>,>=}: infinite range minus one point still
        # overlaps over continuous metrics.
        return True

    # --- == vs == shortcut ----------------------------------------------
    if op1 == "==" and op2 == "==":
        return v1 == v2

    # --- bounded-interval overlap for <,<=,>,>=,== -----------------------
    l1, h1, inc_l1, inc_h1 = get_bounds(op1, v1)
    l2, h2, inc_l2, inc_h2 = get_bounds(op2, v2)

    max_low = max(l1, l2)
    min_high = min(h1, h2)

    if max_low < min_high:
        return True
    if max_low == min_high:
        # Touching boundary: overlap only if both intervals actually
        # include that exact point.
        inc1 = inc_l1 if max_low == l1 else inc_l2
        inc2 = inc_h1 if min_high == h1 else inc_h2
        return inc1 and inc2
    return False


def check_logical_contradictions(stages_config: dict) -> List[Fault]:
    """Scans rules within each stage to ensure no two transitions can
    evaluate to TRUE simultaneously for the same metric.
    """
    faults: List[Fault] = []

    for stage_name, stage_body in stages_config.items():
        rules = stage_body.get("transition_rules", [])
        if len(rules) < 2:
            continue

        metric_rules = defaultdict(list)
        for r in rules:
            cond = r.get("condition", {})
            m = cond.get("metric")
            if m:
                metric_rules[m].append(cond)

        for metric, conds in metric_rules.items():
            for i in range(len(conds)):
                for j in range(i + 1, len(conds)):
                    if c1_overlaps_c2(conds[i], conds[j]):
                        faults.append(
                            Fault(
                                "GUARD_CONTRADICTION",
                                f"Nondeterministic overlap detected in stage '{stage_name}' "
                                f"for metric '{metric}': {conds[i]} intersects {conds[j]}",
                                (stage_name, metric)
                            )
                        )
    return faults


# ---------------------------------------------------------------- self-test
if __name__ == "__main__":
    tests = [
        (("<", 5), (">=", 5), False, "x<5 vs x>=5 — clean partition"),
        (("<=", 5), (">=", 5), True, "x<=5 vs x>=5 — touch at 5"),
        ((">", 2), ("<", 8), True, "x>2 vs x<8 — clear overlap"),
        (("==", 5), ("!=", 5), False, "x==5 vs x!=5 — the bug case, must be False"),
        (("!=", 5), ("==", 5), False, "x!=5 vs x==5 — same case, operands swapped"),
        (("==", 5), ("!=", 7), True, "x==5 vs x!=7 — compatible, both true at x=5"),
        (("!=", 3), ("!=", 7), True, "x!=3 vs x!=7 — compatible, e.g. x=5"),
        (("!=", 5), (">", 1), True, "x!=5 vs x>1 — still infinite room"),
    ]
    all_pass = True
    for (op1, v1), (op2, v2), expected, label in tests:
        result = c1_overlaps_c2({"operator": op1, "value": v1}, {"operator": op2, "value": v2})
        status = "PASS" if result == expected else "FAIL"
        if result != expected:
            all_pass = False
        print(f"{status}  {label}  -> got {result}, expected {expected}")
    print()
    print("ALL TESTS PASS:", all_pass)
