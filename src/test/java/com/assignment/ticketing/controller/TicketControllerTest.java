package com.assignment.ticketing.controller;


import com.assignment.ticketing.model.domain.Comment;
import com.assignment.ticketing.model.domain.Ticket;
import com.assignment.ticketing.model.enums.CommentVisibility;
import com.assignment.ticketing.model.enums.TicketStatus;
import com.assignment.ticketing.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(properties = {
    "api.key.user=test-key-user",
    "api.key.agent=test-key-agent"
})
@AutoConfigureMockMvc
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TicketRepository ticketRepository;

    @BeforeEach
    void setUp() {

        Ticket ticket = new Ticket();
        ticket.setTicketId(1L);
        ticket.setStatus(TicketStatus.OPEN);
        ticket.setSubject("Test Ticket");
        ticket.setComments(List.of(
            new Comment(1L, 1L, "user-01", "Visible comment", CommentVisibility.PUBLIC, LocalDateTime.now()),
            new Comment(2L, 1L, "agent-03", "Internal comment", CommentVisibility.INTERNAL, LocalDateTime.now())
        ));
        ticketRepository.save(ticket);
        Ticket ticket2 = new Ticket();
        ticket2.setTicketId(2L);
        ticket2.setStatus(TicketStatus.RESOLVED);
        ticket2.setSubject("Test Ticket 2");
        ticketRepository.save(ticket2);
    }

    @Test
    void getTicket_shouldReturnOnlyPublicCommentsForUser() throws Exception {
        mockMvc.perform(get("/api/tickets?status=OPEN")
                .header("X-API-KEY", "test-key-user")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].comments.length()").value(1))
                .andExpect(jsonPath("$[0].comments[0].content").value("Visible comment"));
    }
}
