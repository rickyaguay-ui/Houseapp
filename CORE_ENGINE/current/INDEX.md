# INDEX — Make Ready Gate repository

**Created:** 2026-06-16  ·  **Session:** 2026-06-16_recovery-modes  ·  **Version:** v1

This folder is a self-contained, runnable record of one work session. It is
designed to outlive the chat it came from and to run with no dependence on any
AI instance. Plain text, plain Python, no hidden state, no network, no
placeholders. If you are reading this in three weeks with no other context,
everything you need is here.

---

## What was accomplished this session

A load-time **gate** that proves a state-machine *bundle* is well-formed before
any instance runs. This session found and closed a real blind spot — the
**multi-node sealed cluster** trap — and proved the close with a regression rig.

The closing insight, in one line: **a reachable, non-terminal state that cannot
reach any terminal is trapped, and a bundle with any trapped state is rejected.**
That single law replaced three older per-state checks and catches all of them
(self-trap, dead-end, multi-node cluster) as instances — while still permitting
legitimate loops that have an exit.

---

## Files (what each is, which is current)

| File | Role | Current |
|---|---|---|
| `gate_v1.py` | Canonical load-time validator. The law + Tarjan SCC namer. Public API: `validate()`, `is_valid()`, `format_report()`. | ✅ current |
| `test_gate_v1.py` | Regression rig. Proves the gate catches every trap class and rejects no legitimate bundle. Hard assertions; exit code `0` == pass. | ✅ current |
| `SESSION_2026-06-16_v1.md` | Paste-back snapshot. The breadcrumb trail — drop it at the top of a new chat to reconstruct full session state. | ✅ current |
| `INDEX.md` | This file. The map and the storage law. | ✅ current |

---

## How to run

```bash
python3 gate_v1.py          # standalone smoke check (good vs trap bundle)
python3 test_gate_v1.py     # full regression rig; exit code 0 means all pass
```

Using the gate on your own bundle:

```python
from gate_v1 import validate, format_report

bundle = {
    "name": "my_machine",
    "initial": "START",
    "terminal": {"DONE"},
    "transitions": [
        ("START", "begin", "WORK"),
        ("WORK", "finish", "DONE"),
    ],
}
print(format_report(validate(bundle)))   # "PASS — no faults." or named faults
```

**Bundle schema:** a dict with `name` (str), `initial` (state id), `terminal`
(iterable of state ids), `transitions` (list of `(from, event, to)` tuples), and
optional `states` (derived if omitted). State ids and events are strings.

**Fault vocabulary** (controlled — these are the only codes the gate emits):
`REACHABILITY`, `DEAD_END`, `SELF_TRAP`, `SEALED_CLUSTER`, `FEEDS_TRAP`.

---

## THE STORAGE LAW (read this before adding anything)

This repository is **append-only and immutable**, the same discipline the gate
enforces on bundles, applied to the files themselves. The artifacts panel in a
chat is **not** a version-control system — overwriting a file does not archive
the old one, it destroys it. So versioning here is **explicit**:

1. **Never overwrite a file.** To change the gate, write `gate_v2.py` — leave
   `gate_v1.py` untouched. Rolling back = opening the earlier file.
2. **Version the snapshot per checkpoint.** `SESSION_..._v1.md`, then `v2.md`.
   Each is a complete immutable state; never edited in place.
3. **The INDEX names the current version** of each file (the ✅ column above).
   When a new version is added, update only the table, and keep the old row.
4. **Download to your own machine.** Until a file is on disk you control, the
   only copy lives in the chat — which is mortal. Pull these down; that is the
   one backup no AI instance can make for you.

The principle: continuity is a function of what is written down and held by
**you**, never of any instance's memory. These files are breadcrumbs you drop
for the next instance — droppable by the work, held by you, re-fed at the
treeline. They make the AI replaceable on purpose.

---

## Open gaps (not yet built — tracked, not stubbed)

- **Logical guard contradiction** — the gate catches structural nondeterminism,
  not two guards both satisfiable by different logic. The next Gate-1 increment;
  it becomes `gate_v2.py`. Deliberately not placeholdered in code.
- **Drift layer (Gate 2)** — longitudinal monitoring of the *running* system for
  meaning-rot the load-time gate structurally cannot see. Must stay rule-based
  and deterministic; never a probabilistic detector. Not started.
- **Workflow modes** (Meta / Build / Run) and the **"soul tie hard engine"**
  question — named, not yet defined.

Full detail and resume instructions are in `SESSION_2026-06-16_v1.md`.
