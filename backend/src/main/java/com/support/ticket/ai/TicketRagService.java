package com.support.ticket.ai;

import com.support.ticket.config.RagProperties;
import com.support.ticket.web.dto.AskRequest;
import com.support.ticket.web.dto.AskResponse;
import com.support.ticket.web.dto.AskSource;
import com.support.ticket.web.dto.RetrievalMeta;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

@Service
public class TicketRagService {

    private static final String NO_MATCH_ANSWER =
            "No relevant tickets were found in the knowledge base for this question.";

    private final TicketKnowledgeIngester ingester;
    private final ChatModel chatModel;
    private final RagProperties ragProperties;

    public TicketRagService(TicketKnowledgeIngester ingester, ChatModel chatModel, RagProperties ragProperties) {
        this.ingester = ingester;
        this.chatModel = chatModel;
        this.ragProperties = ragProperties;
    }

    public AskResponse ask(AskRequest request) {
        int topK = ragProperties.getTopK();
        double threshold = ragProperties.getSimilarityThreshold();
        List<Document> documents = ingester.search(request.question(), topK, threshold);
        RetrievalMeta retrieval = new RetrievalMeta(topK, threshold, documents.size());

        if (documents.isEmpty()) {
            return new AskResponse(NO_MATCH_ANSWER, false, List.of(), retrieval);
        }

        String context = buildContext(documents);
        String userPrompt = "Question: " + request.question() + "\n\nTicket excerpts:\n" + context;

        String answer = chatModel
                .call(new Prompt(List.of(new SystemMessage(ragProperties.getSystemPrompt()), new UserMessage(userPrompt))))
                .getResult()
                .getOutput()
                .getText();

        List<AskSource> sources = toSources(documents);
        return new AskResponse(answer, true, sources, retrieval);
    }

    private static String buildContext(List<Document> documents) {
        StringBuilder sb = new StringBuilder();
        for (Document doc : documents) {
            String ticketId = String.valueOf(doc.getMetadata().getOrDefault("ticketId", "unknown"));
            sb.append("--- ").append(ticketId).append(" ---\n");
            sb.append(doc.getText()).append("\n");
        }
        return sb.toString();
    }

    private static List<AskSource> toSources(List<Document> documents) {
        Set<String> seen = new LinkedHashSet<>();
        List<AskSource> sources = new ArrayList<>();
        for (Document doc : documents) {
            String ticketId = String.valueOf(doc.getMetadata().getOrDefault("ticketId", "unknown"));
            if (seen.add(ticketId)) {
                double similarity = similarityFrom(doc);
                String snippet = doc.getText().length() > 200 ? doc.getText().substring(0, 200) + "..." : doc.getText();
                sources.add(new AskSource(ticketId, snippet, similarity));
            }
        }
        return sources;
    }

    private static double similarityFrom(Document doc) {
        Object distance = doc.getMetadata().get("distance");
        if (distance instanceof Number number) {
            return 1.0 - number.doubleValue();
        }
        return 0.0;
    }
}
