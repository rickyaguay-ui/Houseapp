# house_engine_v2 — Verification Status

## Verified — actually executed, not self-reported

`stress_test.py` run directly: **7/7 PASS, exit code 0.**

- test_high_volume_commits_and_integrity — 1000 commits, 128.54 tx/s, hash chain held
- test_idempotency_under_load — duplicate key correctly rejected, single record
- test_hash_chain_corruption_detection — verify_chain() correctly caught manual corruption
- test_hmac_signature_failure — wrong key correctly failed verification
- test_large_payload_handling — 1MB payload committed successfully
- test_max_clock_drift_edge_cases — exact boundary (300s accepted, 301s rejected)
- **test_replay_determinism — PASS.** This is the bug the prior session reported as
  failed-then-fixed (derive_state() returning empty state on checkpoint-without-snapshot,
  causing divergence from full_replay()). Independently re-run here, fix confirmed real.

To make this runnable, two dead-import stubs were added (formation.py, gate.py) —
confirmed via grep that stress_test.py imports MultiRegionResolver and validate_bundle
but never calls either. Both stub files say so in their own header comments. No test
logic depends on them; they exist only so the module-level import line doesn't crash.

## NOT verified — needs real files, not stubs

`test_suite.py` could not be run. It depends on `state_model.py`, `truth_library.py`,
and `events.py` — none of which were uploaded. Unlike formation/gate, StateModel is
used 6 times in actual test bodies — this is substantive dependency, not dead weight.
Stubbing these would mean testing invented logic, not the real engine, and reporting
that as "verified" would be a false-confidence result — the exact failure mode this
build has been working to eliminate elsewhere (the gate_v2 != bug, the \( \) markdown
corruption). Declined to fake it.

**To complete verification:** upload state_model.py, truth_library.py, events.py raw.
Then test_suite.py can be run for real, and a full file (gate.py, formation.py) can
replace these stubs.

## Files in this folder

- ledger.py, replay.py — real, verified via stress_test.py
- stress_test.py — real, 7/7 pass confirmed
- test_suite.py — real file, present, NOT yet runnable (missing dependencies)
- __init__.py — real, package surface; also references the same missing modules
