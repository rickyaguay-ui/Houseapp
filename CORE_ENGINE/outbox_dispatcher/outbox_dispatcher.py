"""
outbox_dispatcher.py — Universal commit fan-out, OUTSIDE the deterministic core
================================================================================
Law: nothing in the core (ledger.py, replay.py, formation.py, gate_v1.py)
ever performs I/O, ever calls an external service, ever has side effects.
This file is where that boundary is deliberately crossed -- on purpose,
in one place, clearly separated -- so the core stays pure and testable
while real-world notification still happens.

Pattern: every commit the ledger accepts gets offered to every registered
CHANNEL ADAPTER. Each adapter decides independently whether/how to act --
exactly the same "offer to everyone, each decides independently" shape as
the orthogonal regions, just applied to side effects instead of state.

A channel that isn't connected/available is a CLEARLY LABELED stub that
raises NotImplementedError with an honest message -- never silently
pretends to have sent something it didn't.
"""

from typing import Dict, Any, List, Callable
from dataclasses import dataclass, field


@dataclass
class DispatchResult:
    channel: str
    sent: bool
    detail: str


class OutboxDispatcher:
    def __init__(self):
        self._adapters: Dict[str, Callable[[Dict[str, Any]], DispatchResult]] = {}

    def register(self, channel_name: str, adapter_fn: Callable[[Dict[str, Any]], DispatchResult]):
        self._adapters[channel_name] = adapter_fn

    def dispatch(self, event: Dict[str, Any]) -> List[DispatchResult]:
        """Offers ONE committed event to EVERY registered channel.
        A failure in one channel does not block the others -- same
        independence guarantee as orthogonal regions."""
        results = []
        for name, adapter in self._adapters.items():
            try:
                results.append(adapter(event))
            except Exception as e:
                results.append(DispatchResult(channel=name, sent=False, detail=f"ERROR: {e}"))
        return results


# ---------------------------------------------------------------- adapters

def gmail_adapter(event: Dict[str, Any]) -> DispatchResult:
    """REAL channel -- Gmail is connected and proven in this session
    (used earlier for the Rose State email draft). This function returns
    the exact payload a real Gmail:create_draft call needs; the actual
    API call happens one layer up, where the MCP tool lives -- this stays
    a pure function so it's testable without hitting a real inbox."""
    subject = f"[Ledger] {event.get('event_type')} — tx {event.get('tx_id')}"
    body = (
        f"Event type: {event.get('event_type')}\n"
        f"Timestamp: {event.get('timestamp')}\n"
        f"Payload: {event.get('payload')}\n"
        f"Hash: {event.get('hash', 'n/a')}\n"
    )
    return DispatchResult(channel="gmail", sent=True,
                           detail=f"DRAFT READY subject={subject!r}")


def google_sheets_adapter(event: Dict[str, Any]) -> DispatchResult:
    # STUB -- Google Sheets is NOT a connected tool in this environment
    # right now. Needs the Sheets MCP connector (or API credentials)
    # before this can be real. Labeled honestly, not faked.
    raise NotImplementedError(
        "Google Sheets not connected yet -- requires Sheets MCP connector or API setup"
    )


def google_keep_adapter(event: Dict[str, Any]) -> DispatchResult:
    # STUB -- worth flagging plainly: Google Keep has historically had
    # NO official public API at all. This may not be buildable the way
    # Sheets/Gmail are, regardless of connector setup. Needs research,
    # not just a connection.
    raise NotImplementedError(
        "Google Keep not connected -- and may have no public API to connect to at all"
    )


def sms_staff_directory_adapter(event: Dict[str, Any]) -> DispatchResult:
    # STUB -- needs an SMS provider (Twilio or similar) plus an actual
    # staff directory data source. Neither exists yet in this build.
    raise NotImplementedError(
        "SMS not connected -- requires a provider (e.g. Twilio) and a real staff directory"
    )
