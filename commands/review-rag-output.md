# Command: Review RAG Output (Hallucination / Grounding)

```
For the last POST /api/ai/ask response:
1) List every factual claim in the answer.
2) Map each claim to a source snippet ticketId or mark UNGROUNDED.
3) Flag any ticketId or resolution detail not present in retrieved sources.
4) If grounded=false, verify the answer does not invent ticket IDs or fixes.
5) Compare behavior to rules/rag-vector-store.md guardrails.
Output: PASS/FAIL with bullet evidence.
```
