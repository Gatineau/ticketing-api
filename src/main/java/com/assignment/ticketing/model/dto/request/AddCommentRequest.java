package com.assignment.ticketing.model.dto.request;

import com.assignment.ticketing.model.enums.CommentVisibility;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddCommentRequest(@NotBlank String authorId, @NotNull String content,
        @NotNull CommentVisibility visibility) {
}
