# Verified Bundles Build Roadmap
## Event-Sourced Orthogonal Regions with 2025-2026 Data Only

---

## Architecture

**Protected Core (DO NOT MODIFY):**
- `CORE_ENGINE/current/gate_v1.py` — canonical validator
- `CORE_ENGINE/current/test_gate_v1.py` — regression suite
- `CORE_ENGINE/orthogonal_regions/formation.py` — multi-region resolver

**Bundle Pattern (Thornigal Event-Sourced Shape):**
```json
{
  "name": "bundle_name",
  "version": "1.0",
  "description": "Human-readable purpose",
  "regions": {
    "region_name": {
      "initial": "START_STATE",
      "terminal": ["END_STATE_A", "END_STATE_B"],
      "transitions": [
        ["FROM_STATE", "event_name", "TO_STATE"],
        ...
      ]
    }
  },
  "_metadata": {
    "verified_sources": [
      {"metric": "name", "value": "value", "source": "URL or authority", "year": 2025 or 2026}
    ],
    "ledger_enabled": true,
    "region_count": 1,
    "last_verified": "2026-10-24"
  }
}
```

---

## Build Order (ONE AT A TIME)

### Bundle 1: CONSTRUCTION SAFETY (PRIORITY)
**Why first:** TRIR and OSHA penalties are time-sensitive, well-documented in 2025-2026 sources

**Sources to verify:**
- BLS 2024 TRIR: 2.2 for construction (site: bls.gov)
- OSHA 2025-2026 penalties: serious $16,550, willful $165,514 (site: osha.gov)
- OSHA 2026 update: No increase this year (CPI data lapse)
- State OSHA plans: 22 states with approved plans (site: osha.gov/stateplans)

**State Machine (Events only, no guards):**
- PHASE_START → safety_review → WORK_AUTHORIZATION → work_start → ACTIVE_WORK
- ACTIVE_WORK → incident_report → INCIDENT_LOG
- ACTIVE_WORK → phase_complete → PHASE_CLOSURE OR PHASE_CLOSED_OUT
- (2 regions? physical_work + safety_compliance?)

**Fact-Check Checklist:**
- [ ] Every metric has 2025-2026 source URL
- [ ] TRIR 2.2 from BLS — verify site
- [ ] OSHA penalties from OSHA.gov memo — verify date
- [ ] State variations documented — verify 22 states list
- [ ] Bundle passes gate_v1.py
- [ ] No guard-driven transitions (event-only)
- [ ] Metadata block has every source

**Output:**
- `VERIFIED_BUNDLES/construction_safety_v1.json`
- `VERIFIED_BUNDLES/construction_safety_SOURCES.md` (cited sources)
- `VERIFIED_BUNDLES/construction_safety_VALIDATION_REPORT.txt` (gate output)

---

### Bundle 2: FINANCE SOX/ICFR (SECOND)
**Why:** No quantitative thresholds (metadata only), lower risk of threshold fabrication

**Sources to verify:**
- PCAOB AS 2201: Material weakness definition (site: pcaobus.org)
- SEC Regulation S-X: Materiality standard (site: sec.gov)
- PCAOB 2026 update: AS 2201 amendments effective Dec 15, 2026 (site: pcaobus.org)
- Accelerated filer threshold: $75M-$700M public float (site: sec.gov)

**Fact-Check:** Similar to construction

---

### Bundles 3-6: HOLD
**healthcare_nursing, property_turnover, facility_maintenance, logistics** — require deeper industry data research. Build after establishing pattern with 1-2.

---

## Verification Protocol

For EACH bundle:

1. **Source Check:** Every metric has a URL from 2025-2026, not estimates or "industry consensus"
2. **Gate Validation:** `python3 CORE_ENGINE/current/gate_v1.py` exits code 0, "PASS"
3. **Event-Sourced Shape:** Uses orthogonal regions pattern, event-driven transitions only
4. **Metadata Block:** `_metadata.verified_sources[]` lists every number's source and year
5. **Fact-Check Report:** `_VALIDATION_REPORT.txt` documents gate output + source verification

---

## Status

- [ ] Construction Safety (Bundle 1) — READY TO START
- [ ] Finance SOX (Bundle 2) — QUEUED
- [ ] Healthcare Nursing — HOLD
- [ ] Property Turnover — HOLD
- [ ] Facility Maintenance — HOLD
- [ ] Logistics — HOLD

---

## Critical Rules

**NO:** Fabricated numbers, vendor consensus without formal source, "industry best practice" without citation  
**YES:** CMS rules, BLS data, OSHA memos, SEC filings, PCAOB standards, state regulatory documents

**EVERY NUMBER IS TRACEABLE** — if it's in the bundle, it has a URL and a year.

