# Java Spring Boot Guidelines

- Java 21, Spring Boot 3.3+, records for immutable DTOs where practical.
- Layering: `web` → `service` → `repository`; no JPA in controllers.
- Use `@Valid` on request bodies; centralize errors in `@RestControllerAdvice`.
- Constructor injection only; avoid field `@Autowired`.
- Transactions on service layer (`@Transactional` read-only for queries).
- Public API IDs (`TKT-*`) separate from internal `Long` id.
- State changes only via `TicketStateMachine`, never direct status assignment.
- Spring AI: keep RAG prompts in config; no hardcoded top-K/threshold in code.
- Profiles: `test` (deterministic embeddings), `dev` (H2 + SimpleVectorStore), `prod` (PostgreSQL + PgVector optional).
