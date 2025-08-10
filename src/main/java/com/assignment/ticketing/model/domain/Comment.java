package com.assignment.ticketing.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

import com.assignment.ticketing.model.enums.CommentVisibility;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long commentId;
    private Long ticketId;
    private String authorId;
    private String content;
    private CommentVisibility visibility;
    private LocalDateTime createdAt;
}
