"""
GATE v2 - the close. Replace the three per-state checks with ONE law:
    a reachable, non-terminal state that cannot reach any terminal is TRAPPED -> reject.
This law subsumes self-trap (1-node, self-loop), dead-end (0-exit), and the multi-node
sealed cluster (N-node) as instances of the same property.
SCC (Tarjan) rides on top ONLY to NAME the sealed cluster in the fault, so rejection is auditable.
Then we re-beat: today's exact varmints back through old gate vs new gate.
Deterministic; sorted iteration throughout.
"""
from typing import Dict, List, Set

def states_of(b):
    s=set(b.get("states",set())); s.add(b["initial"]); s|=set(b["terminal"])
    for f,_e,t in b["transitions"]: s.add(f); s.add(t)
    return s
def out_targets(b,s): return sorted(t for (f,_e,t) in b["transitions"] if f==s)
def forward_reachable(b):
    seen,stack=set(),[b["initial"]]
    while stack:
        x=stack.pop()
        if x in seen: continue
        seen.add(x); stack.extend(out_targets(b,x))
    return seen
def terminal_coreachable(b):
    rev={}
    for f,_e,t in b["transitions"]: rev.setdefault(t,[]).append(f)
    seen,stack=set(),list(b["terminal"])
    while stack:
        x=stack.pop()
        if x in seen: continue
        seen.add(x); stack.extend(sorted(rev.get(x,[])))
    return seen

# ---- Tarjan SCC, iterative, deterministic ----
def tarjan_scc(b):
    nodes=sorted(states_of(b)); adj={n:out_targets(b,n) for n in nodes}
    idx={}; low={}; onstk={}; stk=[]; out=[]; c=[0]
    def strongconnect(v):
        work=[(v,0)]
        while work:
            node,pi=work[-1]
            if pi==0:
                idx[node]=low[node]=c[0]; c[0]+=1; stk.append(node); onstk[node]=True
            recurse=False; succ=adj[node]
            i=pi
            while i<len(succ):
                w=succ[i]
                if w not in idx:
                    work[-1]=(node,i+1); work.append((w,0)); recurse=True; break
                elif onstk.get(w): low[node]=min(low[node],idx[w])
                i+=1
            if recurse: continue
            if low[node]==idx[node]:
                comp=[]
                while True:
                    w=stk.pop(); onstk[w]=False; comp.append(w)
                    if w==node: break
                out.append(sorted(comp))
            work.pop()
            if work:
                p=work[-1][0]; low[p]=min(low[p],low[node])
    for n in nodes:
        if n not in idx: strongconnect(n)
    return out

# ---- OLD gate (per-state, NO SCC) ----
def gate_old(b):
    f=[]; allS=states_of(b); reach=forward_reachable(b)
    for s in sorted(allS-reach): f.append(("REACHABILITY",s))
    for s in sorted(reach):
        if s in b["terminal"]: continue
        outs=out_targets(b,s)
        if not outs: f.append(("STATE_DEAD_END",s)); continue
        if all(t==s for t in outs): f.append(("STATE_SELF_TRAP",s))
    return f

# ---- NEW gate v2: the law + SCC cluster-namer ----
def gate_v2(b):
    findings=[]; allS=states_of(b); reach=forward_reachable(b); canEnd=terminal_coreachable(b)
    for s in sorted(allS-reach): findings.append(("REACHABILITY",[s]))   # orphan: separate defect
    trapped={s for s in reach if s not in b["terminal"] and s not in canEnd}  # THE LAW
    if trapped:
        for comp in tarjan_scc(b):
            inside=[s for s in comp if s in trapped]
            if not inside: continue
            if len(comp)>=2:
                findings.append(("SEALED_CLUSTER",inside))           # multi-node trap, named
            else:
                s=comp[0]; outs=out_targets(b,s)
                if not outs:        findings.append(("DEAD_END",[s]))     # 0-exit instance
                elif all(t==s for t in outs): findings.append(("SELF_TRAP",[s]))  # self-loop instance
                else:               findings.append(("FEEDS_TRAP",[s]))   # leads only into a trap
    return findings

def vd(f): return "CLEAN" if not f else "FAULT"
def fmt(f):
    return "; ".join(f"{c}{{{','.join(ss)}}}" for c,ss in f) or "-"

# ===================== today's EXACT varmints + a false-positive guard =====================
GOOD={"name":"GOOD_HAPPY_PATH","initial":"START","terminal":{"DONE"},
 "transitions":[("START","begin","WORK"),("WORK","finish","DONE"),("WORK","retry","START")]}
SELF_TRAP={"name":"CONTROL_SELF_TRAP","initial":"INIT","terminal":{"END"},
 "transitions":[("INIT","trap","S0"),("S0","adv","S0"),("INIT","done","END")]}
DEAD_END={"name":"CONTROL_DEAD_END","initial":"INIT","terminal":{"END"},
 "transitions":[("INIT","work","B"),("INIT","done","END")]}
VARMINT_TWIN={"name":"VARMINT_TWIN_LOOP","initial":"START","terminal":{"DONE"},
 "transitions":[("START","ok","DONE"),("START","begin","A"),("A","ping","B"),("B","pong","A")]}
VARMINT_TRI={"name":"VARMINT_TRIANGLE","initial":"START","terminal":{"DONE"},
 "transitions":[("START","ok","DONE"),("START","begin","A"),("A","x","B"),("B","y","C"),("C","z","A")]}
VARMINT_LOBSTER={"name":"VARMINT_LOBSTER_TRAP","initial":"START","terminal":{"DONE"},
 "transitions":[("START","begin","WORK"),("WORK","finish","DONE"),
   ("WORK","escalate","TRIAGE"),("TRIAGE","assign","REVIEW"),("REVIEW","revise","TRIAGE"),
   ("REVIEW","reject","BACKLOG"),("BACKLOG","reopen","TRIAGE"),("TRIAGE","defer","BACKLOG"),
   ("BACKLOG","escalate","REVIEW")]}
# false-positive guard: a real loop that CAN escape to a terminal -> must stay CLEAN
VALID_CYCLE={"name":"VALID_ESCAPABLE_LOOP","initial":"START","terminal":{"DONE"},
 "transitions":[("START","begin","A"),("A","loop","B"),("B","again","A"),("B","finish","DONE")]}

bundles=[GOOD,SELF_TRAP,DEAD_END,VARMINT_TWIN,VARMINT_TRI,VARMINT_LOBSTER,VALID_CYCLE]

print("="*100)
print("RE-BEAT  -  old per-state gate  vs  gate v2 (one law + SCC namer)")
print("="*100)
print(f"{'bundle':<22}{'OLD':<7}{'NEW':<7}{'diagnosis (v2)'}")
print("-"*100)
flips=[]; false_pos=[]; regressions=[]
for b in bundles:
    o=gate_old(b); n=gate_v2(b); ov,nv=vd(o),vd(n)
    note=""
    if ov=="CLEAN" and nv=="FAULT":
        if b["name"].startswith("VARMINT"): flips.append(b["name"]); note="   <== now caught"
        else: false_pos.append(b["name"]); note="   <== FALSE POSITIVE (bad)"
    if ov=="FAULT" and nv=="CLEAN": regressions.append(b["name"]); note="   <== REGRESSION (bad)"
    print(f"{b['name']:<22}{ov:<7}{nv:<7}{fmt(n)}{note}")
print("-"*100)
print(f"varmints that slipped the old gate and are NOW caught: {flips}")
print(f"legit bundles wrongly rejected by v2 (false positives): {false_pos if false_pos else 'none'}")
print(f"controls that regressed (were caught, now miss):        {regressions if regressions else 'none'}")
ok = flips==['VARMINT_TWIN_LOOP','VARMINT_TRIANGLE','VARMINT_LOBSTER_TRAP'] and not false_pos and not regressions
print("="*100)
print("CLOSE CONFIRMED" if ok else "NOT CLOSED - review")
print("="*100)
