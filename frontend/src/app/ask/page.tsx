"use client";

import Link from "next/link";
import { useState } from "react";
import { askAssistant, formatApiError, type ApiError, type AskResponse } from "@/lib/api";

export default function AskPage() {
  const [question, setQuestion] = useState("Have we seen payment failures before?");
  const [result, setResult] = useState<AskResponse | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  async function onAsk(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    setError(null);
    setResult(null);
    try {
      const res = await askAssistant(question);
      setResult(res);
    } catch (err) {
      setError(formatApiError(err as ApiError));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div>
        <h1 className="text-2xl font-semibold">Ticket assistant</h1>
        <p className="text-sm text-slate-600">
          Answers are grounded in indexed ticket data only.{" "}
          <Link href="/" className="text-indigo-600 hover:underline">Tickets</Link>
        </p>
      </div>

      <form onSubmit={onAsk} className="space-y-3 rounded-lg border border-slate-200 bg-white p-6">
        <label className="block text-sm font-medium">
          Question
          <textarea
            required
            minLength={3}
            className="mt-1 w-full rounded border border-slate-300 px-3 py-2"
            rows={3}
            value={question}
            onChange={(e) => setQuestion(e.target.value)}
          />
        </label>
        <button
          type="submit"
          disabled={loading}
          className="rounded-md bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-500 disabled:opacity-50"
        >
          {loading ? "Asking…" : "Ask"}
        </button>
      </form>

      {error && (
        <div className="rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">{error}</div>
      )}

      {result && (
        <div className="space-y-4 rounded-lg border border-slate-200 bg-white p-6">
          <div className="flex items-center gap-2 text-sm">
            <span
              className={
                result.grounded
                  ? "rounded bg-emerald-100 px-2 py-0.5 text-emerald-800"
                  : "rounded bg-amber-100 px-2 py-0.5 text-amber-900"
              }
            >
              {result.grounded ? "Grounded" : "No match"}
            </span>
            <span className="text-slate-500">
              topK={result.retrieval.topK}, threshold={result.retrieval.similarityThreshold}
            </span>
          </div>
          <p className="whitespace-pre-wrap text-sm leading-relaxed">{result.answer}</p>
          {result.sources.length > 0 && (
            <div>
              <h2 className="text-sm font-medium">Sources</h2>
              <ul className="mt-2 space-y-2 text-sm">
                {result.sources.map((s) => (
                  <li key={s.ticketId} className="rounded bg-slate-50 px-3 py-2">
                    <Link href={`/tickets/${s.ticketId}`} className="font-mono text-indigo-600 hover:underline">
                      {s.ticketId}
                    </Link>
                    <span className="text-slate-500"> · similarity {s.similarity.toFixed(2)}</span>
                    <p className="mt-1 text-slate-600">{s.snippet}</p>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
