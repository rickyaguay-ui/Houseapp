"""
replay.py — Checkpoint + Delta Replay Engine
==============================================
Derives current state from ledger without replaying every event from genesis.

Algorithm:
  1. Load checkpoint: { last_tx_id, state }
  2. Get delta: all transactions with tx_id > last_tx_id
  3. For each tx in order: state = event_applier(state, tx)
  4. Return final state dict

event_applier contract (pure function):
  fn(state: dict, tx: dict) -> dict
  - Receives a copy of current state and the raw transaction dict
  - Returns a NEW state dict (does not mutate the input)
  - Unknown event types should return state unchanged (no crash)
"""

from typing import Callable, Dict, Any
from ledger import Ledger


class ReplayEngine:
    def __init__(self, ledger: Ledger,
                 event_applier: Callable[[Dict[str, Any], Dict[str, Any]], Dict[str, Any]]):
        """
        ledger:         Ledger instance (already initialized)
        event_applier:  pure fn(state_dict, tx_dict) -> new_state_dict
        """
        self._ledger = ledger
        self._applier = event_applier

    def derive_state(self) -> Dict[str, Any]:
        """
        Primary entry point.
        Loads checkpoint state, applies delta transactions, returns current state.
        """
        checkpoint = self._ledger.get_checkpoint()
        # Ensure we always start from a valid state, even if checkpoint only has an ID
        state = dict(checkpoint.get("state") if checkpoint.get("state") is not None else {})
        since_tx_id = checkpoint.get("last_tx_id", 0)
        
        # If the checkpoint has a last_tx_id but NO state, we MUST replay from genesis 
        # to ensure correctness, or we'd be applying deltas to an empty state.
        if since_tx_id > 0 and not checkpoint.get("state"):
            return self.full_replay({})
            
        delta = self._ledger.get_delta(since_tx_id)

        for raw_tx in delta:
            state = self._applier(dict(state), raw_tx)

        return state

    def full_replay(self, initial_state: Dict[str, Any]) -> Dict[str, Any]:
        """
        Replay ALL transactions from genesis.
        Use for verification and chain integrity checks only — not for normal operation.
        """
        state = dict(initial_state)
        for raw_tx in self._ledger.get_transactions():
            state = self._applier(dict(state), raw_tx)
        return state

    def rebuild_checkpoint(self, initial_state: Dict[str, Any]):
        """
        Full replay then write a fresh checkpoint at the latest tx.
        Call after detecting chain corruption or manual recovery.
        """
        txns = self._ledger.get_transactions()
        if not txns:
            return

        state = dict(initial_state)
        for raw_tx in txns:
            state = self._applier(dict(state), raw_tx)

        last_tx_id = txns[-1]["tx_id"]
        self._ledger.update_checkpoint(last_tx_id, state)
