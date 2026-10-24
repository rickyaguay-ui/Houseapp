"""
DELIBERATE TEST - multi-node SCC trap (known-open gap, one size up from STATE_SELF_TRAP)

  gate_current(): the SHIPPED gate. Per-state static checks only (incl. self-trap fix). NO SCC.
  oracle_trap():  INDEPENDENT impl via a different property (terminal-co-reachability).
The bug surfaces as a DISAGREEMENT between them - the same signature that found the self-trap.
Every bundle below is constructed so its terminal is genuinely REACHABLE, so the reachability
check is satisfied and the ONLY thing left to catch a trap is trap-aware analysis.
Pure functions. No randomness. A deliberate handful, not volume.
"""
from typing import Dict, List, Set

def states_of(b):
    s = set(b.get("states", set())); s.add(b["initial"]); s |= set(b["terminal"])
    for f,_e,t in b["transitions"]: s.add(f); s.add(t)
    return s
def out_targets(b,s): return [t for (f,_e,t) in b["transitions"] if f==s]
def forward_reachable(b):
    seen,stack=set(),[b["initial"]]
    while stack:
        s=stack.pop()
        if s in seen: continue
        seen.add(s); stack.extend(out_targets(b,s))
    return seen

# ---- SHIPPED GATE: per-state only, self-trap fix included, NO SCC ----
def gate_current(b):
    findings=[]; allS=states_of(b); reach=forward_reachable(b)
    for s in sorted(allS-reach): findings.append({"code":"REACHABILITY","state":s})
    for s in sorted(reach):
        if s in b["terminal"]: continue
        outs=out_targets(b,s)
        if len(outs)==0: findings.append({"code":"STATE_DEAD_END","state":s}); continue
        if all(t==s for t in outs): findings.append({"code":"STATE_SELF_TRAP","state":s})
    return findings

# ---- INDEPENDENT ORACLE: can a terminal still be reached from here? ----
def terminal_coreachable(b):
    rev={}
    for f,_e,t in b["transitions"]: rev.setdefault(t,[]).append(f)
    seen,stack=set(),list(b["terminal"])
    while stack:
        s=stack.pop()
        if s in seen: continue
        seen.add(s); stack.extend(rev.get(s,[]))
    return seen
def oracle_trap(b):
    reach=forward_reachable(b); canEnd=terminal_coreachable(b)
    return {s for s in reach if s not in b["terminal"] and s not in canEnd}

def verdict(x): return "CLEAN" if not x else "FAULT"
def run(b):
    g=gate_current(b); o=oracle_trap(b); gv,ov=verdict(g),verdict(o); agree=(gv==ov)
    codes=",".join(sorted({f["code"] for f in g})) or "-"
    trapped=",".join(sorted(o)) or "-"
    flag="   <== DISAGREE (gap surfaced)" if not agree else ""
    print(f"{b['name']:<22} gate={gv:<5}[{codes:<26}] oracle={ov:<5} trapped={{{trapped}}}{flag}")
    return agree

# ===================== BUNDLES (terminal always reachable) =====================
GOOD={"name":"GOOD_HAPPY_PATH","initial":"START","terminal":{"DONE"},
 "transitions":[("START","begin","WORK"),("WORK","finish","DONE"),("WORK","retry","START")]}

# control: self-trap, isolated (INIT branches so END is reachable -> only finding is the self-trap)
SELF_TRAP={"name":"CONTROL_SELF_TRAP","initial":"INIT","terminal":{"END"},
 "transitions":[("INIT","trap","S0"),("S0","adv","S0"),("INIT","done","END")]}

# control: deadlock, isolated
DEAD_END={"name":"CONTROL_DEAD_END","initial":"INIT","terminal":{"END"},
 "transitions":[("INIT","work","B"),("INIT","done","END")]}   # B: non-terminal, no exit

# varmint 1 - minimal 2-node loop; healthy branch keeps DONE reachable
VARMINT_TWIN={"name":"VARMINT_TWIN_LOOP","initial":"START","terminal":{"DONE"},
 "transitions":[("START","ok","DONE"),("START","begin","A"),("A","ping","B"),("B","pong","A")]}

# varmint 2 - 3-node ring; DONE reachable via healthy branch
VARMINT_TRI={"name":"VARMINT_TRIANGLE","initial":"START","terminal":{"DONE"},
 "transitions":[("START","ok","DONE"),("START","begin","A"),("A","x","B"),("B","y","C"),("C","z","A")]}

# varmint 3 - the mean one. happy path COMPLETES, real terminal exists, every cluster state has
# multiple exits to DIFFERENT states, no self-loops -> spotless per-state. but anything ESCALATED
# into {TRIAGE,REVIEW,BACKLOG} can never reach DONE. an escalated ticket can never close.
VARMINT_LOBSTER={"name":"VARMINT_LOBSTER_TRAP","initial":"START","terminal":{"DONE"},
 "transitions":[("START","begin","WORK"),("WORK","finish","DONE"),
   ("WORK","escalate","TRIAGE"),("TRIAGE","assign","REVIEW"),("REVIEW","revise","TRIAGE"),
   ("REVIEW","reject","BACKLOG"),("BACKLOG","reopen","TRIAGE"),("TRIAGE","defer","BACKLOG"),
   ("BACKLOG","escalate","REVIEW")]}

print("="*94)
print("DELIBERATE SCC-TRAP TEST   shipped per-state gate   vs   independent co-reachability oracle")
print("="*94)
res=[(b["name"],run(b)) for b in [GOOD,SELF_TRAP,DEAD_END,VARMINT_TWIN,VARMINT_TRI,VARMINT_LOBSTER]]
print("-"*94)
dis=[n for n,a in res if not a]
print(f"good + per-state controls: gate and oracle AGREE on {sum(1 for _,a in res if a)}/{len(res)}")
print(f"DISAGREEMENTS (gate waved through, oracle caught the trap): {dis if dis else 'none'}")
print("="*94)
