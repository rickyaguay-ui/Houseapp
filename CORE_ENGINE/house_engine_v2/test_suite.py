"""
test_suite.py — Full regression suite for house_engine (Orthogonal Regions Edition)
===================================================================================
Run: python3 test_suite.py
Exit 0 = all pass. Exit 1 = failures found.
"""

import os
import sys
import time
import tempfile
import unittest
import json

# Set env key before any imports that touch Ledger
os.environ["HOUSE_LEDGER_KEY"] = "test-key-do-not-use-in-production"

# Ensure we can import from the current directory
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from gate import validate_bundle, GateResult, Fault
from state_model import StateModel
from truth_library import TruthLibrary, TruthEntry
from events import Event, RejectionResult, EventRegistry
from ledger import Ledger, ClockDriftError
from replay import ReplayEngine
from formation import RegionResolver, MultiRegionResolver


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def make_valid_bundle():
    """Returns a bundle following the new regions-based schema."""
    return {
        "regions": {
            "CORE": {
                "metrics": {"health": 100.0, "energy": 80.0},
                "initial_stage": "active",
                "stages": {
                    "active": {"transition_rules": [
                        {"condition": {"metric": "health", "operator": "<", "value": 30.0},
                         "next_stage": "critical"}
                    ]},
                    "critical": {"transition_rules": [
                        {"condition": {"metric": "health", "operator": ">=", "value": 70.0},
                         "next_stage": "active"}
                    ]},
                }
            }
        },
        "event_types": {
            "heal": {
                "mutations": [
                    {"region": "CORE", "metric": "health", "delta": 10.0}
                ]
            },
            "damage": {
                "mutations": [
                    {"region": "CORE", "metric": "health", "delta": -10.0}
                ]
            },
        },
        "truth_entries": {
            "T001": {"region": "CORE", "stage": "active", "categories": ["REST"],
                     "reference": "Ref 1:1", "text": "Truth one."},
        },
        "vocabulary": {
            "heal": ["fix", "restore"],
            "damage": ["hurt", "wound"],
        },
    }


def tmp_ledger():
    """Create a Ledger backed by a temp file, cleaned up after test."""
    f = tempfile.NamedTemporaryFile(suffix=".json", delete=False)
    f.close()
    os.unlink(f.name)   # Remove so Ledger initializes fresh
    return Ledger(ledger_path=f.name), f.name


# ---------------------------------------------------------------------------
# Gate tests
# ---------------------------------------------------------------------------

class TestGate(unittest.TestCase):

    def test_valid_bundle_passes(self):
        r = validate_bundle(make_valid_bundle())
        self.assertTrue(r.ok, r.report())

    def test_missing_key_fails(self):
        b = make_valid_bundle()
        del b["vocabulary"]
        r = validate_bundle(b)
        self.assertFalse(r.ok)
        self.assertTrue(any(f.code == "MISSING_KEY" for f in r.faults))

    def test_wrong_type_fails(self):
        b = make_valid_bundle()
        b["regions"] = "not_a_dict"
        r = validate_bundle(b)
        self.assertFalse(r.ok)
        self.assertTrue(any(f.code == "WRONG_TYPE" for f in r.faults))

    def test_empty_metrics_fails(self):
        b = make_valid_bundle()
        b["regions"]["CORE"]["metrics"] = {}
        r = validate_bundle(b)
        self.assertFalse(r.ok)
        self.assertTrue(any(f.code == "EMPTY_METRICS" for f in r.faults))

    def test_metric_collision_fails(self):
        b = make_valid_bundle()
        b["regions"]["SECOND"] = {
            "metrics": {"health": 50.0}, # health already in CORE
            "initial_stage": "start",
            "stages": {"start": {"transition_rules": []}}
        }
        r = validate_bundle(b)
        self.assertFalse(r.ok)
        self.assertTrue(any(f.code == "METRIC_COLLISION" for f in r.faults))

    def test_invalid_operator_fails(self):
        b = make_valid_bundle()
        b["regions"]["CORE"]["stages"]["active"]["transition_rules"][0]["condition"]["operator"] = "xor"
        r = validate_bundle(b)
        self.assertFalse(r.ok)
        self.assertTrue(any(f.code == "INVALID_OPERATOR" for f in r.faults))

    def test_bad_stage_ref_fails(self):
        b = make_valid_bundle()
        b["regions"]["CORE"]["stages"]["active"]["transition_rules"][0]["next_stage"] = "nonexistent"
        r = validate_bundle(b)
        self.assertFalse(r.ok)
        self.assertTrue(any(f.code == "BAD_STAGE_REF" for f in r.faults))


# ---------------------------------------------------------------------------
# StateModel tests
# ---------------------------------------------------------------------------

class TestStateModel(unittest.TestCase):

    def test_initial_state(self):
        s = StateModel({"h": 100.0, "m": 50.0})
        self.assertEqual(s.get_state(), {"h": 100.0, "m": 50.0})

    def test_update_valid(self):
        s = StateModel({"h": 100.0})
        s.update_metric("h", 80.0)
        self.assertEqual(s.get_metric("h"), 80.0)

    def test_clamp_above_max(self):
        s = StateModel({"h": 120.0})
        s.clamp("h", 0.0, 100.0)
        self.assertEqual(s.get_metric("h"), 100.0)


# ---------------------------------------------------------------------------
# Ledger tests
# ---------------------------------------------------------------------------

class TestLedger(unittest.TestCase):

    def setUp(self):
        self.ledger, self.ledger_path = tmp_ledger()

    def tearDown(self):
        for path in [self.ledger_path, self.ledger_path + ".wal"]:
            if os.path.exists(path):
                os.unlink(path)

    def test_commit_returns_ok(self):
        result = self.ledger.commit("dev1", "key-1", "heal", {}, int(time.time()))
        self.assertEqual(result["status"], "OK")
        self.assertEqual(result["tx_id"], 1)

    def test_idempotency_duplicate_returns_duplicate_ok(self):
        now = int(time.time())
        self.ledger.commit("dev1", "key-1", "heal", {}, now)
        result = self.ledger.commit("dev1", "key-1", "heal", {}, now)
        self.assertEqual(result["status"], "DUPLICATE_OK")
        self.assertEqual(result["tx_id"], 1)
        self.assertEqual(self.ledger.transaction_count(), 1)

    def test_chain_verifies_after_commits(self):
        now = int(time.time())
        self.ledger.commit("dev1", "k1", "heal",   {}, now)
        self.ledger.commit("dev1", "k2", "damage", {}, now)
        self.assertTrue(self.ledger.verify_chain())


# ---------------------------------------------------------------------------
# Formation tests
# ---------------------------------------------------------------------------

class TestFormation(unittest.TestCase):

    def test_region_resolver_transition(self):
        stages = {
            "active": {"transition_rules": [
                {"condition": {"metric": "h", "operator": "<", "value": 30.0},
                 "next_stage": "critical"},
            ]},
            "critical": {"transition_rules": []}
        }
        res = RegionResolver("CORE", stages, "active")
        self.assertEqual(res.get_stage(), "active")
        
        # No transition
        res.evaluate({"h": 50.0})
        self.assertEqual(res.get_stage(), "active")
        
        # Transition fires
        res.evaluate({"h": 20.0})
        self.assertEqual(res.get_stage(), "critical")

    def test_multi_region_orchestration(self):
        bundle = make_valid_bundle()
        multi = MultiRegionResolver.from_bundle(bundle)
        
        self.assertEqual(multi.get_region_stage("CORE"), "active")
        
        # Apply mutation
        multi.apply_mutation("CORE", "health", -80.0)
        self.assertEqual(multi.get_region_metrics("CORE")["health"], 20.0)
        
        # Evaluate
        transitions = multi.evaluate_all_regions()
        self.assertEqual(transitions["CORE"], "critical")
        self.assertEqual(multi.get_region_stage("CORE"), "critical")


if __name__ == "__main__":
    unittest.main(verbosity=2)
