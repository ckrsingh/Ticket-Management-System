# RAG Evaluation Strategy

## Retrieval Metrics (manual + scripted)

1. **Hit rate**: for a golden set of 10 questions, ≥1 correct ticket in top-K.
2. **Citation accuracy**: cited `ticketId` appears in retrieved set.
3. **Abstention**: off-topic questions return `grounded: false` with no fabricated entities.

## Golden Questions (seed data)

| Question | Expected ticket theme |
|----------|----------------------|
| Payment failures | billing / payment |
| TKT-1001 resolution | specific public ID in seed |
| Shipment tracking | logistics category |

## Probabilistic Testing

- Integration tests use deterministic `TestEmbeddingModel` + fixed vectors for ask endpoint shape.
- Separate manual script `docs/rag-smoke.md` for Ollama-backed qualitative checks.

## Regression

When chunking or embedding model changes, re-run golden set and compare hit rate.
