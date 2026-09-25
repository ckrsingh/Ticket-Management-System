package com.support.ticket.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rag")
public class RagProperties {

    private int topK = 5;
    private double similarityThreshold = 0.65;
    private String systemPrompt = """
            You are a support knowledge assistant. Answer ONLY using the ticket excerpts provided.
            If the excerpts do not contain enough information, say you cannot answer from ticket data.
            Do not use general world knowledge. Cite ticket IDs inline like [TKT-1001].
            """;

    public int getTopK() {
        return topK;
    }

    public void setTopK(int topK) {
        this.topK = topK;
    }

    public double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
}
