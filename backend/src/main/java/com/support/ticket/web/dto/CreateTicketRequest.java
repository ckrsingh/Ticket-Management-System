package com.support.ticket.web.dto;

import com.support.ticket.domain.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10000) String description,
        TicketPriority priority,
        @Size(max = 100) String category,
        @Size(max = 120) String assignee) {}
