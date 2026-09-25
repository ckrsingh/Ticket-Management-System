# AI Mistakes Log

Engineering review of assistant output — not accepted blindly.

## 1. Wrong state transition path (code suggestion)

**Date:** 2026-09-25  
**Mistake:** An early draft allowed `OPEN → RESOLVED` directly, skipping `IN_PROGRESS`.  
**Why wrong:** Violates `spec/state-machine.md` and acceptance criteria.  
**Fix:** `TicketStateMachine` only allows `OPEN → IN_PROGRESS` and `IN_PROGRESS → RESOLVED`.  
**Lesson:** Always generate transition matrix tests before implementing controller endpoints.

## 2. Ungrounded RAG fallback (design suggestion)

**Date:** 2026-09-25  
**Mistake:** Suggested using the chat model’s general knowledge when vector search returns no chunks (“helpful default answer”).  
**Why wrong:** Explicit requirement: no fabrication; must state no relevant tickets.  
**Fix:** `TicketRagService` short-circuits when no chunks pass `similarity-threshold`; no LLM call on empty retrieval.  
**Lesson:** Use `commands/review-rag-output.md` on sample answers before merging RAG changes.

## 3. Hardcoded retrieval parameters

**Date:** 2026-09-25  
**Mistake:** Proposed `private static final int TOP_K = 5` inside the service class.  
**Why wrong:** Acceptance criteria require configurable top-K and threshold.  
**Fix:** `RagProperties` bound to `app.rag.*` in `application.yml`.  
**Lesson:** Cross-check generated code against `rules/rag-vector-store.md`.
