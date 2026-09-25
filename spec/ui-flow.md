# UI Flow

## Pages

1. **Ticket list** — table with status filter, search box, link to detail, “New ticket”
2. **Ticket detail** — fields, status transition buttons (only valid next states), comments thread, add comment
3. **Ask assistant** — question input, answer + cited tickets list

## Error UX

- API `fieldErrors` → inline under inputs
- 409 transition errors → toast/banner with server `message`
- AI 503 → “Assistant unavailable (check Ollama)”

## State Transition UI

Buttons derived from same rules as backend (optional client mirror); server remains authoritative.
