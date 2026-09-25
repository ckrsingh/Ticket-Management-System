# RAG API Contract

## POST /api/ai/ask

### Request

```json
{
  "question": "What caused previous payment failures?"
}
```

Validation: `question` required, 3–2000 characters.

### Response — grounded

```json
{
  "answer": "Previous payment failures were often due to ...",
  "grounded": true,
  "sources": [
    {
      "ticketId": "TKT-1001",
      "snippet": "Payment gateway timeout ...",
      "similarity": 0.82
    }
  ],
  "retrieval": {
    "topK": 5,
    "similarityThreshold": 0.65,
    "chunksRetrieved": 3
  }
}
```

### Response — no relevant tickets

```json
{
  "answer": "No relevant tickets were found in the knowledge base for this question.",
  "grounded": false,
  "sources": [],
  "retrieval": {
    "topK": 5,
    "similarityThreshold": 0.65,
    "chunksRetrieved": 0
  }
}
```

### Errors

- 400 validation
- 503 if embedding/chat model unavailable (dev: clear message to start Ollama)

## Configuration (application.yml)

```yaml
app.rag:
  top-k: 5
  similarity-threshold: 0.65
```

Overridable via environment: `APP_RAG_TOP_K`, `APP_RAG_SIMILARITY_THRESHOLD`.
