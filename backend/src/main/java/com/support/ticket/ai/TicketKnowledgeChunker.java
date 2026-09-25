package com.support.ticket.ai;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketComment;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TicketKnowledgeChunker {

    static final int MAX_CHUNK_CHARS = 1500;

    public List<ChunkDraft> chunk(Ticket ticket) {
        List<ChunkDraft> chunks = new ArrayList<>();
        String header = buildHeader(ticket);
        addChunk(chunks, ticket, "HEADER", 0, header);

        int commentIndex = 0;
        for (TicketComment comment : ticket.getComments()) {
            String text = "[" + comment.getAuthor() + " @ " + comment.getCreatedAt() + "] " + comment.getBody();
            splitAndAdd(chunks, ticket, "COMMENT", commentIndex++, text);
        }

        if (StringUtils.hasText(ticket.getResolutionNotes())) {
            String resolution = "Resolution notes: " + ticket.getResolutionNotes();
            splitAndAdd(chunks, ticket, "RESOLUTION", 0, resolution);
        }
        return chunks;
    }

    private void splitAndAdd(List<ChunkDraft> chunks, Ticket ticket, String section, int baseIndex, String text) {
        if (text.length() <= MAX_CHUNK_CHARS) {
            addChunk(chunks, ticket, section, baseIndex, text);
            return;
        }
        String[] paragraphs = text.split("\n\n");
        StringBuilder buffer = new StringBuilder();
        int part = 0;
        for (String paragraph : paragraphs) {
            if (buffer.length() + paragraph.length() + 2 > MAX_CHUNK_CHARS && buffer.length() > 0) {
                addChunk(chunks, ticket, section, baseIndex * 100 + part++, buffer.toString());
                buffer = new StringBuilder();
            }
            if (buffer.length() > 0) {
                buffer.append("\n\n");
            }
            buffer.append(paragraph);
        }
        if (buffer.length() > 0) {
            addChunk(chunks, ticket, section, baseIndex * 100 + part, buffer.toString());
        }
    }

    private static String buildHeader(Ticket ticket) {
        return "Ticket " + ticket.getPublicId() + "\n"
                + "Title: " + ticket.getTitle() + "\n"
                + "Status: " + ticket.getStatus() + "\n"
                + "Priority: " + ticket.getPriority() + "\n"
                + "Category: " + nullToEmpty(ticket.getCategory()) + "\n"
                + "Assignee: " + nullToEmpty(ticket.getAssignee()) + "\n"
                + "Description: " + nullToEmpty(ticket.getDescription());
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private void addChunk(List<ChunkDraft> chunks, Ticket ticket, String section, int chunkIndex, String text) {
        if (!StringUtils.hasText(text)) {
            return;
        }
        String docId = ticket.getPublicId() + "#" + section + "#" + chunkIndex;
        chunks.add(new ChunkDraft(docId, text, section, chunkIndex));
    }

    public record ChunkDraft(String docId, String text, String section, int chunkIndex) {}
}
