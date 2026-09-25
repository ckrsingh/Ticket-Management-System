# RAG / Vector Store Guidelines

## Chunking

- Ticket-scoped section chunks (header, per-comment, resolution).
- Max ~1500 chars; split on `\n\n` only when needed.
- Attach metadata: `ticketId`, `status`, `priority`, `assignee`, `category`, `section`.

## Embedding Model

- Dev: Ollama `nomic-embed-text` via Spring AI Ollama starter.
- Test: deterministic embedder (no network).
- Document tradeoffs in `spec/architecture.md` before changing models.

## Retrieval Defaults (configurable)

```yaml
app.rag.top-k: 5
app.rag.similarity-threshold: 0.65
```

## Grounding

- Single retrieval → generate pass; no agent tools.
- System prompt: answer only from provided ticket excerpts; if insufficient, say no relevant tickets.
- Response must list `sources[].ticketId`.
- Re-ingest on every ticket/comment update (delete vectors for ticket first).

## Vector Store

- Dev/test: `SimpleVectorStore` persisted to `./data/vector-store.json`.
- Prod: PGVector with same metadata schema.
