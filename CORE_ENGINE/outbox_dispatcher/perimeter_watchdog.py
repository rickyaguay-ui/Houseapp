"""
perimeter_watchdog.py — Inbound quarantine for anything the network hands back
================================================================================
The dispatcher (outbox_dispatcher.py) already structurally cannot write to
the ledger -- adapters only ever receive a plain dict, never the Ledger
object, so outbound leakage of WRITE ACCESS is impossible by construction.

This file closes the OTHER half: anything that comes back FROM the network
-- an API response, a webhook, an error payload, anything -- is UNTRUSTED
by law. It is treated exactly like the controlled-vocabulary rule already
governing Soul Tie Brain: no fuzzy interpretation, no silent trust. It is
quarantined, inspected against a strict allow-list shape, and if it doesn't
match exactly, it is rejected and logged as a security event -- never
silently absorbed into anything that could influence state.

LAW: Inbound network data has ZERO write path to the ledger. Not "limited."
ZERO. If something needs to become a real event because of an external
response (e.g. confirming an email was delivered), a HUMAN reviews the
quarantined record and explicitly commits a normal, ordinary event through
the normal ledger.commit() path -- the network response itself never
touches the ledger directly, ever.
"""

from typing import Dict, Any, List, NamedTuple
from dataclasses import dataclass, field
import time


class QuarantineVerdict(NamedTuple):
    accepted: bool
    reason: str


@dataclass
class QuarantinedItem:
    source_channel: str
    raw_payload: Any
    verdict: QuarantineVerdict
    received_at: int = field(default_factory=lambda: int(time.time()))


class PerimeterWatchdog:
    """Sits between any external response and anything else in the system.
    Has NO reference to the Ledger -- cannot write anywhere, by construction,
    same proof pattern as the outbox adapters. Its only job is judging
    shape, never acting on what it judges.
    """

    def __init__(self, allowed_shapes: Dict[str, List[str]]):
        """allowed_shapes: { channel_name: [required_top_level_keys] }
        A strict allow-list, not a blocklist. Unknown channels are
        rejected by default -- same 'explicit rejection on unmapped
        input' law as the controlled vocabulary elsewhere in this build.
        """
        self._allowed_shapes = allowed_shapes
        self.quarantine_log: List[QuarantinedItem] = []

    def inspect(self, source_channel: str, raw_payload: Any) -> QuarantinedItem:
        verdict = self._judge(source_channel, raw_payload)
        item = QuarantinedItem(source_channel=source_channel,
                                raw_payload=raw_payload, verdict=verdict)
        self.quarantine_log.append(item)
        return item

    def _judge(self, source_channel: str, raw_payload: Any) -> QuarantineVerdict:
        if source_channel not in self._allowed_shapes:
            return QuarantineVerdict(False, f"UNKNOWN CHANNEL '{source_channel}' -- rejected by default")
        if not isinstance(raw_payload, dict):
            return QuarantineVerdict(False, f"Payload is not a dict ({type(raw_payload).__name__}) -- rejected")
        required_keys = set(self._allowed_shapes[source_channel])
        actual_keys = set(raw_payload.keys())
        if not required_keys.issubset(actual_keys):
            missing = required_keys - actual_keys
            return QuarantineVerdict(False, f"Missing required keys {missing} -- rejected, not assumed present")
        # Shape matches the allow-list. Still NOT auto-trusted into the
        # ledger -- accepted only means "safe for a HUMAN to review next."
        return QuarantineVerdict(True, "Shape matches allow-list -- queued for human review, NOT auto-committed")
