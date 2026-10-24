# THE PLUG-AND-PLAY ENTRY POINT — engine_cli.py

This is the consolidated front door. Everything verified separately today
-- gate_v1.py, the real ledger, replay, flat events, orthogonal regions --
now lives behind ONE command.

## Run it

```
python3 engine_cli.py path/to/bundle.json
```

Auto-detects bundle shape (flat vs orthogonal "regions" key). Gates it
BEFORE creating any engine instance -- a bundle that fails the gate gets
rejected outright, no engine spins up, no exceptions made.

## Proven live this session, both shapes

**Flat:** maintenance_bundle_v5_failsafe.json -- ran the full ambiguous
-> overdue -> level 2 -> overdue -> failsafe chain interactively, command
by command. Chain integrity true. derive_state matches full_replay exactly.

**Orthogonal:** orthogonal_test_bundle.json -- ran physical_work and
paperwork as genuinely independent regions through the SAME entry point,
zero code changes needed between bundle types. Watched physical work
finish while paperwork was still pending, confirmed NOT complete until
both regions independently finished.

## Commands once it's running

```
<event_type>                  commit an event
<event_type> {"key":"value"}  commit an event with a payload
state                         show current state (flat or per-region)
history                       full transition trail (flat bundles only)
verify                        check ledger hash-chain integrity
replay                        compare derive_state vs full_replay, confirm match
quit                          exit
```

## Honest scope note

gate_v2_guard_contradiction.py is NOT wired into this entry point. It
validates a different bundle schema (numeric metric/operator/value guard
conditions) that no bundle built today actually uses -- every real bundle
so far is pure event-driven. That's real future work when numeric guards
get added to a bundle, not done here, not faked here.

The outbox dispatcher and perimeter watchdog (CORE_ENGINE/outbox_dispatcher/)
are not wired into this CLI either -- they're proven separately. Wiring
commit-time dispatch into this entry point is the natural next step once
you're ready to test notification fan-out live alongside state changes.
