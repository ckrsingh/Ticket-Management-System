package com.support.ticket.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.support.ticket.domain.TicketStatus;
import org.junit.jupiter.api.Test;

class TicketStateMachineTest {

    private final TicketStateMachine machine = new TicketStateMachine();

    @Test
    void allowsHappyPath() {
        assertTrue(machine.canTransition(TicketStatus.OPEN, TicketStatus.IN_PROGRESS));
        assertTrue(machine.canTransition(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED));
        assertTrue(machine.canTransition(TicketStatus.RESOLVED, TicketStatus.CLOSED));
    }

    @Test
    void allowsCancelFromOpenAndInProgress() {
        assertTrue(machine.canTransition(TicketStatus.OPEN, TicketStatus.CANCELLED));
        assertTrue(machine.canTransition(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED));
    }

    @Test
    void rejectsClosedToOpen() {
        assertFalse(machine.canTransition(TicketStatus.CLOSED, TicketStatus.OPEN));
        assertThrows(InvalidStatusTransitionException.class, () -> machine.validateTransition(TicketStatus.CLOSED, TicketStatus.OPEN));
    }

    @Test
    void rejectsResolvedToOpen() {
        assertFalse(machine.canTransition(TicketStatus.RESOLVED, TicketStatus.OPEN));
    }

    @Test
    void rejectsOpenToResolved() {
        assertFalse(machine.canTransition(TicketStatus.OPEN, TicketStatus.RESOLVED));
    }

    @Test
    void rejectsCancelledToOpen() {
        assertFalse(machine.canTransition(TicketStatus.CANCELLED, TicketStatus.OPEN));
    }
}
