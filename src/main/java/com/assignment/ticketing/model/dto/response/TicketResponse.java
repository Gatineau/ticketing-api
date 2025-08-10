package com.assignment.ticketing.model.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record TicketResponse (String subject, String description, String status, List<CommentResponse> comments, LocalDateTime createdAt, LocalDateTime updatedAt) {}
