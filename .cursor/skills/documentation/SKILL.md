---
name: documentation
description: >-
  Write and maintain project specs, API docs, and architecture notes for the
  support ticket system. Use when updating spec/, docs/, or README content.
---

# Documentation Skill

## When to use

- Before implementing a feature: update or create the relevant `spec/*.md` file.
- After API changes: sync `spec/api-contract.md` and `spec/rag-api-contract.md`.
- After RAG tuning: update `spec/architecture.md` and `rules/rag-vector-store.md`.

## Conventions

- Specs are the source of truth; code must match spec or spec must be updated in the same PR.
- Use tables for fields and endpoints; include example JSON.
- Document embedding/chunking decisions with justification, not only the final choice.
- Record AI mistakes in `docs/ai-mistakes-log.md` with date, mistake, correction.

## Outputs

- Prefer editing existing spec files over new ad-hoc markdown in random folders.
- Link related specs (e.g. ingestion → api contract).
