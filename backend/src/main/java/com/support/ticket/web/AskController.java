package com.support.ticket.web;

import com.support.ticket.ai.TicketRagService;
import com.support.ticket.web.dto.AskRequest;
import com.support.ticket.web.dto.AskResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AskController {

    private final TicketRagService ticketRagService;

    public AskController(TicketRagService ticketRagService) {
        this.ticketRagService = ticketRagService;
    }

    @PostMapping("/ask")
    public AskResponse ask(@Valid @RequestBody AskRequest request) {
        return ticketRagService.ask(request);
    }
}
