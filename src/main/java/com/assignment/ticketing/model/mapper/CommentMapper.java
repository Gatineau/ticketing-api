package com.assignment.ticketing.model.mapper;

import org.mapstruct.Mapper;

import com.assignment.ticketing.model.domain.Comment;
import com.assignment.ticketing.model.dto.request.AddCommentRequest;
import com.assignment.ticketing.model.dto.response.CommentResponse;



@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(AddCommentRequest addCommentRequest);

    CommentResponse toCommentResponse(Comment comment);
    
}
