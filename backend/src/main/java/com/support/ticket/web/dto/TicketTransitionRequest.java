package com.support.ticket.web.dto;

import com.support.ticket.domain.TicketStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TicketTransitionRequest(
        @NotNull TicketStatus targetStatus,
        @Size(max = 5000) String resolutionNotes) {}
