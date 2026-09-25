package com.support.ticket.web;

import com.support.ticket.domain.TicketStatus;
import com.support.ticket.service.TicketService;
import com.support.ticket.web.dto.AddCommentRequest;
import com.support.ticket.web.dto.CreateTicketRequest;
import com.support.ticket.web.dto.PageResponse;
import com.support.ticket.web.dto.TicketResponse;
import com.support.ticket.web.dto.TicketSummaryResponse;
import com.support.ticket.web.dto.TicketTransitionRequest;
import com.support.ticket.web.dto.UpdateTicketRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return TicketResponse.from(ticketService.create(request));
    }

    @GetMapping
    public PageResponse<TicketSummaryResponse> list(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        var pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
        var result = ticketService.list(status, query, pageable).map(TicketSummaryResponse::from);
        return PageResponse.from(result);
    }

    @GetMapping("/{publicId}")
    public TicketResponse get(@PathVariable String publicId) {
        return TicketResponse.from(ticketService.getRequired(publicId));
    }

    @PatchMapping("/{publicId}")
    public TicketResponse update(@PathVariable String publicId, @Valid @RequestBody UpdateTicketRequest request) {
        return TicketResponse.from(ticketService.update(publicId, request));
    }

    @PostMapping("/{publicId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse addComment(
            @PathVariable String publicId, @Valid @RequestBody AddCommentRequest request) {
        return TicketResponse.from(ticketService.addComment(publicId, request));
    }

    @PostMapping("/{publicId}/transitions")
    public TicketResponse transition(
            @PathVariable String publicId, @Valid @RequestBody TicketTransitionRequest request) {
        return TicketResponse.from(ticketService.transition(publicId, request));
    }
}
