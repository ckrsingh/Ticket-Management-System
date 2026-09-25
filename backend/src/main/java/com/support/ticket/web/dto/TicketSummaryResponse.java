package com.support.ticket.web.dto;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketPriority;
import com.support.ticket.domain.TicketStatus;
import java.time.Instant;

public record TicketSummaryResponse(
        String publicId,
        String title,
        TicketStatus status,
        TicketPriority priority,
        String category,
        String assignee,
        Instant updatedAt) {

    public static TicketSummaryResponse from(Ticket ticket) {
        return new TicketSummaryResponse(
                ticket.getPublicId(),
                ticket.getTitle(),
                ticket.getStatus(),
                ticket.getPriority(),
                ticket.getCategory(),
                ticket.getAssignee(),
                ticket.getUpdatedAt());
    }
}
