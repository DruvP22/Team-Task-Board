package com.druv.team_task_board.exception;

public class Invalid_Status_TransitionException extends RuntimeException{
    
    public Invalid_Status_TransitionException(String message) {
        super(message);
    }
}
