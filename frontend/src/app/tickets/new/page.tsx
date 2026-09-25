"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";
import { useState } from "react";
import {
  createTicket,
  formatApiError,
  type ApiError,
  type TicketPriority,
} from "@/lib/api";

export default function NewTicketPage() {
  const router = useRouter();
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [priority, setPriority] = useState<TicketPriority>("MEDIUM");
  const [category, setCategory] = useState("");
  const [assignee, setAssignee] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const ticket = await createTicket({
        title,
        description,
        priority,
        category: category || undefined,
        assignee: assignee || undefined,
      });
      router.push(`/tickets/${ticket.publicId}`);
    } catch (err) {
      setError(formatApiError(err as ApiError));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="mx-auto max-w-lg space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">New ticket</h1>
        <p className="text-sm text-slate-600">
          <Link href="/" className="text-indigo-600 hover:underline">Back to list</Link>
        </p>
      </div>

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div>
      )}

      <form onSubmit={onSubmit} className="space-y-4 rounded-lg border border-slate-200 bg-white p-6">
        <label className="block text-sm font-medium">
          Title *
          <input
            required
            maxLength={200}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          />
        </label>
        <label className="block text-sm font-medium">
          Description
          <textarea
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            rows={4}
            value={description}
            onChange={(e) => setDescription(e.target.value)}
          />
        </label>
        <label className="block text-sm font-medium">
          Priority
          <select
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            value={priority}
            onChange={(e) => setPriority(e.target.value as TicketPriority)}
          >
            {(["LOW", "MEDIUM", "HIGH", "CRITICAL"] as TicketPriority[]).map((p) => (
              <option key={p} value={p}>{p}</option>
            ))}
          </select>
        </label>
        <label className="block text-sm font-medium">
          Category
          <input
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            value={category}
            onChange={(e) => setCategory(e.target.value)}
          />
        </label>
        <label className="block text-sm font-medium">
          Assignee
          <input
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            value={assignee}
            onChange={(e) => setAssignee(e.target.value)}
          />
        </label>
        <button
          type="submit"
          disabled={submitting}
          className="w-full rounded-md bg-indigo-600 py-2 text-sm font-medium text-white hover:bg-indigo-500 disabled:opacity-50"
        >
          {submitting ? "Creating…" : "Create ticket"}
        </button>
      </form>
    </div>
  );
}
