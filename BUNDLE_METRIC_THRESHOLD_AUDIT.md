# Bundle Metric Threshold Audit
## Which Bundles Have Fabricated Thresholds Baked Into State Machine?

---

## 1. HEALTHCARE NURSING (v1)

**Transitions:** Event-driven ONLY  
**Metric Dependencies:** NONE in state machine  
**Threshold Baking:** ❌ SAFE

Structure:
```json
["STAFFING_RATIO_CALC", "ratio_meets_threshold", "RATIO_COMPLIANT"]
["STAFFING_RATIO_CALC", "ratio_below_threshold", "RATIO_DEFICIT"]
```

**Status:** Uses EVENT names, not numeric thresholds. The actual threshold (0.55, 0.60, 3.5, etc.) is metadata only. Can change `_metrics` without breaking transitions.

---

## 2. HEALTHCARE NURSING (v2 - GUARD-DRIVEN)

**Transitions:** HYBRID (event-driven + guard-driven)  
**Metric Dependencies:** YES - hardcoded in transitions  
**Threshold Baking:** ⚠️ **DANGEROUS**

Guard-driven transitions:
```json
["STAFFING_RATIO_CALC", {"metric": "staffing_ratio", "op": ">=", "value": 0.55}, "RATIO_COMPLIANT"]
["STAFFING_RATIO_CALC", {"metric": "staffing_ratio", "op": "<", "value": 0.55}, "RATIO_DEFICIT"]
```

**Problem:** The 0.55 threshold is **HARDCODED into the state machine**. If research says:
- Real threshold should be **0.60** (CA/NY standard) or **3.5 HPRD**, transitions are WRONG
- The state machine assumes if ratio >= 0.55, you go to RATIO_COMPLIANT
- If that's false, reachability might break

**Verification Status:** 0.55 is from **federal study (repealed)**, NOT 2025-2026 verified source

**Action Required:** REBUILD with verified threshold before adding this to production code

---

## 3. PROPERTY TURNOVER (v1)

**Transitions:** Event-driven ONLY  
**Metric Dependencies:** NONE in state machine  
**Threshold Baking:** ❌ SAFE

Structure:
```json
["UNIT_VACANT_START", "inspect_unit", "INSPECTION_SCHEDULED"]
["INSPECTION_SCHEDULED", "inspection_complete", "REPAIRS_ASSESSMENT"]
[...all event names...]
```

**Status:** No metric thresholds in transitions. Metadata (_metrics block) can change freely.

---

## 4. FACILITY MAINTENANCE (v1)

**Transitions:** Event-driven ONLY  
**Metric Dependencies:** NONE in state machine  
**Threshold Baking:** ❌ SAFE

Structure:
```json
["MAINTENANCE_REQUEST", "schedule_work", "SCHEDULED"]
["SCHEDULED", "work_complete", "VERIFICATION"]
[...all event names...]
```

**Status:** Event-driven. No thresholds baked in. Safe to update metrics.

**Note:** Bundle references "92% PM compliance" in metadata, but this is NOT in transitions. However, **92% is NOT formally published by IFMA**—this is vendor consensus only.

---

## 5. LOGISTICS/SUPPLY CHAIN (v1)

**Transitions:** Event-driven ONLY  
**Metric Dependencies:** NONE in state machine  
**Threshold Baking:** ❌ SAFE

Structure:
```json
["ORDER_PLACED", "confirm", "CONFIRMED"]
["CONFIRMED", "ship", "IN_TRANSIT"]
["IN_TRANSIT", "deliver", "DELIVERED"]
[...all event names...]
```

**Status:** Pure event-driven. Transitions are structural flow, not threshold-dependent.

**Note:** Metadata mentions OTIF 95-98%, Walmart penalties, etc., but these are NOT baked into state machine paths.

---

## 6. CONSTRUCTION SAFETY (v1)

**Transitions:** Event-driven ONLY  
**Metric Dependencies:** NONE in state machine  
**Threshold Baking:** ❌ SAFE

Structure:
```json
["PHASE_START", "safety_review_complete", "WORK_AUTHORIZATION"]
["WORK_AUTHORIZATION", "work_start", "ACTIVE_WORK"]
["ACTIVE_WORK", "incident_report", "INCIDENT_LOG"]
[...all event names...]
```

**Status:** Event-driven. TRIR 2.2, OSHA penalties, training effectiveness are metadata, not baked into transitions.

---

## 7. FINANCE SOX/ICFR (v1)

**Transitions:** Event-driven ONLY  
**Metric Dependencies:** NONE in state machine  
**Threshold Baking:** ❌ SAFE

Structure:
```json
["FISCAL_YEAR_START", "design_controls", "CONTROL_DESIGN"]
["CONTROL_DESIGN", "test_controls", "TESTING"]
["TESTING", "remediate_issues", "REMEDIATION"]
["REMEDIATION", "file_opinion", "OPINION_FILED"]
[...all event names...]
```

**Status:** Event-driven. Material weakness thresholds are metadata ("no quantitative threshold"), not baked in.

---

## SUMMARY

| Bundle | Version | Type | Metric Baking | Risk | Action |
|--------|---------|------|---------------|------|--------|
| Healthcare Nursing | v1 | Event-driven | None | ✅ SAFE | Can update metrics freely |
| Healthcare Nursing | v2 | Guard-driven | **0.55 HPRD** hardcoded | ⚠️ **DANGEROUS** | **REBUILD with verified threshold** |
| Property Turnover | v1 | Event-driven | None | ✅ SAFE | Can update metrics freely |
| Facility Maintenance | v1 | Event-driven | None* | ✅ SAFE | Can update metrics freely |
| Logistics | v1 | Event-driven | None | ✅ SAFE | Can update metrics freely |
| Construction Safety | v1 | Event-driven | None | ✅ SAFE | Can update metrics freely |
| Finance SOX/ICFR | v1 | Event-driven | None | ✅ SAFE | Can update metrics freely |

**Key Findings:**
- **Only healthcare_nursing_v2.json has thresholds baked into state machine**
- **All v1 bundles are event-driven and safe**
- **The 0.55 HPRD threshold in healthcare_nursing_v2 is NOT verified from 2025-2026 source**

---

## RECOMMENDATION

**DO NOT COMMIT healthcare_nursing_v2.json until the 0.55 threshold is verified**

Options:
1. **Delete healthcare_nursing_v2.json entirely** (remove guard-driven version)
2. **Rebuild with verified threshold** (research says 0.55 is federal, repealed; CA/NY use 3.5 HPRD)
3. **Convert back to event-driven** (safer, simpler, matches v1 pattern)

All other bundles (v1 event-driven) are safe to keep and update metrics without rebuilding.

---

**Critical Question:** Should we use guard-driven transitions at all if we can't verify the threshold values?
