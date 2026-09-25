package com.support.ticket.service;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketComment;
import com.support.ticket.domain.TicketPriority;
import com.support.ticket.domain.TicketStatus;
import com.support.ticket.repository.TicketRepository;
import com.support.ticket.web.dto.AddCommentRequest;
import com.support.ticket.web.dto.CreateTicketRequest;
import com.support.ticket.web.dto.TicketTransitionRequest;
import com.support.ticket.web.dto.UpdateTicketRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStateMachine stateMachine;
    private final ApplicationEventPublisher events;

    public TicketService(
            TicketRepository ticketRepository,
            TicketStateMachine stateMachine,
            ApplicationEventPublisher events) {
        this.ticketRepository = ticketRepository;
        this.stateMachine = stateMachine;
        this.events = events;
    }

    @Transactional
    public Ticket create(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority() != null ? request.priority() : TicketPriority.MEDIUM);
        ticket.setCategory(request.category());
        ticket.setAssignee(request.assignee());
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPublicId("PENDING");
        Ticket saved = ticketRepository.save(ticket);
        saved.setPublicId(formatPublicId(saved.getId()));
        saved = ticketRepository.save(saved);
        publishChanged(saved.getPublicId());
        return reload(saved.getPublicId());
    }

    @Transactional
    public Ticket update(String publicId, UpdateTicketRequest request) {
        Ticket ticket = getRequired(publicId);
        if (request.title() != null) {
            ticket.setTitle(request.title());
        }
        if (request.description() != null) {
            ticket.setDescription(request.description());
        }
        if (request.priority() != null) {
            ticket.setPriority(request.priority());
        }
        if (request.assignee() != null) {
            ticket.setAssignee(request.assignee());
        }
        if (request.category() != null) {
            ticket.setCategory(request.category());
        }
        publishChanged(ticket.getPublicId());
        ticketRepository.save(ticket);
        return reload(publicId);
    }

    @Transactional
    public Ticket addComment(String publicId, AddCommentRequest request) {
        Ticket ticket = getRequired(publicId);
        TicketComment comment = new TicketComment();
        comment.setAuthor(request.author());
        comment.setBody(request.body());
        ticket.addComment(comment);
        publishChanged(ticket.getPublicId());
        ticketRepository.save(ticket);
        return reload(publicId);
    }

    @Transactional
    public Ticket transition(String publicId, TicketTransitionRequest request) {
        Ticket ticket = getRequired(publicId);
        TicketStatus target = request.targetStatus();
        stateMachine.validateTransition(ticket.getStatus(), target);
        if (target == TicketStatus.RESOLVED && !StringUtils.hasText(request.resolutionNotes())) {
            throw new IllegalArgumentException("resolutionNotes is required when resolving a ticket");
        }
        if (StringUtils.hasText(request.resolutionNotes())) {
            ticket.setResolutionNotes(request.resolutionNotes());
        }
        ticket.setStatus(target);
        publishChanged(ticket.getPublicId());
        ticketRepository.save(ticket);
        return reload(publicId);
    }

    @Transactional(readOnly = true)
    public Ticket getRequired(String publicId) {
        return ticketRepository
                .findWithCommentsByPublicId(publicId)
                .orElseThrow(() -> new TicketNotFoundException(publicId));
    }

    @Transactional
    public Page<Ticket> list(TicketStatus status, String q, Pageable pageable) {
        return ticketRepository.search(status, q, pageable);
    }

    private static String formatPublicId(Long id) {
        return "TKT-" + (1000 + id);
    }

    private void publishChanged(String publicId) {
        events.publishEvent(new TicketChangedEvent(publicId));
    }

    private Ticket reload(String publicId) {
        return ticketRepository
                .findWithCommentsByPublicId(publicId)
                .orElseThrow(() -> new TicketNotFoundException(publicId));
    }
}
