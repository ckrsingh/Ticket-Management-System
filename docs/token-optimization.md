# Token Optimisation

## Static context (prompt caching)

- Keep stable instructions in `rules/` and `spec/` rather than re-pasting in every chat.
- RAG system prompt lives in `application.yml` → `app.rag.system-prompt` (unchanging prefix for provider-side caching where supported).

## Navigation tools

- Prefer **codebase-memory** / **Graphify** graph queries for “who calls X” instead of uploading whole files.
- Use `commands/review-code.md` with a focused diff instead of full-repo reviews.

## RAG cost

- Dev: Ollama local embeddings (no per-token API cost).
- Tune `top-k` and threshold via env to reduce context size sent to the chat model.

## Cursor

- Project rules in `rules/` (mirror key items in `.cursor/rules/` if desired).
- Skills under `.cursor/skills/` for repeatable workflows.
