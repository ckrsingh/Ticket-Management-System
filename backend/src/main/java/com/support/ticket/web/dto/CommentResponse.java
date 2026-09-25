package com.support.ticket.web.dto;

import com.support.ticket.domain.TicketComment;
import java.time.Instant;

public record CommentResponse(Long id, String author, String body, Instant createdAt) {

    public static CommentResponse from(TicketComment c) {
        return new CommentResponse(c.getId(), c.getAuthor(), c.getBody(), c.getCreatedAt());
    }
}
