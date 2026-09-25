# Architecture

## System Context

```
[Browser / Next.js UI] --REST--> [Spring Boot API]
                                      |
                    +-----------------+------------------+
                    |                 |                  |
               [H2 / PostgreSQL]  [Vector Store]    [Chat / Embed LLM]
               tickets, comments   ticket chunks     Spring AI
```

## Layers (Backend)

- **web** — REST controllers, DTOs, `GlobalExceptionHandler`
- **service** — `TicketService`, `TicketStateMachine`, `CommentService`
- **ai** — `TicketKnowledgeIngester`, `TicketRagService`, `AskController`
- **domain** — JPA entities, enums
- **config** — Spring AI beans, RAG properties, CORS

## State Machine

Enforced in `TicketStateMachine` (single source of truth). Controllers never set status directly without transition validation.

```
OPEN → IN_PROGRESS → RESOLVED → CLOSED
OPEN → CANCELLED
IN_PROGRESS → CANCELLED
```

## RAG Pipeline

1. On ticket create/update/close (and comment add): build `KnowledgeDocument` from title, description, comments, resolution notes.
2. **Chunking** (see `rag-ingestion.md`): ticket-scoped, section-based chunks with metadata.
3. Embed via configured `EmbeddingModel` (Ollama `nomic-embed-text` dev; OpenAI `text-embedding-3-small` optional cloud).
4. Upsert into vector store keyed by `ticketId` + chunk index (delete-then-insert per ticket on refresh).
5. **Ask flow**: embed question → similarity search with configurable top-K and threshold → if empty, return honest no-match → else LLM with strict system prompt + cited context only.

## Embedding Model Choice

| Option | Latency | Cost | Quality | Decision |
|--------|---------|------|---------|----------|
| Ollama `nomic-embed-text` (local) | Medium | Free | Good for dev/demo | **Default `dev` profile** |
| OpenAI `text-embedding-3-small` | Low | Paid | Strong | **Optional `cloud` profile** |
| Deterministic hash embedder | Instant | Free | Poor semantics | **Tests only** (`test` profile) |

Tradeoff: local embeddings avoid API keys and cost for assessment runs; cloud improves retrieval for production at operational cost.

## Chunking Strategy (Summary)

Ticket data is semi-structured, not long prose. We use **ticket-scoped section chunking** (not naive fixed 512-token splits):

- One chunk for header (title, status, priority, category, assignee).
- One chunk per comment (author + body).
- One chunk for resolution notes when present.

Max chunk size cap (~1500 chars) with paragraph split only if a single comment exceeds the cap. Metadata: `ticketId`, `status`, `priority`, `assignee`, `category`, `section`.

Justification: preserves comment boundaries (fixed-size splitting would merge unrelated comments and hurt citation accuracy).

## Re-ingestion

`TicketEmbeddingListener` (application events) triggers `ingestTicket(ticketId)` after commit on create/update/status change/comment. Implementation deletes all vectors for `ticketId` then re-adds.

## Frontend

Next.js App Router, client components for forms, server-side fetch to Spring API via `NEXT_PUBLIC_API_URL`.

## Token Optimisation

- Project rules/skills in repo (cached context for Cursor).
- Codebase-memory / graph tools for navigation instead of pasting whole files.
- Static RAG system prompt in `application.yml` (stable prefix for prompt caching where provider supports it).
