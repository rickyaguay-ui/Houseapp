"""
house_engine — Universal Deterministic Engine Core
===================================================
Public API surface. Internal modules stay internal.
Import from here, not from submodules directly.
"""

from .gate import validate_bundle, validate_and_raise, GateResult, GateRejectionError, Fault
from .state_model import StateModel
from .truth_library import TruthLibrary, TruthEntry
from .events import Event, RejectionResult, EventRegistry
from .ledger import Ledger, LedgerEntry, ClockDriftError
from .replay import ReplayEngine
from .formation import RegionResolver, MultiRegionResolver

__all__ = [
    # Gate
    "validate_bundle",
    "validate_and_raise",
    "GateResult",
    "GateRejectionError",
    "Fault",
    # State
    "StateModel",
    # Truth
    "TruthLibrary",
    "TruthEntry",
    # Events
    "Event",
    "RejectionResult",
    "EventRegistry",
    # Ledger
    "Ledger",
    "LedgerEntry",
    "ClockDriftError",
    # Replay
    "ReplayEngine",
    # Formation
    "RegionResolver",
    "MultiRegionResolver",
]
