package com.support.ticket.ai;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketComment;
import com.support.ticket.domain.TicketPriority;
import com.support.ticket.domain.TicketStatus;
import java.util.List;
import org.junit.jupiter.api.Test;

class TicketKnowledgeChunkerTest {

    private final TicketKnowledgeChunker chunker = new TicketKnowledgeChunker();

    @Test
    void producesHeaderAndCommentChunks() {
        Ticket ticket = new Ticket();
        ticket.setPublicId("TKT-1001");
        ticket.setTitle("Payment issue");
        ticket.setDescription("Failed charge");
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPriority(TicketPriority.HIGH);
        TicketComment comment = new TicketComment();
        comment.setAuthor("alice");
        comment.setBody("Investigating gateway logs");
        ticket.addComment(comment);

        List<TicketKnowledgeChunker.ChunkDraft> chunks = chunker.chunk(ticket);
        assertTrue(chunks.size() >= 2);
        assertTrue(chunks.stream().anyMatch(c -> c.section().equals("HEADER")));
        assertTrue(chunks.stream().anyMatch(c -> c.section().equals("COMMENT")));
    }
}
