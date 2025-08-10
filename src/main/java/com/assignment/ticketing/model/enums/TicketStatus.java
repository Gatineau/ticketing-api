package com.assignment.ticketing.model.enums;

public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED;
    
        /**
         * Checks if the current status can transition to the next status.
         *
         * @param next The next status to transition to.
         * @return true if the transition is valid, false otherwise.
         */
        public boolean canTransitionTo(TicketStatus next) {
            return switch (this) {
                case OPEN        -> next == IN_PROGRESS;
                case IN_PROGRESS -> next == RESOLVED;
                case RESOLVED    -> next == CLOSED;
                case CLOSED      -> false;
            };
        }
}
