package com.assignment.ticketing.service;

import com.assignment.ticketing.model.domain.Ticket;
import com.assignment.ticketing.model.enums.TicketStatus;
import com.assignment.ticketing.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TicketServiceTest {

    private TicketRepository ticketRepository;
    private TicketService ticketService;

    @BeforeEach
    void setUp() {
        ticketRepository = mock(TicketRepository.class);
        ticketService = new TicketService(ticketRepository);
    }

    @Test
    void createTicket_shouldSaveAndReturnTicket() {
        Ticket ticket = new Ticket();
        when(ticketRepository.save(ticket)).thenReturn(ticket);

        Ticket result = ticketService.createTicket(ticket);

        assertEquals(ticket, result);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void updateTicketStatus_shouldUpdateIfValidTransition() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setStatus(TicketStatus.OPEN);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Ticket> result = ticketService.updateTicketStatus(1L, TicketStatus.IN_PROGRESS);

        assertTrue(result.isPresent());
        assertEquals(TicketStatus.IN_PROGRESS, result.get().getStatus());
    }

    @Test
    void updateTicketStatus_shouldThrowIfInvalidTransition() {
        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setStatus(TicketStatus.CLOSED);
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.updateTicketStatus(1L, TicketStatus.OPEN);
        });
    }
}