package com.support.ticket.service;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String publicId) {
        super("Ticket not found: " + publicId);
    }
}
