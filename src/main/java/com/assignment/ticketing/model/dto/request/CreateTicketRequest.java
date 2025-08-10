package com.assignment.ticketing.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateTicketRequest(@NotBlank String userId, @NotBlank String subject, @NotBlank String description) {}
