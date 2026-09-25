---
name: prompt-history
description: Append each user prompt to .specstory/history and docs/prompt-history.md for auditability.
---

# Prompt History Skill

After each substantive user prompt in this repo:

1. Append a one-line summary with ISO date to `docs/prompt-history.md` (table row).
2. If the prompt is long or assessment-critical, add `/.specstory/history/YYYY-MM-DD-<slug>.md` with the full text.

Do not store secrets or API keys in history files.
