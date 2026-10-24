＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
SESSION MASTER SNAPSHOT  —  paste-back state reconciliation
＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

SESSION_ID:   2026-06-16_recovery-modes
TIMESTAMP:    2026-06-16
MODE:         Meta Mode (design / reconstruction)
STATE OF PLAY:
  Lost session fully recovered from three source documents. Complete
  three-layer architecture reconstructed in text. Layer-0 gate's
  multi-node SCC trap CLOSED and proven closed this session (re-beat
  confirmed, no false positives, no regressions). Save-boundary protocol
  defined. Workflow modes + "soul tie" still unreconstructed.

────────────────────────────────────────────────────────────────────────
RESUME PROTOCOL  (run this first on paste-back, before adding anything new)
────────────────────────────────────────────────────────────────────────
  1. Confirm you can reconstruct every SETTLED item below WITHOUT
     contradiction. If any item doesn't cohere, say so before proceeding —
     that's the drift-check-on-resume, the conversational analogue of the
     gate's load-time validation. Catch it now, not 40 messages later.
  2. Do NOT relitigate anything under SETTLED. It is source of truth.
  3. Surface OPEN GAPS explicitly. Never let a dropped gap vanish silently.
  4. Hold Meta Mode tone: disciplined technical strategist, structural,
     no theatrical framing. Push back honestly. Replace trust with rules.

────────────────────────────────────────────────────────────────────────
SETTLED  —  do not re-derive
────────────────────────────────────────────────────────────────────────

[ FOUNDATION — why the architecture is shaped this way ]
  • Structure ships, contents don't. Bundle = structure (schema, gate,
    state/transition shape, patterns — universal) + contents (vocabulary,
    weights, truth library — irreducibly per-domain). A vocabulary broad
    enough to ship everywhere is unbounded = the loose-vocabulary
    corruption. Refused artifact: pre-filled universal word-list.
    Delivered artifact: robust skeleton, cloned per domain, contents
    re-authored behind the gate.
  • Depth = Harel statecharts (David Harel, 1987, "Statecharts: A Visual
    Formalism for Complex Systems," Science of Computer Programming; origin:
    Lavi avionics; now part of UML). Hierarchy, orthogonal regions, history
    states — not a bigger flat machine. The three signals (focus,
    volatility, recovery) should be three ORTHOGONAL (independent, parallel,
    communicating) regions, not one STRAINED/BALANCED/HARMONIOUS scalar +
    side vector. History states = decay-on-return + reality-recheck watchdog.
  • Rules-as-data reference impl = XState/SCXML. Stateless machine def,
    transition() pure function, engine separate from bundle. Multi-tenant
    definition storage = the agent factory. Study the split; don't
    necessarily adopt the tool.
  • Edge-case discipline = total transition function + illegal-states-
    unrepresentable. Every (state × input) has an explicit outcome;
    impossible combos unrepresentable by construction (discriminated unions
    over boolean flags). Same principle as the controlled-vocabulary
    Anti-Corruption Layer, one level up.
  • Escape hatch (anti-over-reach): an unhandled (state × input) becomes an
    explicit, logged UNKNOWN/escalation EVENT. Never silent fall-through,
    never crash. Keeps the gap auditable — the ledger ethic.

[ LAYER 0 — the gate (load-time static analysis of one bundle) ]
  • Four-argument hardening method closes the infinite bundle-space gap:
      - exhaustive small-scope (2,592 cases; proof within 3 states / 2
        events / 1 region)
      - differential oracle (4,000; independent reachability impl)
      - metamorphic (2,800; assert direction output should move on
        perturbation; no ground-truth answer key needed)
      - mutation (5/5 killed; proves the suite has teeth)
  • STATE_SELF_TRAP found + closed (s0 --adv--> s0, behavioral black hole).
    Permanent regression rig welded on.
  • ** MULTI-NODE SCC TRAP — CLOSED THIS SESSION. ** A sealed cluster of
    states cycling only among themselves with no exit to a terminal.
    Slipped the per-state gate at 2, 3, and realistic scale (the
    "lobster trap": an escalated ticket that can never close).
    THE LAW that closed it (subsumes self-trap AND dead-end as instances):
      → a reachable, non-terminal state that cannot reach any terminal
        is TRAPPED; reject the bundle.
    SCC (Tarjan) rides on top ONLY to NAME the sealed cluster in the fault.
    Re-beat proof: all 3 varmints flipped CLEAN→FAULT; VALID_ESCAPABLE_LOOP
    stayed CLEAN (law is precise — permits escapable loops, kills only
    sealed ones); self-trap + dead-end controls unchanged. No false
    positives, no regressions. "CLOSE CONFIRMED."
  • Gate checklist (current): reachability (orphans) · co-reachability-of-
    terminal (THE LAW — replaces dead-end + self-trap + multi-node) ·
    [still open: contradictory-guard detection].

[ DRIFT LAYER — longitudinal, watches the running trajectory (NOT yet built) ]
  • Gate validates a finite artifact at load; drift is a property of an
    unbounded trajectory over time. Core trap: determinism guarantees
    REPRODUCIBLE truth, not CURRENT truth — a rotted rulebook stays green.
  • Instruments the design already emits: replay-diff oracle (same event
    log through bundle v2 vs v1, diff resulting ledgers) · vocabulary-
    boundary escalation rate (rising UNKNOWNs = leading indicator) ·
    polarity advance/regress aggregate invariants.
  • HARD CONSTRAINT: drift detection stays rule-based / deterministic /
    ledger-logged. NEVER a probabilistic or ML anomaly detector — that is
    unauditable drift wearing a lab coat.

[ SAVE-BOUNDARY PROTOCOL ]
  • Human-in-the-loop keyword ("checkpoint"); you fire it or Claude suggests
    it on structural change / breakthrough / char-limit approach.
  • Output = paste-back master prompt (this file) carrying active mode.
  • Snapshot fields: provenance · open-gaps register · do-not-re-derive
    list · mode-specific resume instruction · drift-check-on-resume.
  • Two checkpoints upstream of each other:
      GATE 1 (load-time, static, per-bundle) — SCC fix lives here.
      GATE 2 (drift, longitudinal, post-instantiation) — watches aging.
    Only structurally-sound bundles ever reach the hallways drift watches.

────────────────────────────────────────────────────────────────────────
OPEN GAPS  —  carry forward, unresolved
────────────────────────────────────────────────────────────────────────
  • LOGICAL GUARD CONTRADICTION — gate catches structural nondeterminism,
    not two guards both satisfiable by different logic. (Last structural
    gap remaining in Gate 1.)
  • DRIFT LAYER, four untransferred problems:
      (a) can't enumerate/replay an unbounded log → checkpoints / windowing
          / incremental detection
      (b) drift has no FAIL line → needs leading indicators, not just
          lagging ones
      (c) the baseline can itself drift → oracle needs its own validation
          discipline
      (d) fleet-wide drift is cross-bundle → only visible at reconciliation
  • WORKFLOW MODES (Meta / Build / Run) — named, not yet defined in text.
  • "SOUL TIE HARD ENGINE" — unresolved: a modeled DOMAIN, or a structural
    ENGINE COMPONENT? Awaiting Richard's input. (Flagged: may carry weight
    beyond the technical layer — track honestly, don't flatten.)
  • STANDING BUILD-FORK (from the foundation doc, never answered): draft the
    bundle schema + gate checklist as a deliverable, and model Make Ready
    Pro OR Dev Coach as the reference statechart proving orthogonal-region
    depth against the current flat design — which domain?
  • SNAPSHOT TEMPLATE — fields proposed; this file is the first working
    instance of it.

────────────────────────────────────────────────────────────────────────
ARTIFACTS  (durable, saved this session)
────────────────────────────────────────────────────────────────────────
  • scc_trap_test.py   — the deliberate test: shipped per-state gate vs
                         independent co-reachability oracle; varmints
                         surface the gap as a disagreement.
  • gate_v2_close.py   — the close: co-reachability law + Tarjan SCC namer;
                         re-beat of all varmints; CLOSE CONFIRMED.

────────────────────────────────────────────────────────────────────────
LIVE THREADS  (pick up here)
────────────────────────────────────────────────────────────────────────
  1. Workflow modes (Meta / Build / Run) + the "soul tie" question.
  2. Guard-contradiction detection (last Gate-1 structural gap).
  3. Drift layer build (Gate 2) — start with the data-handling spine
     (checkpoints / windows / incremental) so the oracle has something
     tractable to run against.
  4. The standing build-fork: schema + reference statechart, which domain.

＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
END SNAPSHOT  —  paste this whole file at the top of a new chat to resume
＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
