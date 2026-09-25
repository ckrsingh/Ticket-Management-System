package com.support.ticket.ai;

import com.support.ticket.domain.Ticket;
import com.support.ticket.repository.TicketRepository;
import com.support.ticket.service.TicketNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketKnowledgeIngester {

    private final TicketRepository ticketRepository;
    private final TicketKnowledgeChunker chunker;
    private final VectorStore vectorStore;

    public TicketKnowledgeIngester(
            TicketRepository ticketRepository,
            TicketKnowledgeChunker chunker,
            VectorStore vectorStore) {
        this.ticketRepository = ticketRepository;
        this.chunker = chunker;
        this.vectorStore = vectorStore;
    }

    @Transactional(readOnly = true)
    public void ingest(String publicId) {
        Ticket ticket = ticketRepository.findByPublicId(publicId).orElseThrow(() -> new TicketNotFoundException(publicId));
        deleteForTicket(publicId);
        List<TicketKnowledgeChunker.ChunkDraft> drafts = chunker.chunk(ticket);
        if (drafts.isEmpty()) {
            return;
        }
        List<Document> documents = drafts.stream().map(d -> toDocument(ticket, d)).toList();
        vectorStore.add(documents);
    }

    private void deleteForTicket(String publicId) {
        var filter = new FilterExpressionBuilder().eq("ticketId", publicId).build();
        SearchRequest request =
                SearchRequest.builder().query("ticket").topK(1000).filterExpression(filter).build();
        List<String> ids = vectorStore.similaritySearch(request).stream().map(Document::getId).toList();
        if (!ids.isEmpty()) {
            vectorStore.delete(ids);
        }
    }

    private Document toDocument(Ticket ticket, TicketKnowledgeChunker.ChunkDraft draft) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ticketId", ticket.getPublicId());
        metadata.put("status", ticket.getStatus().name());
        metadata.put("priority", ticket.getPriority().name());
        metadata.put("assignee", ticket.getAssignee() == null ? "" : ticket.getAssignee());
        metadata.put("category", ticket.getCategory() == null ? "" : ticket.getCategory());
        metadata.put("section", draft.section());
        metadata.put("chunkIndex", draft.chunkIndex());
        return new Document(draft.docId(), draft.text(), metadata);
    }

    public List<Document> search(String query, int topK, double similarityThreshold) {
        SearchRequest request = SearchRequest.builder().query(query).topK(topK).similarityThreshold(similarityThreshold).build();
        return vectorStore.similaritySearch(request);
    }
}
