# RAG Ingestion

## Document Assembly

For each ticket, textual knowledge is built from:

1. Header block: public ID, title, status, priority, category, assignee, description
2. Each comment: `[author @ timestamp] body`
3. Resolution block when status is RESOLVED or CLOSED and notes exist

## Chunking Convention

- **Strategy**: ticket-scoped section chunks (see `architecture.md`)
- **Cap**: 1500 characters; overflow splits on paragraph boundaries (`\n\n`)
- **ID**: `docId = {publicId}#{section}#{chunkIndex}`

## Metadata (stored with each vector)

```json
{
  "ticketId": "TKT-1001",
  "status": "RESOLVED",
  "priority": "HIGH",
  "assignee": "alice@example.com",
  "category": "billing",
  "section": "COMMENT"
}
```

## Triggers

| Event | Action |
|-------|--------|
| Ticket created | ingest |
| Ticket updated | re-ingest (delete + insert) |
| Comment added | re-ingest |
| Status → CLOSED | re-ingest |

## Stale Prevention

Never append-only without delete: `VectorStore.delete(filter ticketId == X)` then add new chunks.
