# Outbox Dispatcher — Universal Commit Fan-Out

Sits OUTSIDE the deterministic core, same boundary discipline as the fire
notifier. Every ledger commit gets offered to every registered channel
adapter, independently -- one failing doesn't block the others.

## Verified live this session
- Dispatcher mechanism itself: real, tested, works correctly.
- gmail adapter: REAL channel, Gmail already connected and proven in this
  conversation (used for the Rose State email). Returns the exact payload
  shape a real Gmail:create_draft call needs.

## Honestly stubbed, not faked
- google_sheets_adapter: NotImplementedError -- needs Sheets MCP connector
  or API credentials. Solvable, not yet done.
- google_keep_adapter: NotImplementedError -- flag plainly: Google Keep has
  historically had NO official public API. This may not be a "connect it"
  problem at all -- needs research before committing to this channel.
- sms_staff_directory_adapter: NotImplementedError -- needs an SMS provider
  (e.g. Twilio) AND a real staff directory data source, neither exists yet.

## Next step
User was prompted for connector opt-in mid-session (Sheets/SMS search).
Once resolved, re-run search_mcp_registry to get real connector UUIDs and
wire google_sheets_adapter (and SMS if a provider connector exists) for
real -- same pattern as gmail_adapter, no architecture changes needed.

## Perimeter Watchdog — added this session, both directions verified

**Outbound (proven structurally, not by convention):** every channel
adapter's function signature takes only `event: Dict[str, Any]` -- never
the Ledger object. Confirmed via inspect.signature() on all four adapters.
A compromised or malicious adapter has no handle to call .commit() because
it was never given one.

**Inbound (perimeter_watchdog.py, new):** anything coming back from the
network is treated as untrusted by law, same as Soul Tie Brain's controlled
vocabulary. Strict allow-list by channel name + required key shape. Unknown
channel = rejected by default. Wrong shape = rejected, not assumed. Even a
passing shape is only queued for HUMAN review -- the watchdog itself has no
commit/write method at all, confirmed by inspecting its own method list.

Tested live: legitimate-shaped response accepted-for-review, malformed
payload rejected, unregistered channel rejected by default.

LAW: inbound network data has ZERO direct write path to the ledger, ever.
A human must explicitly commit a normal event through the normal path if
an external response needs to become a real fact.
