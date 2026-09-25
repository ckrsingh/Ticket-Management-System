package com.support.ticket.web.dto;

public record RetrievalMeta(int topK, double similarityThreshold, int chunksRetrieved) {}
