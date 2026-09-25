const API_BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

export type TicketStatus =
  | "OPEN"
  | "IN_PROGRESS"
  | "RESOLVED"
  | "CLOSED"
  | "CANCELLED";

export type TicketPriority = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export interface TicketSummary {
  publicId: string;
  title: string;
  status: TicketStatus;
  priority: TicketPriority;
  category?: string;
  assignee?: string;
  updatedAt: string;
}

export interface Comment {
  id: number;
  author: string;
  body: string;
  createdAt: string;
}

export interface Ticket extends TicketSummary {
  description?: string;
  resolutionNotes?: string;
  createdAt: string;
  comments: Comment[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  fieldErrors?: { field: string; message: string }[];
}

export interface AskResponse {
  answer: string;
  grounded: boolean;
  sources: { ticketId: string; snippet: string; similarity: number }[];
  retrieval: { topK: number; similarityThreshold: number; chunksRetrieved: number };
}

async function parseJson<T>(res: Response): Promise<T> {
  const text = await res.text();
  if (!text) {
    throw new Error("Empty response from API");
  }
  return JSON.parse(text) as T;
}

export async function listTickets(params: {
  status?: TicketStatus;
  q?: string;
  page?: number;
}): Promise<PageResponse<TicketSummary>> {
  const search = new URLSearchParams();
  if (params.status) search.set("status", params.status);
  if (params.q) search.set("q", params.q);
  if (params.page != null) search.set("page", String(params.page));
  const res = await fetch(`${API_BASE}/tickets?${search}`);
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export async function getTicket(publicId: string): Promise<Ticket> {
  const res = await fetch(`${API_BASE}/tickets/${publicId}`);
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export async function createTicket(body: {
  title: string;
  description?: string;
  priority?: TicketPriority;
  category?: string;
  assignee?: string;
}): Promise<Ticket> {
  const res = await fetch(`${API_BASE}/tickets`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export async function updateTicket(
  publicId: string,
  body: Partial<{
    title: string;
    description: string;
    priority: TicketPriority;
    category: string;
    assignee: string;
  }>
): Promise<Ticket> {
  const res = await fetch(`${API_BASE}/tickets/${publicId}`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export async function addComment(
  publicId: string,
  body: { author: string; body: string }
): Promise<Ticket> {
  const res = await fetch(`${API_BASE}/tickets/${publicId}/comments`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export async function transitionTicket(
  publicId: string,
  body: { targetStatus: TicketStatus; resolutionNotes?: string }
): Promise<Ticket> {
  const res = await fetch(`${API_BASE}/tickets/${publicId}/transitions`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export async function askAssistant(question: string): Promise<AskResponse> {
  const res = await fetch(`${API_BASE}/ai/ask`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ question }),
  });
  if (!res.ok) throw await parseJson<ApiError>(res);
  return parseJson(res);
}

export function formatApiError(err: unknown): string {
  if (err && typeof err === "object" && "message" in err) {
    const api = err as ApiError;
    if (api.fieldErrors?.length) {
      return api.fieldErrors.map((f) => `${f.field}: ${f.message}`).join("; ");
    }
    return api.message;
  }
  return err instanceof Error ? err.message : "Unknown error";
}
