"""
ledger.py — Append-Only HMAC-Signed Hash-Chained Event Ledger
===============================================================
Laws enforced here:
  - Append-only. No mutation of committed entries. No deletion.
  - HMAC-signed before commit. SHA-256. Canonical JSON (sort_keys=True).
  - Hash-chain: SHA-256(prev_hash + canonical_json(entry)).
  - Idempotency keys: duplicate submission returns original tx_id, no second write.
  - Checkpoint every CHECKPOINT_INTERVAL events.
  - WAL write protocol: write .wal, then os.replace() atomic commit.
  - Secrets from env only: HOUSE_LEDGER_KEY. Missing = EnvironmentError at startup.
  - Clock drift >MAX_CLOCK_DRIFT_SEC: reject. Never restamp.
  - Integer timestamps only. Float stringification is non-deterministic across platforms.
"""

import os
import hmac
import hashlib
import json
import time
from dataclasses import dataclass, field
from typing import Dict, Any, List, Optional

GENESIS_HASH = "0" * 64
CHECKPOINT_INTERVAL = 50
MAX_CLOCK_DRIFT_SEC = 300


# ---------------------------------------------------------------------------
# ClockDriftError — raised on reject, NOT on restamp
# ---------------------------------------------------------------------------

class ClockDriftError(Exception):
    def __init__(self, submitted: int, server_time: int):
        self.submitted = submitted
        self.server_time = server_time
        drift = abs(server_time - submitted)
        super().__init__(
            f"Clock drift {drift}s exceeds {MAX_CLOCK_DRIFT_SEC}s limit. "
            f"Submitted: {submitted}, Server: {server_time}. Transaction rejected."
        )


# ---------------------------------------------------------------------------
# LedgerEntry
# ---------------------------------------------------------------------------

@dataclass
class LedgerEntry:
    tx_id: int
    idempotency_key: str
    client_id: str
    event_type: str
    timestamp: int          # integer unix — float is non-deterministic in hashing
    payload: Dict[str, Any]
    prev_hash: str
    hash: str = ""
    hmac_sig: str = ""

    def canonical(self) -> str:
        """
        Canonical JSON of the signable payload.
        Excludes 'hash' and 'hmac_sig' (which are derived from this string).
        sort_keys=True ensures deterministic output across Python versions.
        """
        d = {
            "tx_id": self.tx_id,
            "idempotency_key": self.idempotency_key,
            "client_id": self.client_id,
            "event_type": self.event_type,
            "timestamp": self.timestamp,
            "payload": self.payload,
            "prev_hash": self.prev_hash,
        }
        return json.dumps(d, sort_keys=True, separators=(",", ":"))

    def to_dict(self) -> dict:
        return {
            "tx_id": self.tx_id,
            "idempotency_key": self.idempotency_key,
            "client_id": self.client_id,
            "event_type": self.event_type,
            "timestamp": self.timestamp,
            "payload": self.payload,
            "prev_hash": self.prev_hash,
            "hash": self.hash,
            "hmac_sig": self.hmac_sig,
        }

    @classmethod
    def from_dict(cls, d: dict) -> "LedgerEntry":
        return cls(
            tx_id=d["tx_id"],
            idempotency_key=d["idempotency_key"],
            client_id=d["client_id"],
            event_type=d["event_type"],
            timestamp=int(d["timestamp"]),
            payload=d["payload"],
            prev_hash=d["prev_hash"],
            hash=d.get("hash", ""),
            hmac_sig=d.get("hmac_sig", ""),
        )


# ---------------------------------------------------------------------------
# Ledger
# ---------------------------------------------------------------------------

class Ledger:
    def __init__(self, ledger_path: str = "house_ledger.json"):
        # Read secret from env — fail loud, no defaults, no hardcoding
        self._secret = os.environ.get("HOUSE_LEDGER_KEY")
        if not self._secret:
            raise EnvironmentError(
                "HOUSE_LEDGER_KEY environment variable not set. "
                "Set it before instantiating Ledger."
            )
        self._ledger_path = ledger_path
        self._wal_path = ledger_path + ".wal"
        self._data = self._load_or_initialize()

    # ------------------------------------------------------------------
    # Storage
    # ------------------------------------------------------------------

    def _load_or_initialize(self) -> dict:
        # Stale WAL means prior commit didn't complete — ledger is intact, WAL is orphan
        if os.path.exists(self._wal_path):
            os.remove(self._wal_path)

        if os.path.exists(self._ledger_path):
            with open(self._ledger_path, "r", encoding="utf-8") as f:
                return json.load(f)

        return {
            "transactions": [],
            "checkpoint": {"last_tx_id": 0, "state": {}},
            "idempotency_index": {},
        }

    def _save(self):
        """WAL write protocol: write to .wal first, then atomic os.replace()."""
        with open(self._wal_path, "w", encoding="utf-8") as f:
            json.dump(self._data, f, indent=2, sort_keys=True)
        os.replace(self._wal_path, self._ledger_path)

    # ------------------------------------------------------------------
    # Crypto
    # ------------------------------------------------------------------

    def _sign(self, canonical: str) -> str:
        return hmac.new(
            self._secret.encode("utf-8"),
            canonical.encode("utf-8"),
            hashlib.sha256,
        ).hexdigest()

    def _hash(self, prev_hash: str, canonical: str) -> str:
        return hashlib.sha256(
            (prev_hash + canonical).encode("utf-8")
        ).hexdigest()

    def _verify_entry(self, entry: LedgerEntry) -> bool:
        canonical = entry.canonical()
        expected_hash = self._hash(entry.prev_hash, canonical)
        expected_hmac = self._sign(canonical)
        return (
            hmac.compare_digest(entry.hash, expected_hash)
            and hmac.compare_digest(entry.hmac_sig, expected_hmac)
        )

    # ------------------------------------------------------------------
    # Write
    # ------------------------------------------------------------------

    def commit(
        self,
        client_id: str,
        idempotency_key: str,
        event_type: str,
        payload: Dict[str, Any],
        timestamp: int,
    ) -> dict:
        """
        Commit one event. Returns {"status": "OK"|"DUPLICATE_OK", "tx_id": int}.
        Raises ClockDriftError if drift > MAX_CLOCK_DRIFT_SEC.
        Never restamps — restamping corrupts the hash chain.
        """
        now = int(time.time())

        # Clock drift gate — reject, never restamp
        if abs(timestamp - now) > MAX_CLOCK_DRIFT_SEC:
            raise ClockDriftError(submitted=timestamp, server_time=now)

        # Idempotency gate
        idem_key = f"{client_id}:{idempotency_key}"
        if idem_key in self._data["idempotency_index"]:
            original_tx_id = self._data["idempotency_index"][idem_key]
            return {"status": "DUPLICATE_OK", "tx_id": original_tx_id}

        # Build and sign entry
        txns = self._data["transactions"]
        tx_id = len(txns) + 1
        prev_hash = txns[-1]["hash"] if txns else GENESIS_HASH

        entry = LedgerEntry(
            tx_id=tx_id,
            idempotency_key=idempotency_key,
            client_id=client_id,
            event_type=event_type,
            timestamp=timestamp,
            payload=payload,
            prev_hash=prev_hash,
        )
        canonical = entry.canonical()
        entry.hash = self._hash(prev_hash, canonical)
        entry.hmac_sig = self._sign(canonical)

        # Append and index
        self._data["transactions"].append(entry.to_dict())
        self._data["idempotency_index"][idem_key] = tx_id

        # Checkpoint every CHECKPOINT_INTERVAL events
        if tx_id % CHECKPOINT_INTERVAL == 0:
            # ONLY update the ID if we are at a boundary; 
            # state is updated separately via update_checkpoint
            self._data["checkpoint"]["last_tx_id"] = tx_id

        self._save()
        return {"status": "OK", "tx_id": tx_id}

    def update_checkpoint(self, tx_id: int, state: dict):
        """Called by replay engine to snapshot state at a checkpoint boundary."""
        self._data["checkpoint"] = {"last_tx_id": tx_id, "state": dict(state)}
        self._save()

    # ------------------------------------------------------------------
    # Read
    # ------------------------------------------------------------------

    def verify_chain(self) -> bool:
        """Replay hash chain from genesis. Returns False on first corruption."""
        txns = self._data["transactions"]
        prev_hash = GENESIS_HASH
        for raw in txns:
            entry = LedgerEntry.from_dict(raw)
            if entry.prev_hash != prev_hash:
                return False
            if not self._verify_entry(entry):
                return False
            prev_hash = entry.hash
        return True

    def get_transactions(self) -> List[dict]:
        return list(self._data["transactions"])

    def get_checkpoint(self) -> dict:
        return dict(self._data["checkpoint"])

    def get_delta(self, since_tx_id: int) -> List[dict]:
        """All transactions after since_tx_id (exclusive)."""
        return [t for t in self._data["transactions"] if t["tx_id"] > since_tx_id]

    def transaction_count(self) -> int:
        return len(self._data["transactions"])
