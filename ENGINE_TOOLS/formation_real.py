"""
formation.py — Orthogonal Region Resolver (REAL implementation, not a stub)
============================================================================
Standard Harel-statechart orthogonal-region semantics:
  - One instance holds MULTIPLE independent regions simultaneously.
  - Each region has its own current state, scoped entirely to itself.
  - An event is offered to EVERY region at once.
  - Each region independently checks: does (my_state, event) match one of
    MY transitions? If yes, that region moves. If no, that region is
    untouched -- same "unmapped event = unchanged" law as the flat engine,
    just applied per-region instead of globally.
  - This is the same pure-function, no-I/O, deterministic discipline as
    every other piece of this engine. No region knows about any other
    region's internals.
"""

import json
from typing import Dict, Any, List, Tuple


class MultiRegionResolver:
    def __init__(self, bundle_path: str):
        with open(bundle_path) as f:
            self.bundle = json.load(f)
        # bundle["regions"] = { region_name: {initial, terminal, transitions} }
        self.region_tables: Dict[str, Dict[Tuple[str, str], str]] = {}
        self.region_initial: Dict[str, str] = {}
        self.region_terminal: Dict[str, List[str]] = {}
        for region_name, region_def in self.bundle["regions"].items():
            table = {}
            for src, evt, dst in region_def["transitions"]:
                table[(src, evt)] = dst
            self.region_tables[region_name] = table
            self.region_initial[region_name] = region_def["initial"]
            self.region_terminal[region_name] = region_def["terminal"]

    def initial_state(self) -> Dict[str, str]:
        """The starting cross-product: every region at its own initial state."""
        return dict(self.region_initial)

    def apply_event(self, state: Dict[str, str], event_type: str) -> Dict[str, str]:
        """PURE function. state is {region_name: current_state_in_that_region}.
        Offers the event to every region independently. Returns a NEW dict --
        does not mutate input. A region with no matching transition is
        returned unchanged, exactly like the flat engine's contract.
        """
        new_state = dict(state)
        for region_name, table in self.region_tables.items():
            current = state.get(region_name, self.region_initial[region_name])
            next_state = table.get((current, event_type))
            if next_state is not None:
                new_state[region_name] = next_state
        return new_state

    def is_complete(self, state: Dict[str, str]) -> bool:
        """The WHOLE instance is done only when EVERY region has independently
        reached one of ITS OWN terminal states. AND-composition, not OR."""
        return all(
            state.get(region_name) in terminals
            for region_name, terminals in self.region_terminal.items()
        )

    def region_status(self, state: Dict[str, str]) -> Dict[str, bool]:
        """Per-region completion, for visibility into WHICH region is the
        holdup when the whole isn't done yet."""
        return {
            region_name: state.get(region_name) in terminals
            for region_name, terminals in self.region_terminal.items()
        }
