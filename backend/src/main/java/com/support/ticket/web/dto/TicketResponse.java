package com.support.ticket.web.dto;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketPriority;
import com.support.ticket.domain.TicketStatus;
import java.time.Instant;
import java.util.List;

public record TicketResponse(
        String publicId,
        String title,
        String description,
        TicketStatus status,
        TicketPriority priority,
        String category,
        String assignee,
        String resolutionNotes,
        Instant createdAt,
        Instant updatedAt,
        List<CommentResponse> comments) {

    public static TicketResponse from(Ticket ticket) {
        List<CommentResponse> comments =
                ticket.getComments().stream().map(CommentResponse::from).toList();
        return new TicketResponse(
                ticket.getPublicId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getAssignee(),
                ticket.getResolutionNotes(),
                ticket.getCreatedAt(),
                ticket.getUpdatedAt(),
                comments);
    }
}
