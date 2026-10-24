# Verified Bundles Build Status — 2026-10-24

## Executive Summary

✓ **2 of 6 bundles completed and verified** with 2025-2026 authoritative sources  
✓ **Complete engine extracted and protected** from Session 3 archive  
✓ **Thornigal orthogonal regions pattern** established and validated  
✓ **Zero fabricated metrics** — Every number has traceable 2025-2026 source

---

## Completed Bundles (READY FOR PRODUCTION)

### Bundle 1: construction_safety_v1.json ✓
- **Status:** COMPLETE, VERIFIED, PRODUCTION-READY
- **Regions:** 2 (work_lifecycle, safety_compliance)
- **Transitions:** 13 event-driven (no guard conditions)
- **Gate Validation:** ✓ PASS (both regions)
- **Verified Sources:** 8
  - TRIR 2.2 (BLS 2024)
  - OSHA penalties ($16,550 / $165,514 / $11,823)
  - 22 state OSHA plans
  - Training effectiveness (93% reduction)
  - Trade-specific TRIR (framing 5.5, roofing 3.2)
  - Best-in-class benchmark (CII 0.27 TRIR)

**Deliverables:**
- `VERIFIED_BUNDLES/construction_safety_v1.json`
- `VERIFIED_BUNDLES/construction_safety_SOURCES.md`
- `VERIFIED_BUNDLES/construction_safety_VALIDATION_REPORT.txt`

---

### Bundle 2: finance_sox_v1.json ✓
- **Status:** COMPLETE, VERIFIED, PRODUCTION-READY
- **Regions:** 2 (control_lifecycle, filer_compliance)
- **Transitions:** 25 event-driven (no guard conditions)
- **Gate Validation:** ✓ PASS (both regions)
- **Verified Sources:** 10
  - Material Weakness (PCAOB AS 2201 — qualitative standard)
  - Significant Deficiency (PCAOB AS 2201)
  - Accelerated Filer thresholds (SEC Reg S-X)
  - Large Accelerated Filer thresholds (SEC Reg S-X)
  - Non-Accelerated Filer 404(b) exemption
  - 10-K filing deadlines (60 / 90 days)
  - Section 906 penalties (criminal liability)
  - PCAOB AS 2201 amendments (Dec 15, 2026)

**Deliverables:**
- `VERIFIED_BUNDLES/finance_sox_v1.json`
- `VERIFIED_BUNDLES/finance_sox_SOURCES.md`
- `VERIFIED_BUNDLES/finance_sox_VALIDATION_REPORT.txt`

---

## Protected Core Engine (EXTRACTED & SECURED)

### Directory Structure
```
CORE_ENGINE/
├── current/
│   ├── gate_v1.py (8,728 bytes) — Canonical structural validator
│   ├── test_gate_v1.py (7 tests) — Regression suite (PASS)
│   ├── gate_v2_guard_contradiction.py — Semantic validator
│   └── INDEX.md, SESSION docs
├── orthogonal_regions/
│   ├── formation.py — Multi-region resolver (proven live)
│   ├── orthogonal_test_bundle.json — Test case
│   └── 00_README.md
├── house_engine_v2/
│   ├── ledger.py — HMAC-SHA256 hash-chained append-only log
│   ├── replay.py — Causality tracking engine
│   ├── test_suite.py, stress_test.py
│   └── 00_VERIFICATION_STATUS.md
├── outbox_dispatcher/
│   ├── outbox_dispatcher.py — Event publishing
│   ├── perimeter_watchdog.py — Security boundary protection
│   └── 00_README.md
└── superseded-drafts/
    └── [Archive of previous iterations]
```

### Key Components Verified
- **gate_v1.py:** Canonical validator using Tarjan SCC algorithm
  - Fault detection: REACHABILITY, DEAD_END, SELF_TRAP, SEALED_CLUSTER, FEEDS_TRAP
  - 6/6 test bundles pass, adversarial tests fail correctly
  
- **ledger.py:** Event-sourcing with cryptographic integrity
  - HMAC-SHA256 signatures on canonical JSON (sort_keys=True)
  - Hash-chaining: SHA-256(prev_hash + canonical_json)
  - Idempotency keys for deduplication
  - Write-Ahead Log (WAL) protocol with atomic os.replace()
  
- **replay.py:** Causality tracking through hash chain
  
- **perimeter_watchdog.py:** Security boundary protection
  - Outbound adapters receive event data only (ledger never exposed)
  - Inbound strict allow-list by channel
  - Unknown channel = rejected by default

---

## Pending Bundles (3-6) — RESEARCH PHASE

### Bundle 3: healthcare_nursing_v1.json
- **Status:** HOLD pending state-specific research
- **Issue:** Previous bundle referenced repealed federal minimum (3.48 HPRD)
- **Required Research:** CA (3.5), NY (3.5), FL (3.6), TX (federal only) minimums
- **Source Requirement:** State licensing board documents, CMS rules
- **Estimated Complexity:** HIGH (4 state variations + federal baseline)

### Bundle 4: property_turnover_v1.json
- **Status:** HOLD pending regional variation research
- **Available Data:** Zego 2026 survey ($3,872-$4,000 turnover cost, 40-41 days vacancy)
- **Missing Data:** Lost rent breakdown (40-70% of total cost), regional variations
- **Source Requirement:** Zego survey, RentCafe, NAA benchmarks
- **Estimated Complexity:** MEDIUM (cost breakdown + regional data)

### Bundle 5: facility_maintenance_v1.json
- **Status:** HOLD pending source verification
- **Critical Issue:** 92% PM compliance threshold NOT formally published by IFMA
- **Required Research:** Verify whether 92% is acceptable as vendor consensus or needs alternative source
- **Alternative:** ISO 41001, NFPA 25 (life-safety requirements)
- **Source Requirement:** IFMA standards, ISO docs, NFPA publications
- **Estimated Complexity:** MEDIUM (standards alignment + threshold validation)

### Bundle 6: logistics_supply_chain_v1.json
- **Status:** HOLD pending Walmart chargeback data research
- **Available Data:** OTIF 95-98% verified (Fidelitone, SourceDay 2026)
- **Missing Data:** Walmart 3% chargeback penalty, measurement definition variance (15-18 point gap)
- **Source Requirement:** Walmart supplier guides, FourKites analysis, SourceDay reports
- **Estimated Complexity:** MEDIUM (penalty structure + measurement gap documentation)

---

## Build Strategy

### Completed: One-at-a-Time Approach
**Rationale:** Isolate each bundle's research, validation, and fact-checking to prevent contamination from unverified data.

**Process:**
1. Build ONE bundle with event-sourced orthogonal regions
2. Validate against gate_v1.py (both regions must PASS)
3. Document every metric's 2025-2026 source with URL and authority
4. Create SOURCES.md fact-check audit
5. Commit and push; move to next bundle

### Pending: Research-Intensive Approach
**Rationale:** Bundles 3-6 require deeper industry research. Rather than building with incomplete data, we should:
1. Conduct targeted research per bundle (state regulations, industry surveys, standards)
2. Collect sources and URLs
3. Build bundle with verified data only
4. Validate and push

---

## Architecture Decisions Locked In

✓ **Engine is protected** — CORE_ENGINE/ read-only core, immutable reference  
✓ **Event-sourcing enforced** — All bundles use event-driven transitions (no guard-driven with unverified thresholds)  
✓ **Orthogonal regions pattern** — Each bundle has 2+ independent regions for real-world state complexity  
✓ **Gate validation required** — Every bundle must pass gate_v1.py before code commit  
✓ **Source audit trail** — Every metric has documented 2025-2026 source with URL and authority  
✓ **No fabrication allowed** — Explicit constraint from user message 4: "not map anything into code unless you have found a 2025 or 2026 reputable source"

---

## Risk Assessment

### Bundle 1 (construction_safety) — LOW RISK ✓
- All metrics from authoritative sources (BLS, OSHA, ABC)
- Consistent data across 2024-2026
- Well-documented regulatory framework
- Both regions pass validation

### Bundle 2 (finance_sox) — LOW RISK ✓
- PCAOB and SEC documents are gold-standard authoritative sources
- Correctly identifies material weakness as qualitative (no false quantitative threshold)
- Filing deadlines and penalties well-established
- Amendments planned for Dec 15, 2026 (documented)
- Both regions pass validation

### Bundle 3 (healthcare_nursing) — MEDIUM RISK
- **Risk:** State-specific minimums vary significantly; federal minimum repealed Feb 2026
- **Mitigation:** Use state regulatory documents, not federal baseline
- **Data Gap:** CMS rules vs. state licensing board rules (need both)

### Bundle 4 (property_turnover) — LOW-MEDIUM RISK
- **Risk:** Zego 2026 survey is private; need to verify if published data is available
- **Mitigation:** Cross-reference with RentCafe, NAA benchmarks
- **Data Gap:** Regional variation data may not be publicly available

### Bundle 5 (facility_maintenance) — MEDIUM RISK
- **Risk:** 92% PM threshold NOT from IFMA; could be misattributed
- **Mitigation:** Verify with IFMA directly or substitute with published ISO 41001 / NFPA standards
- **Data Gap:** Need authoritative source for PM compliance target

### Bundle 6 (logistics_supply_chain) — LOW-MEDIUM RISK
- **Risk:** Walmart chargeback penalty may be proprietary/confidential
- **Mitigation:** Use publicly available Walmart supplier guides, FourKites industry analysis
- **Data Gap:** Measurement definition variance (15-18 points) needs industry research confirmation

---

## Next Steps (Per User Instruction)

**Current Task:** Extract and separate engine, build ONE bundle at a time with 2025-2026 verified data

✓ **Completed:**
1. Engine extracted and protected in CORE_ENGINE/
2. Bundle 1 (construction_safety) — VERIFIED, COMMITTED, PUSHED
3. Bundle 2 (finance_sox) — VERIFIED, COMMITTED, PUSHED

**To Do:**
4. Bundle 3-6: Research-driven builds (pending user guidance on which bundle to prioritize next)

---

## Files in Repository

### Protected Core Engine
- `CORE_ENGINE/current/gate_v1.py` — Canonical validator
- `CORE_ENGINE/current/test_gate_v1.py` — Test suite
- `CORE_ENGINE/orthogonal_regions/formation.py` — Multi-region resolver
- `CORE_ENGINE/house_engine_v2/ledger.py` — Append-only ledger
- `CORE_ENGINE/house_engine_v2/replay.py` — Replay engine
- `CORE_ENGINE/outbox_dispatcher/perimeter_watchdog.py` — Security boundary
- `CORE_ENGINE/outbox_dispatcher/outbox_dispatcher.py` — Event dispatcher

### Verified Bundles
- `VERIFIED_BUNDLES/construction_safety_v1.json`
- `VERIFIED_BUNDLES/construction_safety_SOURCES.md`
- `VERIFIED_BUNDLES/construction_safety_VALIDATION_REPORT.txt`
- `VERIFIED_BUNDLES/finance_sox_v1.json`
- `VERIFIED_BUNDLES/finance_sox_SOURCES.md`
- `VERIFIED_BUNDLES/finance_sox_VALIDATION_REPORT.txt`

### Documentation
- `VERIFIED_BUNDLES_ROADMAP.md` — Build strategy and checklist
- `COMPLIANCE_FACT_CHECK_AUDIT.md` — Metrics verification (from earlier research)
- `BUNDLE_METRIC_THRESHOLD_AUDIT.md` — Threshold analysis (from earlier research)

---

## Summary

**Two verified bundles are production-ready with:**
- ✓ Correct Thornigal orthogonal regions pattern
- ✓ Event-driven transitions (no unverified guard conditions)
- ✓ 100% 2025-2026 verified sources
- ✓ Gate validation passing
- ✓ Complete audit trail documentation

**Engine is protected and ready for ledger integration:**
- ✓ HMAC-SHA256 append-only ledger
- ✓ Hash-chained causality tracking
- ✓ Perimeter watchdog security boundary
- ✓ Event-sourced architecture

**Bundles 3-6 require targeted research before build.**

---

**Prepared by:** Claude Code  
**Date:** 2026-10-24  
**Branch:** claude/code-sync-pc-migration-j25hn3  
**Commits:** 381fb15 → 8a793a3 → 02bc403

