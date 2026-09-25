package com.support.ticket.config;

import com.support.ticket.domain.Ticket;
import com.support.ticket.domain.TicketComment;
import com.support.ticket.domain.TicketPriority;
import com.support.ticket.domain.TicketStatus;
import com.support.ticket.repository.TicketRepository;
import org.springframework.stereotype.Component;

@Component
public class DemoDataSeeder {

    private final TicketRepository ticketRepository;

    public DemoDataSeeder(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public void seedIfEmpty() {
        if (ticketRepository.count() > 0) {
            return;
        }
        Ticket payment = createTicket(
                "Payment failed at checkout",
                "Customer card declined during payment capture.",
                TicketPriority.HIGH,
                "billing",
                "alice@example.com");
        payment.addComment(comment("support", "Gateway returned code 51 — insufficient funds."));
        payment.addComment(comment("alice@example.com", "Advised customer to retry with another card."));
        payment.setStatus(TicketStatus.RESOLVED);
        payment.setResolutionNotes("Customer retried with a different card; payment succeeded.");
        ticketRepository.save(payment);

        Ticket shipment = createTicket(
                "Shipment tracking not updating",
                "Tracking ID stuck on 'label created' for 48h.",
                TicketPriority.MEDIUM,
                "logistics",
                "bob@example.com");
        shipment.addComment(comment("bob@example.com", "Carrier API delay; common during peak season."));
        shipment.setStatus(TicketStatus.IN_PROGRESS);
        ticketRepository.save(shipment);

        Ticket payment2 = createTicket(
                "Recurring payment failure",
                "Subscription renewal failed twice for enterprise account.",
                TicketPriority.CRITICAL,
                "billing",
                "alice@example.com");
        payment2.setStatus(TicketStatus.OPEN);
        ticketRepository.save(payment2);
    }

    private Ticket createTicket(String title, String description, TicketPriority priority, String category, String assignee) {
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setPriority(priority);
        ticket.setCategory(category);
        ticket.setAssignee(assignee);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setPublicId("PENDING");
        Ticket saved = ticketRepository.save(ticket);
        saved.setPublicId("TKT-" + (1000 + saved.getId()));
        return ticketRepository.save(saved);
    }

    private static TicketComment comment(String author, String body) {
        TicketComment c = new TicketComment();
        c.setAuthor(author);
        c.setBody(body);
        return c;
    }
}
