package com.assignment.ticketing.model.enums;

public enum CommentVisibility {
    PUBLIC,
    INTERNAL;

    /* 
     * Checks if the comment is visible to users.
     */
    public boolean isVisibleToUser() {
        return this == PUBLIC;
    }
    
}
