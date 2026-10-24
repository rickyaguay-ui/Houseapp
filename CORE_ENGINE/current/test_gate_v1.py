"""
test_gate_v1.py  —  regression rig for gate_v1.py  (2026-06-16)

Consolidates this session's two exploratory scripts into one standing test.
Proves three things at once, with hard assertions (exit code != 0 on failure):

  1. The gate PASSES a good bundle and a legitimate escapable loop
     (no false positives — it does not just reject everything).
  2. The gate CATCHES every trap class: self-trap, dead-end, and the
     multi-node sealed cluster at 2, 3, and realistic scale.
  3. The trap fault NAMES the sealed structure (auditable rejection).

Run:  python3 test_gate_v1.py
Discipline: don't trust a green gate until the rig bites. This rig bites.
"""

import sys
from gate_v1 import validate, is_valid, format_report

# ----------------------------------------------------------------- fixtures
GOOD = {
    "name": "GOOD_HAPPY_PATH", "initial": "START", "terminal": {"DONE"},
    "transitions": [("START", "begin", "WORK"), ("WORK", "finish", "DONE"),
                    ("WORK", "retry", "START")],
}

# a real cycle that CAN escape to a terminal — must PASS (precision guard)
VALID_ESCAPABLE_LOOP = {
    "name": "VALID_ESCAPABLE_LOOP", "initial": "START", "terminal": {"DONE"},
    "transitions": [("START", "begin", "A"), ("A", "loop", "B"),
                    ("B", "again", "A"), ("B", "finish", "DONE")],
}

# single-node special cases of the law
CONTROL_SELF_TRAP = {
    "name": "CONTROL_SELF_TRAP", "initial": "INIT", "terminal": {"END"},
    "transitions": [("INIT", "trap", "S0"), ("S0", "adv", "S0"),
                    ("INIT", "done", "END")],
}
CONTROL_DEAD_END = {
    "name": "CONTROL_DEAD_END", "initial": "INIT", "terminal": {"END"},
    "transitions": [("INIT", "work", "B"), ("INIT", "done", "END")],
}

# multi-node sealed clusters — the gap that was closed this session
VARMINT_TWIN_LOOP = {
    "name": "VARMINT_TWIN_LOOP", "initial": "START", "terminal": {"DONE"},
    "transitions": [("START", "ok", "DONE"), ("START", "begin", "A"),
                    ("A", "ping", "B"), ("B", "pong", "A")],
}
VARMINT_TRIANGLE = {
    "name": "VARMINT_TRIANGLE", "initial": "START", "terminal": {"DONE"},
    "transitions": [("START", "ok", "DONE"), ("START", "begin", "A"),
                    ("A", "x", "B"), ("B", "y", "C"), ("C", "z", "A")],
}
# realistic: an escalated ticket that can never close
VARMINT_LOBSTER_TRAP = {
    "name": "VARMINT_LOBSTER_TRAP", "initial": "START", "terminal": {"DONE"},
    "transitions": [("START", "begin", "WORK"), ("WORK", "finish", "DONE"),
                    ("WORK", "escalate", "TRIAGE"), ("TRIAGE", "assign", "REVIEW"),
                    ("REVIEW", "revise", "TRIAGE"), ("REVIEW", "reject", "BACKLOG"),
                    ("BACKLOG", "reopen", "TRIAGE"), ("TRIAGE", "defer", "BACKLOG"),
                    ("BACKLOG", "escalate", "REVIEW")],
}

# ----------------------------------------------------------------- assertions
def codes(bundle):
    return sorted({f.code for f in validate(bundle)})

def trapped_states(bundle, code):
    out = set()
    for f in validate(bundle):
        if f.code == code:
            out.update(f.states)
    return out

failures = []
def check(label, condition):
    status = "ok  " if condition else "FAIL"
    print(f"  [{status}] {label}")
    if not condition:
        failures.append(label)

print("=" * 78)
print("REGRESSION RIG — gate_v1.py")
print("=" * 78)

print("\nmust PASS (no false positives):")
check("GOOD_HAPPY_PATH passes", is_valid(GOOD))
check("VALID_ESCAPABLE_LOOP passes (loop with an exit is permitted)",
      is_valid(VALID_ESCAPABLE_LOOP))

print("\nsingle-node trap classes caught:")
check("CONTROL_SELF_TRAP -> SELF_TRAP{S0}",
      "SELF_TRAP" in codes(CONTROL_SELF_TRAP)
      and trapped_states(CONTROL_SELF_TRAP, "SELF_TRAP") == {"S0"})
check("CONTROL_DEAD_END -> DEAD_END{B}",
      "DEAD_END" in codes(CONTROL_DEAD_END)
      and trapped_states(CONTROL_DEAD_END, "DEAD_END") == {"B"})

print("\nmulti-node sealed clusters caught AND named:")
check("VARMINT_TWIN_LOOP -> SEALED_CLUSTER{A,B}",
      trapped_states(VARMINT_TWIN_LOOP, "SEALED_CLUSTER") == {"A", "B"})
check("VARMINT_TRIANGLE -> SEALED_CLUSTER{A,B,C}",
      trapped_states(VARMINT_TRIANGLE, "SEALED_CLUSTER") == {"A", "B", "C"})
check("VARMINT_LOBSTER_TRAP -> SEALED_CLUSTER{BACKLOG,REVIEW,TRIAGE}",
      trapped_states(VARMINT_LOBSTER_TRAP, "SEALED_CLUSTER")
      == {"BACKLOG", "REVIEW", "TRIAGE"})

print("\n" + "-" * 78)
if failures:
    print(f"RESULT: {len(failures)} FAILURE(S) — {failures}")
    print("=" * 78)
    sys.exit(1)
print("RESULT: ALL CHECKS PASS — gate_v1 closes self-trap, dead-end, and the")
print("        multi-node SCC trap, and rejects no legitimate bundle.")
print("=" * 78)
