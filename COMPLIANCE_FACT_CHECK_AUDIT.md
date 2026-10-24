# Compliance Metrics Fact-Check Audit
## Bundle Values vs. 2026 Verified Public Data

**Purpose:** Before committing ANY code, verify every metric in the bundles matches verified industry research. Flag fabricated, outdated, or unverified numbers.

---

## 1. HEALTHCARE NURSING

| Metric | Current Bundle Value | Verified 2026 Data | Source | Match? | Status |
|--------|----------------------|-------------------|--------|--------|--------|
| Federal minimum HPRD | 3.48 (null in v2) | REPEALED Feb 2, 2026 | CMS interim final rule, PL 119-21 | ✅ CORRECT | Bundle correctly marks as null |
| California minimum HPRD | [NOT IN BUNDLE] | 3.5 HPRD | CA Health & Safety Code § 1276.65 | ⚠️ MISSING | Should add state-specific minimums |
| New York minimum HPRD | [NOT IN BUNDLE] | 3.5 HPRD (2.2 CNA, 1.1 licensed) | NY Public Health Law § 2895-b | ⚠️ MISSING | Should add state-specific minimums |
| Florida minimum HPRD | [NOT IN BUNDLE] | 3.6 HPRD (2.0 CNA, 1.0 licensed) | FL rules with ratio caps | ⚠️ MISSING | Should add state-specific minimums |
| Texas minimum HPRD | [NOT IN BUNDLE] | No state numeric minimum (8-hr RN baseline only) | Federal OSHA only | ⚠️ MISSING | Should document as federal-only |
| Industry best practice HPRD | [NOT IN BUNDLE] | 4.1 total HPRD, 0.75 RN HPRD | 2001 federal study (historical) | ⚠️ OUTDATED | Only 28% of facilities meet this |
| Current national average | [NOT IN BUNDLE] | 3.75 total HPRD, 0.62 RN HPRD | Q1 2026 national data | ⚠️ MISSING | Document current reality |
| Joint Commission NPG 12 | [NOT IN BUNDLE] | Applies to **hospitals ONLY** as of Jan 1, 2026 | JC announcement | ⚠️ MISALIGNED | Bundle is nursing homes, not covered |

**Recommendation:** Bundle is STRUCTURALLY CORRECT (null on repealed value), but INCOMPLETE. Add state minimums and clarify which regulations apply to nursing vs. hospitals.

---

## 2. PROPERTY TURNOVER

| Metric | Current Bundle Value | Verified 2026 Data | Source | Match? | Status |
|--------|----------------------|-------------------|--------|--------|--------|
| Average turnover cost | ~$3,872 | $3,872–$4,000 (Zego 2026 survey of 602 PMs) | Zego survey, NAA | ✅ CORRECT | Within range |
| Average vacancy duration | ~41 days | 40–41 days | Apartment List, RentCafe 2026 | ✅ CORRECT | Verified |
| YoY cost increase | +17.5% | Limited 2026 data; costs stable at $4,000; **vacancy duration increasing** | NAA benchmark | ⚠️ PARTIALLY OUTDATED | Costs stable, but **vacancy growing** |
| Cleaning cost | [NOT SPECIFIED] | $120–$420 | Zego 2026 breakdown | ⚠️ MISSING | Consider adding breakdown |
| Painting cost | [NOT SPECIFIED] | $200–$2,000 (full interior avg $2,022) | Zego 2026 breakdown | ⚠️ MISSING | Consider adding breakdown |
| Repairs/maintenance | [NOT SPECIFIED] | $100–$500 | Zego 2026 breakdown | ⚠️ MISSING | Consider adding breakdown |
| Lost rent (vacancy cost driver) | [NOT SPECIFIED] | $50–$65/vacant day (40-70% of total turnover cost) | Zego 2026 analysis | ⚠️ CRITICAL MISSING | Largest cost component not in bundle |
| Regional vacancy rate range | [NOT SPECIFIED] | South 10.8%, Northeast 5.1% | Zego/RentCafe 2026 | ⚠️ MISSING | Document regional variation |

**Recommendation:** Bundle costs are CORRECT, but INCOMPLETE. The research shows **lost rent is the largest cost driver** (40-70% of total), which should be emphasized. Add regional variations.

---

## 3. FACILITY MAINTENANCE

| Metric | Current Bundle Value | Verified 2026 Data | Source | Match? | Status |
|--------|----------------------|-------------------|--------|--------|--------|
| PM completion best-in-class | ≥92% | 90%+ is consensus; 92%+ cited in vendor guides | IFMA, vendor guides | ⚠️ UNVERIFIED | **NOT formally published by IFMA** |
| PM completion <80% = reactive | <80% = reactive | Verified: <65% reactive, 80% is threshold | Industry consensus | ✅ CORRECT | Supported by research |
| Critical system uptime | 99% | Mission-critical 99.9%+; business-critical 90–95% | IFMA/ASHRAE practice | ⚠️ INCOMPLETE | Should tier by system type |
| IFMA standard source | [IMPLIES FORMAL] | **IFMA publishes NO specific PM% targets** | IFMA search results | ❌ FABRICATED | This is vendor consensus, not IFMA |
| ISO 41001 compliance | [NOT IN BUNDLE] | ISO 41001:2018 is IFMA-endorsed framework | IFMA endorsement | ⚠️ MISSING | Consider adding ISO reference |
| NFPA 25 sprinkler inspection | [NOT IN BUNDLE] | Quarterly inspections, annual tests, annual internal valve checks (new 2026) | NFPA 25 2026 edition | ⚠️ MISSING | Should add life-safety requirements |
| Emergency response time | [NOT IN BUNDLE] | Life-safety 15–30 min; urgent 2–4 hr; routine 24–72 hr | Industry practice | ⚠️ MISSING | Consider adding service level targets |

**CRITICAL ISSUE:** The 92% PM compliance threshold **is NOT a published IFMA standard**—it's vendor consensus. The bundle may be claiming a standard that doesn't exist formally.

---

## 4. LOGISTICS/SUPPLY CHAIN

| Metric | Current Bundle Value | Verified 2026 Data | Source | Match? | Status |
|--------|----------------------|-------------------|--------|--------|--------|
| OTIF excellence threshold | 95–98% | 95–98% for consumer goods confirmed | Multiple sources | ✅ CORRECT | Industry consensus verified |
| Walmart requirement | [LIKELY 95% IMPLIED] | 90–98% on-time (varies by supplier type); 95% in-full | Walmart supplier guides 2026 | ⚠️ INCOMPLETE | Walmart has **ranges**, not single target |
| Walmart chargeback penalty | [NOT SPECIFIED] | 3% of cost of goods per non-compliant case | Fidelitone/Walmart 2026 guides | ⚠️ MISSING | Critical financial metric missing |
| OTIF poor performance | <85% is poor | <90% carries high chargeback risk | FourKites, industry guides | ✅ CORRECT | Confirmed |
| Supplier scorecard excellent | [NOT SPECIFIED] | 99%+ excellent; 95–98% meets most retailers | Industry consensus | ⚠️ MISSING | Should add scorecard tiers |
| Manufacturing OTIF target | [NOT IN BUNDLE] | 88–95% (varies by lead time) | SourceDay 2026 | ⚠️ MISSING | Document industry variation |
| Healthcare supply chain OTIF | [NOT IN BUNDLE] | 98%+ (higher than consumer goods) | SourceDay 2026 | ⚠️ MISSING | Note sector-specific requirements |
| Measurement inconsistency | [NOT DOCUMENTED] | 15–18 point gap between internal and customer views | Industry research findings | ⚠️ CRITICAL GAP | Bundle doesn't account for definition variance |

**Recommendation:** OTIF targets are CORRECT, but bundle is INCOMPLETE on Walmart specifics and chargebacks. **Measurement definition gap is not addressed.**

---

## 5. CONSTRUCTION SAFETY

| Metric | Current Bundle Value | Verified 2026 Data | Source | Match? | Status |
|--------|----------------------|-------------------|--------|--------|--------|
| Construction TRIR | 2.2 | 2.2 (BLS 2024, latest available) | BLS 2024 data | ✅ CORRECT | 2025-2026 national data not yet published |
| OSHA serious violation penalty | $16,550 | $16,550 (2025-2026; **no increase in 2026**) | OSHA 2025-2026 memo | ✅ CORRECT | Verified current |
| OSHA willful/repeat max | $165,514 | $165,514 (2025-2026) | OSHA 2025-2026 memo | ✅ CORRECT | Verified current |
| OSHA willful minimum | $11,823 | $11,823 (new in 2025) | OSHA 2025-2026 memo | ✅ CORRECT | Verified current |
| Training impact (orientation) | [NOT IN BUNDLE] | **93% lower TRIR** for 195+ min orientation vs. <75 min | ABC/construction industry research | ⚠️ MISSING | Significant finding not captured |
| Best-performing TRIR | [NOT IN BUNDLE] | CII members: 0.27 TRIR (2023); ABC STEP: 85–86% reduction | CII/ABC benchmark data | ⚠️ MISSING | Document excellence benchmarks |
| Trade-specific TRIR | [NOT IN BUNDLE] | Framing 5.5, roofing 3.2 (substantially above average 2.2) | BLS trade-specific data | ⚠️ MISSING | Document trade variation |
| State variations | [NOT DOCUMENTED] | 22 states with approved OSHA plans; CA strictest; TX/others federal-only | OSHA state plan map | ⚠️ INCOMPLETE | Bundle should document state variation |

**Recommendation:** TRIR and OSHA penalties are CORRECT. Bundle is **INCOMPLETE** on training effectiveness and state-specific variations.

---

## 6. FINANCE SOX/ICFR

| Metric | Current Bundle Value | Verified 2026 Data | Source | Match? | Status |
|--------|----------------------|-------------------|--------|--------|--------|
| Material weakness definition | [LIKELY IMPLIED] | **"Reasonable possibility" of material misstatement**—NO quantitative threshold | PCAOB AS 2201, SEC Reg S-X | ✅ CORRECT | No bright-line % exists |
| Accelerated filer threshold | [NOT SPECIFIED] | Public float $75M–$700M, revenue $100M+ | SEC definition 2025-2026 | ⚠️ MISSING | Should document thresholds |
| Large accelerated filer | [NOT SPECIFIED] | Public float ≥$700M | SEC definition 2025-2026 | ⚠️ MISSING | Should document thresholds |
| Non-accelerated filer exemption | [NOT SPECIFIED] | Exempt from 404(b) auditor attestation; must still do 404(a) management assessment | SEC rules 2025-2026 | ⚠️ MISSING | Important compliance boundary |
| PCAOB update effective date | [NOT IN BUNDLE] | December 15, 2026 (AS 2201 amendments) | PCAOB 2024 announcement | ⚠️ MISSING | Timeline relevant for 2026 compliance |
| 10-K filing deadline (accelerated) | [NOT IN BUNDLE] | 60 days after fiscal year-end | SEC rules | ⚠️ MISSING | Should document timeline |
| 10-K filing deadline (non-accel) | [NOT IN BUNDLE] | 90 days after fiscal year-end | SEC rules | ⚠️ MISSING | Should document timeline |
| Section 906 penalties | [NOT IN BUNDLE] | Knowing: 10 yrs + $1M; Willful: 20 yrs + $5M | Section 906 law | ⚠️ MISSING | Significant penalties not documented |

**Recommendation:** Bundle concept is CORRECT (no quantitative threshold), but INCOMPLETE. Should document filer categories, timelines, and penalties.

---

## SUMMARY OF FINDINGS

### ✅ CORRECT (verify before code commit)
- Healthcare: Federal repeal status correct (null)
- Property: Turnover costs $3,872–$4,000 verified
- Logistics: 95–98% OTIF excellence threshold verified
- Construction: TRIR 2.2, OSHA penalties 2025-2026 all verified
- Finance: No quantitative material weakness threshold correct

### ⚠️ UNVERIFIED OR SOURCING ISSUES
- **Facility Maintenance PM 92% threshold**: NOT formally published by IFMA—this is vendor consensus
- **Healthcare accreditation**: Joint Commission NPG 12 applies to hospitals ONLY, not nursing homes

### ❌ CRITICAL GAPS (missing from bundles)
- **Healthcare**: State-specific minimums (CA 3.5, NY 3.5, FL 3.6, TX federal-only)
- **Property**: Lost rent is 40–70% of total cost; vacancy duration increasing
- **Logistics**: Walmart 3% chargeback penalty; measurement definition gap (15–18 pt variance)
- **Facility**: NFPA life-safety requirements; system uptime tiers
- **Construction**: Training effectiveness (93% TRIR reduction); state variations; trade-specific variation
- **Finance**: Filer categories; filing deadlines; Section 906 penalties

### ❌ POTENTIAL FABRICATIONS (needs verification before code)
- **Facility Maintenance**: The 92% PM "best-in-class" threshold is cited as industry practice but is NOT an IFMA published standard

---

## RECOMMENDATION

**DO NOT COMMIT CODE** until we:

1. **Verify the source** of the 92% FM threshold—is this acceptable as vendor consensus, or should we use 90% (industry consensus)?
2. **Clarify which numbers to update** in the bundles (do we want complete state-by-state data, or keep current structure?)
3. **Document the gaps** explicitly (are they intentional simplifications, or missing requirements?)
4. **Add citations** to bundles so compliance auditors can verify our data came from authoritative sources

---

**Status: READY FOR YOUR REVIEW**

Which metrics should be updated in the code? Which gaps are acceptable? Which need fixing before we commit anything?
