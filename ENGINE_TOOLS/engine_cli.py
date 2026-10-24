"""
engine_cli.py — THE PLUG-AND-PLAY DEV/TEST ENTRY POINT
=========================================================
This is the single consolidated front door to everything proven today:
gate_v1.py (structural validation), ledger.py (signed, hash-chained,
WAL-protected commits), replay.py (deterministic state reconstruction),
events.py (flat single-state engine), formation.py (orthogonal multi-
region engine). One file in, one running engine out.

USAGE:
    python3 engine_cli.py path/to/bundle.json

Auto-detects bundle shape:
    - Has top-level "regions" key      -> ORTHOGONAL multi-region engine
    - Has top-level "transitions" key  -> FLAT single-state engine

GATES THE BUNDLE BEFORE ANYTHING ELSE RUNS. A bundle that fails gate_v1.py
never gets an engine instance -- no exceptions, no "just this once."

Then drops into an interactive loop:
    > intake                      commit an event, see the result
    > intake {"unit":"4B"}        commit an event with a payload
    > state                       show current state
    > history                     show the full transition trail
    > verify                      check ledger hash-chain integrity
    > replay                      compare derive_state vs full_replay
    > quit

HONEST SCOPE NOTE: gate_v2_guard_contradiction.py is NOT wired in here.
It validates a DIFFERENT bundle schema (numeric metric/operator/value
guard conditions inside stages_config) that no bundle built today
actually uses -- every real bundle so far is pure event-driven, no
numeric guards. Wiring gate_v2 in is real future work, not done here,
not faked here.
"""

import sys
import os
import json
import time
import tempfile

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

os.environ.setdefault("HOUSE_LEDGER_KEY", "dev-test-key-not-for-production")

from gate_v1 import validate, is_valid, format_report
from ledger import Ledger
from replay import ReplayEngine


# ---------------------------------------------------------------- flat engine
class FlatEventRegistry:
    """Wraps a flat bundle's transition table as the pure applier fn."""
    def __init__(self, bundle: dict):
        self.transition_table = {}
        for src, evt, dst in bundle["transitions"]:
            self.transition_table[(src, evt)] = dst
        self.initial_state = bundle["initial"]
        self.terminal = set(bundle["terminal"])

    def applier(self, state, tx):
        current = state.get("state", self.initial_state) if state else self.initial_state
        history = list(state.get("history", [])) if state else []
        event_type = tx.get("event_type")
        next_state = self.transition_table.get((current, event_type))
        if next_state is None:
            return {"state": current, "history": history}
        history = history + [f"{current} --{event_type}--> {next_state}"]
        return {"state": next_state, "history": history}

    def describe(self, state):
        s = state.get("state", self.initial_state) if state else self.initial_state
        done = " (TERMINAL)" if s in self.terminal else ""
        return f"state = {s}{done}"


# ---------------------------------------------------------- orthogonal engine
class OrthogonalEventRegistry:
    """Wraps an orthogonal-region bundle as the pure applier fn. The applier
    contract is identical in shape to the flat engine -- this is what makes
    both pluggable into the same ledger/replay machinery unmodified."""
    def __init__(self, bundle: dict):
        self.region_tables = {}
        self.region_initial = {}
        self.region_terminal = {}
        for region_name, region_def in bundle["regions"].items():
            table = {}
            for src, evt, dst in region_def["transitions"]:
                table[(src, evt)] = dst
            self.region_tables[region_name] = table
            self.region_initial[region_name] = region_def["initial"]
            self.region_terminal[region_name] = set(region_def["terminal"])

    def applier(self, state, tx):
        event_type = tx.get("event_type")
        if not state or "regions" not in state:
            current = dict(self.region_initial)
        else:
            current = dict(state["regions"])
        new_regions = dict(current)
        for region_name, table in self.region_tables.items():
            here = current.get(region_name, self.region_initial[region_name])
            nxt = table.get((here, event_type))
            if nxt is not None:
                new_regions[region_name] = nxt
        return {"regions": new_regions}

    def is_complete(self, state):
        regions = state.get("regions", self.region_initial) if state else self.region_initial
        return all(regions.get(r) in term for r, term in self.region_terminal.items())

    def describe(self, state):
        regions = state.get("regions", self.region_initial) if state else self.region_initial
        lines = [f"  {r} = {s}" for r, s in regions.items()]
        complete = " ALL REGIONS COMPLETE" if self.is_complete(state) else ""
        return "regions:\n" + "\n".join(lines) + complete


# ---------------------------------------------------------------- gate runner
def gate_bundle(bundle: dict) -> bool:
    """Gates the bundle BEFORE any engine instance is created. Flat bundles
    gate once. Orthogonal bundles gate every region independently, same
    proven gate_v1.py, zero modification needed -- a region IS a flat
    bundle from the gate's point of view."""
    if "regions" in bundle:
        all_clean = True
        for region_name, region_def in bundle["regions"].items():
            flat = {"initial": region_def["initial"],
                    "terminal": region_def["terminal"],
                    "transitions": region_def["transitions"]}
            result = validate(flat)
            status = "PASS" if is_valid(flat) else "FAIL"
            print(f"  [{status}] region '{region_name}': {format_report(result)}")
            if not is_valid(flat):
                all_clean = False
        return all_clean
    else:
        result = validate(bundle)
        print(f"  {format_report(result)}")
        return is_valid(bundle)


# ---------------------------------------------------------------------- main
def main():
    if len(sys.argv) < 2:
        print("Usage: python3 engine_cli.py path/to/bundle.json")
        sys.exit(1)

    bundle_path = sys.argv[1]
    with open(bundle_path) as f:
        bundle = json.load(f)

    is_orthogonal = "regions" in bundle

    print("=" * 60)
    print(f"LOADING BUNDLE: {bundle_path}")
    print(f"SHAPE DETECTED: {'ORTHOGONAL (multi-region)' if is_orthogonal else 'FLAT (single-state)'}")
    print("=" * 60)
    print()
    print("GATE VALIDATION (before any engine instance is created):")
    clean = gate_bundle(bundle)
    print()

    if not clean:
        print("BUNDLE REJECTED BY GATE. No engine instance created. Fix the bundle and re-run.")
        sys.exit(1)

    print("Bundle is structurally sound. Spinning up the real engine.")
    print()

    tmp_dir = tempfile.mkdtemp()
    ledger_path = os.path.join(tmp_dir, "dev_ledger.json")
    ledger = Ledger(ledger_path=ledger_path)
    print(f"Ledger initialized (signed, hash-chained, WAL-protected): {ledger_path}")

    registry = OrthogonalEventRegistry(bundle) if is_orthogonal else FlatEventRegistry(bundle)
    replay = ReplayEngine(ledger, registry.applier)

    nonce = [0]
    def commit(event_type, payload=None):
        nonce[0] += 1
        ledger.commit(
            client_id="dev_cli",
            idempotency_key=f"dev-{nonce[0]}",
            event_type=event_type,
            payload=payload or {},
            timestamp=int(time.time())
        )

    print()
    print("Engine is live. Type an event_type to commit it.")
    print("Optional payload: intake {\"unit\": \"4B\"}")
    print("Commands: state | history | verify | replay | quit")
    print("-" * 60)

    while True:
        try:
            line = input("> ").strip()
        except EOFError:
            break
        if not line:
            continue
        if line == "quit":
            break
        if line == "state":
            print(" ", registry.describe(replay.derive_state()))
            continue
        if line == "history":
            st = replay.derive_state()
            if is_orthogonal:
                print("  (orthogonal engine tracks per-region state, not a single linear history)")
            else:
                for h in st.get("history", []):
                    print("  ", h)
            continue
        if line == "verify":
            print("  chain integrity:", ledger.verify_chain())
            continue
        if line == "replay":
            d = replay.derive_state()
            f = replay.full_replay({})
            print("  derive_state:", d)
            print("  full_replay :", f)
            print("  match:", d == f)
            continue

        # treat as "event_type {optional json payload}"
        parts = line.split(" ", 1)
        event_type = parts[0]
        payload = {}
        if len(parts) > 1:
            try:
                payload = json.loads(parts[1])
            except json.JSONDecodeError:
                print("  (couldn't parse payload as JSON, committing with empty payload)")
        commit(event_type, payload)
        print(" ", registry.describe(replay.derive_state()))


if __name__ == "__main__":
    main()
