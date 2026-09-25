import type { TicketStatus } from "./api";

const NEXT: Record<TicketStatus, TicketStatus[]> = {
  OPEN: ["IN_PROGRESS", "CANCELLED"],
  IN_PROGRESS: ["RESOLVED", "CANCELLED"],
  RESOLVED: ["CLOSED"],
  CLOSED: [],
  CANCELLED: [],
};

export function allowedTransitions(status: TicketStatus): TicketStatus[] {
  return NEXT[status] ?? [];
}
