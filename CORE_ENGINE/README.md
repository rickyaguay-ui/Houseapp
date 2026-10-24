# CORE ENGINE — Findings & Index

This is the domain-agnostic engine itself — the gate-validated state-machine core that The House, Make Ready Pro, and any future bundle all run on top of. Already confirmed in Mem: this engine is not domain-specific; The House (with Soul Tie as its persona layer) is one bundle riding on it, not a special case.

Unlike The House pile, this material is **one clean, single lineage** — discovery, close, canonicalize — not competing visions. Good news, and worth saying plainly: this part of the build has no drift problem.

---

## First finding: 20 uploaded files were actually 5

Every duplicate-named file checked out byte-for-byte identical via hash:

- `gate_v1.py` × 2 → 1 unique file
- `gate_v2_close.py` × 7 → 1 unique file
- `scc_trap_test.py` × 8 → 1 unique file
- `SESSION_2026-06-16_recovery-modes.md` × 2 → 1 unique file
- `make_ready_gate_v1.zip` → 1 bundle, containing 4 more files

Most likely cause: the same files got downloaded or re-presented multiple times across that engineering session and all landed in one upload batch here. Worth knowing for next time — check before re-uploading a whole folder.

---

## Second finding: verified, not just claimed

Ran both canonical files directly rather than trusting the prior session's self-report:

```
python3 gate_v1.py        → PASS on good bundle, FAIL+named on trap bundle. Exit code 0.
python3 test_gate_v1.py   → ALL CHECKS PASS (7/7). Exit code 0.
```

The gate genuinely closes self-trap, dead-end, and the multi-node sealed-cluster trap, and rejects no legitimate bundle (the escapable-loop control still passes). This is real, working, independently confirmed — not an inherited claim.

---

## The lineage — how the draft files became the canonical ones

1. **`scc_trap_test.py`** (now in `/superseded-drafts`) — the discovery script. Ran the shipped per-state gate against an independent co-reachability oracle on three adversarial "varmint" bundles. The gap surfaced as a *disagreement* between the two — same signature that originally found `STATE_SELF_TRAP`.
2. **`gate_v2_close.py`** (now in `/superseded-drafts`) — the close. Replaced three separate per-state checks with one law: *a reachable, non-terminal state that cannot reach any terminal is trapped*. Tarjan SCC analysis added only to name the trapped cluster for an auditable rejection message.
3. **`gate_v1.py`** (in `/current`) — the canonicalized version of #2: same law, full public API (`validate()`, `is_valid()`, `format_report()`), documented fault vocabulary, proper typing.
4. **`test_gate_v1.py`** (in `/current`) — consolidates `scc_trap_test.py` and the exploratory testing from #2 into one standing regression rig with hard assertions.
5. **`SESSION_2026-06-16_recovery-modes.md`** (draft) → **`SESSION_2026-06-16_v1.md`** (current) — the draft snapshot was written before canonicalization and still names `scc_trap_test.py`/`gate_v2_close.py` as the artifacts. The final version tags `gate_v1.py` as `IMPLEMENTED` and lists all four `/current` files as the repository contents. Wording was also tightened throughout — same substance, more compressed.

Nothing here was deleted. The drafts are kept exactly per the project's own storage law (below) — never overwrite, never destroy, version explicitly.

---

## The storage law (pulled directly from `INDEX.md`, already governing this folder)

Never overwrite a file — to change the gate, write `gate_v2.py` and leave `gate_v1.py` untouched. Version the snapshot per checkpoint. The index names the current version; old rows stay in the table. Download to your own machine — until a file is on disk you control, the only copy lives in a mortal chat.

---

## Open gaps — accurately carried forward, not resolved here

- **Logical guard contradiction** — the gate catches structural nondeterminism, not two transition guards both satisfiable by different logic. Last remaining Gate-1 structural gap. Deliberately not stubbed — no placeholder exists for it.
- **Drift layer (Gate 2)** — longitudinal monitoring for meaning-rot over time, as distinct from load-time structural validity. Not started. Four specific untransferred problems: can't enumerate/replay an unbounded log without checkpointing; drift has no leading-indicator "FAIL line" yet, only lagging ones; the baseline oracle can itself drift and needs its own validation discipline; fleet-wide drift across multiple bundles is only visible at reconciliation. Must stay rule-based — never a probabilistic detector.
- **Workflow modes (Meta/Build/Run) + "soul tie hard engine"** — named in this lineage but left undefined here. Both have since been resolved elsewhere in this build: the mode system was reconciled into Meta/Spec/Run/Build, and Soul Tie was confirmed as a domain/persona, not a structural engine component. Worth a one-line update to this folder's record reflecting that resolution, if you want it added.
- **Standing build-fork, still genuinely open**: model Make Ready Pro *or* AI Dev Coach as the reference statechart proving orthogonal-region depth against the current flat design. Not decided in any session so far.

---

## Folder structure

```
CORE_ENGINE/
  README.md                          this file
  current/
    gate_v1.py                       canonical validator — verified passing
    test_gate_v1.py                  canonical regression rig — verified 7/7 pass
    INDEX.md                         original map + storage law
    SESSION_2026-06-16_v1.md         final session snapshot
  superseded-drafts/
    scc_trap_test.py                 the discovery script (kept for provenance)
    gate_v2_close.py                 the close implementation (kept for provenance)
    SESSION_2026-06-16_DRAFT.md      pre-canonicalization snapshot (kept for provenance)
```
