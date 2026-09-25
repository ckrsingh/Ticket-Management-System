# Ticket Management System

GitHub: [ckrsingh/-Ticket-Management-System](https://github.com/ckrsingh/-Ticket-Management-System) (repo name has a **leading hyphen**)

Local folder: `~/cursor-projects/Ticket-Management-System` (your machine) — push help: [`docs/github-push.md`](docs/github-push.md).

Spec-driven support desk with **Java 21 / Spring Boot / Spring AI** backend, **Next.js** UI, and **RAG** over ticket history (`POST /api/ai/ask`).

## Repository layout

| Path | Purpose |
|------|---------|
| `spec/` | Requirements, architecture, API, RAG, tests (source of truth) |
| `rules/` | AI steering: Spring, testing, API, RAG |
| `commands/` | Review code/spec, generate tests, review RAG grounding |
| `.cursor/skills/` | Documentation + prompt recording |
| `docs/` | Prompt history, AI mistakes log, token notes |
| `.specstory/history/` | Saved session prompts |
| `backend/` | Spring Boot API |
| `frontend/` | Next.js UI |

## Quick start

### Backend

```bash
cd backend
# requires JDK 21 and Maven (or use downloaded Maven)
mvn spring-boot:run
```

- API: `http://localhost:8080/api`
- H2 console (dev): `http://localhost:8080/h2-console`
- Demo tickets seeded on first run; embeddings re-indexed on changes.

**Ollama (recommended for real RAG answers):**

```bash
ollama pull nomic-embed-text
ollama pull llama3.2
```

Without Ollama, the app falls back to deterministic embeddings + stub chat (dev only).

**RAG tuning (env):**

- `APP_RAG_TOP_K` (default `5`)
- `APP_RAG_SIMILARITY_THRESHOLD` (default `0.65`)

### Frontend

```bash
cd frontend
cp .env.local.example .env.local
npm install
npm run dev
```

Open `http://localhost:3000`.

### Tests

```bash
cd backend && mvn clean test
```

## AI engineering artifacts

- Documented AI mistakes: [`docs/ai-mistakes-log.md`](docs/ai-mistakes-log.md)
- Hallucination review command: [`commands/review-rag-output.md`](commands/review-rag-output.md)
- Token usage: [`docs/token-optimization.md`](docs/token-optimization.md)

## Acceptance checklist

See [`spec/requirements.md`](spec/requirements.md) and exercise criteria in the initial prompt; architecture and chunking rationale in [`spec/architecture.md`](spec/architecture.md).
