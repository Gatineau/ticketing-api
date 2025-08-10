package com.assignment.ticketing.model.dto.request;

import com.assignment.ticketing.model.enums.TicketStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(@NotNull TicketStatus status) {
}
