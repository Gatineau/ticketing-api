package com.assignment.ticketing.storage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

import com.assignment.ticketing.model.domain.Comment;
import com.assignment.ticketing.model.domain.Ticket;
import com.assignment.ticketing.repository.TicketRepository;

@Component
public class InMemoryStorage implements TicketRepository {

    private final Map<Long, Ticket> tickets = new ConcurrentHashMap<>();
    private final AtomicLong ticketIdGenerator = new AtomicLong();
    private final AtomicLong commentIdGenerator = new AtomicLong();

    /**
     * Saves a ticket to the in-memory storage.
     * If the ticket does not have an ID, it generates a new ID and sets the created timestamp.
     * Updates the updated timestamp every time the ticket is saved.
     *
     * @param ticket the ticket to save
     * @return the saved ticket
     */
    public Ticket save(Ticket ticket) {
        LocalDateTime now = LocalDateTime.now();
        if (ticket.getTicketId() == null) {
            Long id = ticketIdGenerator.incrementAndGet();
            ticket.setTicketId(id);
            ticket.setCreatedAt(now);
        }
        ticket.setUpdatedAt(now);
        tickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    /**
     * Finds a ticket by its ID.
     *
     * @param id the ID of the ticket
     * @return an Optional containing the ticket if found, or empty if not found
     */
    public Optional<Ticket> findById(Long id) {
        return Optional.ofNullable(tickets.get(id));
    }

    /**
     * Finds tickets based on the provided filters.
     * Filters by status, userId, and assigneeId.
     *
     * @param status      the status of the tickets to filter by (optional)
     * @param userId      the ID of the user who created the tickets (optional)
     * @param assigneeId  the ID of the agent assigned to the tickets (optional)
     * @return a list of tickets matching the filters
     */
    public List<Ticket> findByFilter(String status, String userId, String assigneeId) {
        return tickets.values().stream()
                .filter(ticket -> (status == null || ticket.getStatus().name().equalsIgnoreCase(status)) &&
                        (userId == null || ticket.getUserId().equals(userId)) &&
                        (assigneeId == null || ticket.getAssigneeId().equals(assigneeId)))
                .toList();
    }

    /**
     * Adds a comment to a ticket.
     * Generates a new comment ID, sets the created timestamp, and updates the ticket's updated timestamp.
     *
     * @param ticketId the ID of the ticket to add the comment to
     * @param comment  the comment to add
     * @return an Optional containing the added comment if successful, or empty if the ticket does not exist
     */
    public Optional<Comment> addComment(Long ticketId, Comment comment) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null)
            return Optional.empty();
        Long commentId = commentIdGenerator.incrementAndGet();
        comment.setCommentId(commentId);
        comment.setCreatedAt(LocalDateTime.now());
        ticket.getComments().add(comment);
        ticket.setUpdatedAt(comment.getCreatedAt() != null ? comment.getCreatedAt() : LocalDateTime.now());
        return Optional.of(comment);
    }
}
