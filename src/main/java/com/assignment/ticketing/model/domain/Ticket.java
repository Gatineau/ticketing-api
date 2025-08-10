package com.assignment.ticketing.model.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.assignment.ticketing.model.enums.TicketStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    private Long ticketId;
    private String subject;
    private String description;
    private TicketStatus status;
    private String userId;
    private String assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Comment> comments = new ArrayList<>();
}
