package com.assignment.ticketing.repository;

import java.util.List;
import java.util.Optional;

import com.assignment.ticketing.model.domain.Comment;
import com.assignment.ticketing.model.domain.Ticket;

public interface TicketRepository {

    Ticket save(Ticket ticket);

    Optional<Ticket> findById(Long id);

    List<Ticket> findByFilter(String status, String userId, String assigneeId);

    Optional<Comment> addComment(Long ticketId, Comment comment);

}