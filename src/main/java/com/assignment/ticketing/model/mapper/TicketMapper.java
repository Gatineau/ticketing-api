package com.assignment.ticketing.model.mapper;

import org.mapstruct.Mapper;

import com.assignment.ticketing.model.domain.Ticket;
import com.assignment.ticketing.model.dto.response.TicketResponse;

import com.assignment.ticketing.model.dto.request.CreateTicketRequest;

@Mapper(componentModel = "spring")
public interface TicketMapper {

    Ticket toTicket(CreateTicketRequest ticketRequest);

    TicketResponse toTicketResponse(Ticket ticket);
    
}
