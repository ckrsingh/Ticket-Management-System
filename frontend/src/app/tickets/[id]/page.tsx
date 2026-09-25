"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import {
  addComment,
  formatApiError,
  getTicket,
  transitionTicket,
  updateTicket,
  type ApiError,
  type Ticket,
  type TicketPriority,
  type TicketStatus,
} from "@/lib/api";
import { allowedTransitions } from "@/lib/transitions";

export default function TicketDetailPage() {
  const params = useParams();
  const publicId = params.id as string;
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [resolutionNotes, setResolutionNotes] = useState("");
  const [commentAuthor, setCommentAuthor] = useState("");
  const [commentBody, setCommentBody] = useState("");

  const load = useCallback(async () => {
    setError(null);
    try {
      const t = await getTicket(publicId);
      setTicket(t);
    } catch (e) {
      setError(formatApiError(e as ApiError));
    }
  }, [publicId]);

  useEffect(() => {
    void load();
  }, [load]);

  async function saveField(field: "title" | "assignee" | "priority", value: string) {
    if (!ticket) return;
    setError(null);
    try {
      const updated = await updateTicket(publicId, {
        [field]: field === "priority" ? (value as TicketPriority) : value,
      });
      setTicket(updated);
    } catch (e) {
      setError(formatApiError(e as ApiError));
    }
  }

  async function onTransition(target: TicketStatus) {
    setError(null);
    try {
      const updated = await transitionTicket(publicId, {
        targetStatus: target,
        resolutionNotes: target === "RESOLVED" ? resolutionNotes : undefined,
      });
      setTicket(updated);
    } catch (e) {
      setError(formatApiError(e as ApiError));
    }
  }

  async function onAddComment(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    try {
      const updated = await addComment(publicId, { author: commentAuthor, body: commentBody });
      setTicket(updated);
      setCommentBody("");
    } catch (err) {
      setError(formatApiError(err as ApiError));
    }
  }

  if (!ticket && !error) {
    return <p className="text-sm text-slate-500">Loading…</p>;
  }

  if (!ticket) {
    return (
      <div className="space-y-4">
        <Link href="/" className="text-sm text-indigo-600 hover:underline">← Tickets</Link>
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div>
      </div>
    );
  }

  const next = allowedTransitions(ticket.status);

  return (
    <div className="space-y-8">
      <div>
        <Link href="/" className="text-sm text-indigo-600 hover:underline">← Tickets</Link>
        <p className="font-mono text-sm text-slate-500">{ticket.publicId}</p>
        <h1 className="text-2xl font-semibold">{ticket.title}</h1>
        <p className="mt-1 text-sm text-slate-600">Status: {ticket.status} · Priority: {ticket.priority}</p>
      </div>

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
          {error}
        </div>
      )}

      <section className="rounded-lg border border-slate-200 bg-white p-6 space-y-4">
        <h2 className="font-medium">Details</h2>
        <p className="text-sm whitespace-pre-wrap">{ticket.description || "—"}</p>
        <div className="grid gap-3 sm:grid-cols-2">
          <label className="text-sm">
            Title
            <input
              className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
              defaultValue={ticket.title}
              onBlur={(e) => {
                if (e.target.value !== ticket.title) void saveField("title", e.target.value);
              }}
            />
          </label>
          <label className="text-sm">
            Assignee
            <input
              className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
              defaultValue={ticket.assignee ?? ""}
              onBlur={(e) => {
                if (e.target.value !== (ticket.assignee ?? "")) void saveField("assignee", e.target.value);
              }}
            />
          </label>
          <label className="text-sm">
            Priority
            <select
              className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
              value={ticket.priority}
              onChange={(e) => void saveField("priority", e.target.value)}
            >
              {(["LOW", "MEDIUM", "HIGH", "CRITICAL"] as TicketPriority[]).map((p) => (
                <option key={p} value={p}>{p}</option>
              ))}
            </select>
          </label>
        </div>
      </section>

      {next.length > 0 && (
        <section className="rounded-lg border border-slate-200 bg-white p-6 space-y-3">
          <h2 className="font-medium">Status transitions</h2>
          {next.includes("RESOLVED") && (
            <textarea
              className="w-full rounded border border-slate-300 px-3 py-2 text-sm"
              placeholder="Resolution notes (required when resolving)"
              value={resolutionNotes}
              onChange={(e) => setResolutionNotes(e.target.value)}
              rows={3}
            />
          )}
          <div className="flex flex-wrap gap-2">
            {next.map((s) => (
              <button
                key={s}
                type="button"
                onClick={() => void onTransition(s)}
                className="rounded-md border border-slate-300 px-3 py-1.5 text-sm hover:bg-slate-50"
              >
                → {s}
              </button>
            ))}
          </div>
        </section>
      )}

      {ticket.resolutionNotes && (
        <section className="rounded-lg border border-emerald-200 bg-emerald-50 p-4 text-sm">
          <h2 className="font-medium text-emerald-900">Resolution</h2>
          <p className="mt-1 whitespace-pre-wrap text-emerald-800">{ticket.resolutionNotes}</p>
        </section>
      )}

      <section className="rounded-lg border border-slate-200 bg-white p-6 space-y-4">
        <h2 className="font-medium">Comments</h2>
        <ul className="space-y-3">
          {ticket.comments.map((c) => (
            <li key={c.id} className="rounded bg-slate-50 px-3 py-2 text-sm">
              <span className="font-medium">{c.author}</span>
              <span className="text-slate-500"> · {new Date(c.createdAt).toLocaleString()}</span>
              <p className="mt-1 whitespace-pre-wrap">{c.body}</p>
            </li>
          ))}
        </ul>
        <form onSubmit={onAddComment} className="space-y-2 border-t border-slate-100 pt-4">
          <input
            required
            placeholder="Your name"
            className="w-full rounded border border-slate-300 px-3 py-2 text-sm"
            value={commentAuthor}
            onChange={(e) => setCommentAuthor(e.target.value)}
          />
          <textarea
            required
            placeholder="Comment"
            className="w-full rounded border border-slate-300 px-3 py-2 text-sm"
            rows={3}
            value={commentBody}
            onChange={(e) => setCommentBody(e.target.value)}
          />
          <button type="submit" className="rounded-md bg-slate-800 px-4 py-2 text-sm text-white hover:bg-slate-700">
            Add comment
          </button>
        </form>
      </section>
    </div>
  );
}
