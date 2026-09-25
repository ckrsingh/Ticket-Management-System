# API Standards

- REST JSON under `/api`; use plural nouns (`/tickets`).
- Use correct HTTP verbs; transitions as `POST .../transitions` (not PATCH status).
- Error body: `timestamp`, `status`, `error`, `message`, optional `fieldErrors[]`.
- Pagination: `page` (0-based), `size` (default 20, max 100).
- Enums as uppercase strings in JSON.
- `Location` header optional on 201 create.
- CORS enabled for local Next.js origin only in dev.
- Versioning: not required for this exercise; document breaking changes in spec.
