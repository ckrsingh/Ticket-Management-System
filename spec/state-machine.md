# Ticket State Machine

## States

- OPEN
- IN_PROGRESS
- RESOLVED
- CLOSED
- CANCELLED (terminal)

## Allowed Transitions

| From | To |
|------|-----|
| OPEN | IN_PROGRESS, CANCELLED |
| IN_PROGRESS | RESOLVED, CANCELLED |
| RESOLVED | CLOSED |
| CLOSED | — |
| CANCELLED | — |

## Rejected Examples

- CLOSED → OPEN
- RESOLVED → OPEN
- CANCELLED → OPEN
- OPEN → RESOLVED (must go through IN_PROGRESS)

## Implementation Rules

1. All transitions go through `TicketStateMachine.transition(current, target)`.
2. `RESOLVED` requires non-blank `resolutionNotes`.
3. Invalid transition → HTTP 409 Conflict with clear message.
