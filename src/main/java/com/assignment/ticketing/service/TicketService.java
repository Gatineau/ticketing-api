package com.assignment.ticketing.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.assignment.ticketing.model.enums.TicketStatus;
import com.assignment.ticketing.model.enums.CommentVisibility;
import com.assignment.ticketing.model.domain.Comment;
import com.assignment.ticketing.model.domain.Ticket;


import com.assignment.ticketing.repository.TicketRepository;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }


    /**
     * Creates a new ticket with the status set to OPEN.
     *
     * @param ticket the ticket to be created
     * @return the created ticket
     */
    public Ticket createTicket(Ticket ticket) {
        log.info("Creating ticket with subject: {}", ticket.getSubject());
        ticket.setStatus(TicketStatus.OPEN);
        return ticketRepository.save(ticket);
    }

    /**
     * Retrieves a list of tickets based on the provided filters.
     *
     * @param status      the status of the tickets to filter by (optional)
     * @param userId      the ID of the user who created the tickets (optional)
     * @param assigneeId  the ID of the agent assigned to the tickets (optional)
     * @return a list of tickets matching the filters
     */
    public List<Ticket> getTickets(String status, String userId, String assigneeId) {
        if(isCurrentUserAgent()) {
            log.info("Fetching tickets as an agent with status: {}, userId: {}, assigneeId: {}", status, userId, assigneeId);
            return ticketRepository.findByFilter(status, userId, assigneeId);
        } else {
            log.info("Fetching tickets as a user with status: {}, userId: {}, assigneeId: {}", status, userId, assigneeId);
            return ticketRepository.findByFilter(status, userId, assigneeId).stream()
                .map(ticket -> {
                    ticket.setComments(
                        ticket.getComments().stream()
                            .filter(c -> c.getVisibility().isVisibleToUser())
                            .sorted(Comparator.comparing(Comment::getCreatedAt))
                            .collect(Collectors.toCollection(ArrayList::new))
                    );
                    return ticket;
                })
                .toList();
        }
    }

    /**
     * Updates the status of a ticket.
     *
     * @param ticketId   the ID of the ticket to update
     * @param newStatus  the new status to set for the ticket
     * @return an Optional containing the updated ticket if found, or empty if not found
     */
    public Optional<Ticket> updateTicketStatus(Long ticketId, TicketStatus newStatus) {
        log.info("Updating ticket status for ticketId: {} to newStatus: {}", ticketId, newStatus);
        return ticketRepository.findById(ticketId).map(ticket -> {
            if (!ticket.getStatus().canTransitionTo(newStatus)) {
                throw new IllegalArgumentException("Invalid status transition: " + ticket.getStatus() + " → " + newStatus);
            }
            ticket.setStatus(newStatus);
            return ticketRepository.save(ticket);
        });
    }

    /**
     * Adds a comment to a ticket.
     *
     * @param ticketId  the ID of the ticket to add the comment to
     * @param comment   the comment to be added
     * @return an Optional containing the added comment if successful, or empty if the ticket does not exist
     */
    public Optional<Comment> addComment(Long ticketId, Comment comment) {
        
        if(!isCurrentUserAgent() && 
           !comment.getVisibility().equals(CommentVisibility.PUBLIC)) {
            throw new IllegalArgumentException("User comments must be public");
        }
        log.info("Adding comment to ticketId: {} with visibility: {}", ticketId, comment.getVisibility());
        return ticketRepository.addComment(ticketId, comment);
    }

    /**
     * Checks if the current user is an agent.
     *
     * @return true if the current user is an agent, false otherwise
     */
    private boolean isCurrentUserAgent() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
    }
}
