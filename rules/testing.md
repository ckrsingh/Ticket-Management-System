# Testing Guidelines

- Unit tests for pure logic (state machine, chunking) without Spring context.
- `@WebMvcTest` for controller validation; `@DataJpaTest` for repositories.
- Integration tests use `test` profile with `DeterministicEmbeddingModel`.
- Do not call real LLM APIs in CI; mock `ChatModel` or use stub responses.
- Assert HTTP status and JSON structure for error paths (400, 409).
- Name tests: `shouldRejectClosedToOpenTransition`.
- Seed data via `data.sql` or test fixtures, not copy-paste in every test.
- After RAG changes, run golden questions manually (see `spec/evaluation-strategy.md`).
