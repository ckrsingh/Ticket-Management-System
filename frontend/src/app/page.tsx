"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import {
  formatApiError,
  listTickets,
  type ApiError,
  type TicketStatus,
  type TicketSummary,
} from "@/lib/api";

const STATUSES: TicketStatus[] = [
  "OPEN",
  "IN_PROGRESS",
  "RESOLVED",
  "CLOSED",
  "CANCELLED",
];

export default function HomePage() {
  const [tickets, setTickets] = useState<TicketSummary[]>([]);
  const [status, setStatus] = useState<TicketStatus | "">("");
  const [q, setQ] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const page = await listTickets({
        status: status || undefined,
        q: q || undefined,
      });
      setTickets(page.content);
    } catch (e) {
      setError(formatApiError(e as ApiError));
    } finally {
      setLoading(false);
    }
  }, [status, q]);

  useEffect(() => {
    void load();
  }, [load]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <h1 className="text-2xl font-semibold">Tickets</h1>
          <p className="text-sm text-slate-600">Search and filter support requests</p>
        </div>
        <Link
          href="/tickets/new"
          className="inline-flex justify-center rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-500"
        >
          Create ticket
        </Link>
      </div>

      <div className="flex flex-col gap-3 rounded-lg border border-slate-200 bg-white p-4 sm:flex-row">
        <label className="flex flex-1 flex-col gap-1 text-sm">
          Search
          <input
            className="rounded border border-slate-300 px-3 py-2"
            placeholder="Keyword in title or description"
            value={q}
            onChange={(e) => setQ(e.target.value)}
          />
        </label>
        <label className="flex flex-col gap-1 text-sm sm:w-48">
          Status
          <select
            className="rounded border border-slate-300 px-3 py-2"
            value={status}
            onChange={(e) => setStatus(e.target.value as TicketStatus | "")}
          >
            <option value="">All</option>
            {STATUSES.map((s) => (
              <option key={s} value={s}>{s}</option>
            ))}
          </select>
        </label>
        <button
          type="button"
          onClick={() => void load()}
          className="rounded-md border border-slate-300 px-4 py-2 text-sm font-medium hover:bg-slate-50 sm:self-end"
        >
          Apply
        </button>
      </div>

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800" role="alert">
          {error}
        </div>
      )}

      {loading ? (
        <p className="text-sm text-slate-500">Loading…</p>
      ) : tickets.length === 0 ? (
        <p className="text-sm text-slate-500">No tickets found.</p>
      ) : (
        <ul className="divide-y divide-slate-200 overflow-hidden rounded-lg border border-slate-200 bg-white">
          {tickets.map((t) => (
            <li key={t.publicId}>
              <Link
                href={`/tickets/${t.publicId}`}
                className="flex flex-col gap-1 px-4 py-3 hover:bg-slate-50 sm:flex-row sm:items-center sm:justify-between"
              >
                <div>
                  <span className="font-mono text-xs text-slate-500">{t.publicId}</span>
                  <p className="font-medium">{t.title}</p>
                </div>
                <div className="flex flex-wrap gap-2 text-xs">
                  <span className="rounded bg-slate-100 px-2 py-1">{t.status}</span>
                  <span className="rounded bg-indigo-50 px-2 py-1 text-indigo-800">{t.priority}</span>
                </div>
              </Link>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
