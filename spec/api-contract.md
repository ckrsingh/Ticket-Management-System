# REST API Contract

Base: `/api`

## Tickets

| Method | Path | Description |
|--------|------|-------------|
| POST | `/tickets` | Create ticket |
| GET | `/tickets` | List (`status`, `q`, `page`, `size`) |
| GET | `/tickets/{publicId}` | Details |
| PATCH | `/tickets/{publicId}` | Update fields |
| POST | `/tickets/{publicId}/comments` | Add comment |
| POST | `/tickets/{publicId}/transitions` | Change status |

### Create Ticket

```json
POST /api/tickets
{
  "title": "Payment failed at checkout",
  "description": "Customer card declined",
  "priority": "HIGH",
  "category": "billing",
  "assignee": "alice@example.com"
}
```

### Transition

```json
POST /api/tickets/TKT-1001/transitions
{
  "targetStatus": "IN_PROGRESS"
}
```

When `targetStatus` is `RESOLVED`, optional `resolutionNotes` required.

### Error Shape

```json
{
  "timestamp": "2026-09-25T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid status transition",
  "fieldErrors": []
}
```

## AI

See `rag-api-contract.md`.
