"""
stress_test.py — High-intensity stress and edge-case testing for house_engine
=============================================================================
Tests integrity, concurrency (simulation), large datasets, and corruption recovery.
"""

import os
import time
import json
import random
import unittest
import tempfile
import hmac
import hashlib
from typing import Dict, Any

# Set env key
os.environ["HOUSE_LEDGER_KEY"] = "stress-test-secret-key-12345"

from ledger import Ledger, ClockDriftError, GENESIS_HASH
from replay import ReplayEngine
from formation import MultiRegionResolver
from gate import validate_bundle


class StressTests(unittest.TestCase):

    def setUp(self):
        self.tmp_dir = tempfile.TemporaryDirectory()
        self.ledger_path = os.path.join(self.tmp_dir.name, "stress_ledger.json")
        self.ledger = Ledger(ledger_path=self.ledger_path)

    def tearDown(self):
        self.tmp_dir.cleanup()

    def test_high_volume_commits_and_integrity(self):
        """Commit 1000 events and verify the hash chain integrity."""
        print("\n[Stress] Committing 1000 events...")
        start_time = time.time()
        for i in range(1000):
            self.ledger.commit(
                client_id="stress_bot",
                idempotency_key=f"key_{i}",
                event_type="test_event",
                payload={"val": i},
                timestamp=int(time.time())
            )
        duration = time.time() - start_time
        print(f"[Stress] 1000 commits took {duration:.2f}s ({1000/duration:.2f} tx/s)")
        
        self.assertTrue(self.ledger.verify_chain(), "Hash chain should be valid after 1000 commits")
        self.assertEqual(self.ledger.transaction_count(), 1000)

    def test_idempotency_under_load(self):
        """Verify that duplicate keys never create duplicate transactions even under rapid submission."""
        print("[Stress] Testing idempotency...")
        for _ in range(100):
            self.ledger.commit("bot", "same_key", "event", {}, int(time.time()))
        
        self.assertEqual(self.ledger.transaction_count(), 1)

    def test_hash_chain_corruption_detection(self):
        """Manually corrupt a transaction and ensure verify_chain() catches it."""
        print("[Stress] Testing corruption detection...")
        self.ledger.commit("bot", "k1", "e", {}, int(time.time()))
        self.ledger.commit("bot", "k2", "e", {}, int(time.time()))
        self.ledger.commit("bot", "k3", "e", {}, int(time.time()))
        
        # Load data, corrupt one middle transaction payload
        with open(self.ledger_path, "r") as f:
            data = json.load(f)
        
        data["transactions"][1]["payload"]["corrupted"] = True
        
        with open(self.ledger_path, "w") as f:
            json.dump(data, f)
            
        # Re-init ledger to load corrupted data
        corrupted_ledger = Ledger(ledger_path=self.ledger_path)
        self.assertFalse(corrupted_ledger.verify_chain(), "Ledger should detect payload corruption")

    def test_hmac_signature_failure(self):
        """Change the secret key and ensure all existing signatures fail verification."""
        print("[Stress] Testing HMAC key change detection...")
        self.ledger.commit("bot", "k1", "e", {}, int(time.time()))
        
        # Change key
        os.environ["HOUSE_LEDGER_KEY"] = "WRONG_KEY"
        wrong_ledger = Ledger(ledger_path=self.ledger_path)
        self.assertFalse(wrong_ledger.verify_chain(), "Verification should fail with wrong HMAC key")
        
        # Restore key
        os.environ["HOUSE_LEDGER_KEY"] = "stress-test-secret-key-12345"

    def test_replay_determinism(self):
        """Verify that replaying events always results in the exact same state."""
        print("[Stress] Testing replay determinism...")
        
        def applier(state, tx):
            s = dict(state)
            val = tx["payload"].get("val", 0)
            s["total"] = s.get("total", 0) + val
            return s

        for i in range(100):
            self.ledger.commit("bot", f"k{i}", "add", {"val": 10}, int(time.time()))
            
        engine = ReplayEngine(self.ledger, applier)
        state1 = engine.derive_state()
        state2 = engine.full_replay({})
        
        self.assertEqual(state1, state2)
        self.assertEqual(state1["total"], 1000)

    def test_large_payload_handling(self):
        """Test the engine with large event payloads (1MB)."""
        print("[Stress] Testing large payload (1MB)...")
        large_data = "x" * (1024 * 1024)
        self.ledger.commit("bot", "large_k", "large_e", {"data": large_data}, int(time.time()))
        self.assertTrue(self.ledger.verify_chain())

    def test_max_clock_drift_edge_cases(self):
        """Test boundary conditions for clock drift."""
        now = int(time.time())
        # Exactly at limit (300s)
        self.ledger.commit("bot", "limit_ok", "e", {}, now - 300)
        
        # Just over limit (301s)
        with self.assertRaises(ClockDriftError):
            self.ledger.commit("bot", "limit_fail", "e", {}, now - 301)

if __name__ == "__main__":
    unittest.main(verbosity=2)
