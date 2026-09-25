# Test Strategy

## Backend

| Layer | Tests |
|-------|-------|
| State machine | `TicketStateMachineTest` — all valid/invalid transitions |
| Integration | `@SpringBootTest` + H2 — ticket CRUD, transition 409 |
| RAG | `AskControllerTest` — mock retrieval empty vs populated |
| Ingestion | `TicketKnowledgeIngesterTest` — chunk count per ticket |

## Frontend

- Smoke: list loads, create ticket form validation (optional Playwright later)

## CI Command

```bash
cd backend && ./mvnw -q test
```

## Deterministic vs Probabilistic

- State machine: 100% deterministic assertions
- LLM answers: assert JSON schema, `sources` subset of retrieved IDs, abstention path without calling real LLM in CI
