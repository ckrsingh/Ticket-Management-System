# Requirements — AI-Powered Support Ticket Management

## Functional

| ID | Requirement |
|----|-------------|
| FR-1 | Create a support ticket (title, description, priority, optional category, assignee). |
| FR-2 | List tickets with pagination. |
| FR-3 | View ticket details including comments and resolution notes. |
| FR-4 | Update title, description, priority, assignee. |
| FR-5 | Add comments to a ticket. |
| FR-6 | Keyword search across title and description. |
| FR-7 | Filter tickets by status. |
| FR-8 | Persist all data in a database; survive restart. |
| FR-9 | Backend input validation with structured error responses. |
| FR-10 | UI displays validation and business-rule errors clearly. |
| FR-11 | Enforce ticket status state machine on the server. |
| FR-12 | Natural-language Q&A over ticket history via `POST /api/ai/ask`. |
| FR-13 | Answers cite ticket IDs used; no fabrication when no matches. |

## Non-Functional

| ID | Requirement |
|----|-------------|
| NFR-1 | Java 21, Spring Boot 3.x, Spring AI. |
| NFR-2 | Embeddings + vector store (PGVector prod profile; SimpleVectorStore dev). |
| NFR-3 | Configurable retrieval `top-k` and similarity threshold. |
| NFR-4 | Re-ingest embeddings on ticket update/close. |
| NFR-5 | No secrets in repository. |
| NFR-6 | Spec-driven development; documented AI guardrails. |

## Out of Scope

- Autonomous agents (create ticket, send email, tool chaining).
- Multi-tenant auth (optional basic API key for AI endpoint in prod only).

## Example Assistant Questions

- “Have we seen payment failures before?”
- “What was the resolution for ticket TKT-1001?”
- “What are the common causes of shipment tracking issues?”
- “Show me similar resolved tickets.”
- “Which high-priority tickets are related to payment?”
