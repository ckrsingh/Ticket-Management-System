# Data Model

## Ticket

| Field | Type | Notes |
|-------|------|-------|
| id | BIGINT PK | Internal |
| public_id | VARCHAR UNIQUE | e.g. `TKT-1001` |
| title | VARCHAR(200) NOT NULL | |
| description | TEXT | |
| status | ENUM | OPEN, IN_PROGRESS, RESOLVED, CLOSED, CANCELLED |
| priority | ENUM | LOW, MEDIUM, HIGH, CRITICAL |
| category | VARCHAR(100) | Optional |
| assignee | VARCHAR(120) | Optional |
| resolution_notes | TEXT | Set when moving to RESOLVED |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

## Comment

| Field | Type | Notes |
|-------|------|-------|
| id | BIGINT PK | |
| ticket_id | FK → ticket | |
| author | VARCHAR(120) | |
| body | TEXT NOT NULL | |
| created_at | TIMESTAMP | |

## Vector Metadata (per chunk)

- `ticketId` (public_id)
- `status`, `priority`, `assignee`, `category`
- `section` (HEADER, COMMENT, RESOLUTION)
- `chunkIndex`

## Indexes

- `ticket(status)`, `ticket(public_id)`
- Full-text or LIKE search on title/description (H2: `LOWER(title) LIKE`)
