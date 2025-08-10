package com.assignment.ticketing.model.dto.response;

import java.time.LocalDateTime;

public record CommentResponse(String authorId, String content, LocalDateTime createdAt) {}
