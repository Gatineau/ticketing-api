package com.assignment.ticketing.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.ticketing.model.domain.Comment;
import com.assignment.ticketing.model.domain.Ticket;
import com.assignment.ticketing.model.dto.request.AddCommentRequest;
import com.assignment.ticketing.model.dto.request.CreateTicketRequest;
import com.assignment.ticketing.model.dto.request.UpdateTicketStatusRequest;
import com.assignment.ticketing.model.dto.response.TicketResponse;
import com.assignment.ticketing.model.enums.TicketStatus;
import com.assignment.ticketing.model.mapper.CommentMapper;
import com.assignment.ticketing.model.mapper.TicketMapper;
import com.assignment.ticketing.service.TicketService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final CommentMapper commentMapper;

    public TicketController(TicketService ticketService, TicketMapper ticketMapper, CommentMapper commentMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
        this.commentMapper = commentMapper;
    }

    /*
     * Creates a new ticket.
     */
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest createTicketRequest) {
        Ticket ticket = ticketMapper.toTicket(createTicketRequest);
        Ticket created = ticketService.createTicket(ticket);
        return ResponseEntity.status(201).body(ticketMapper.toTicketResponse(created));

    }

    /*
     * Retrieves a list of tickets based on optional filters.
     */
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String assigneeId) {

        List<TicketResponse> tickets = ticketService.getTickets(status, userId, assigneeId)
                .stream()
                .map(ticketMapper::toTicketResponse)
                .toList();

        return ResponseEntity.ok(tickets);

    }

    /*
     * Retrieves a ticket by its ID.
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateTicketStatus(@PathVariable("id") Long ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest updateTicketStatusRequest) {
        TicketStatus newStatus = updateTicketStatusRequest.status();

        return ticketService.updateTicketStatus(ticketId, newStatus)
                .map(ticket -> ResponseEntity.ok(ticketMapper.toTicketResponse(ticket)))
                .orElse(ResponseEntity.notFound().build());

    }

    /*
     * Adds a comment to a ticket.
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable("id") Long ticketId,
            @Valid @RequestBody AddCommentRequest addCommentRequest) {

        Comment comment = commentMapper.toComment(addCommentRequest);

        return ticketService.addComment(ticketId, comment)
                .map(created -> ResponseEntity.status(201).body(commentMapper.toCommentResponse(created)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
