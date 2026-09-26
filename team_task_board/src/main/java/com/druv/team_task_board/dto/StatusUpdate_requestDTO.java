package com.druv.team_task_board.dto;

import com.druv.team_task_board.model.Task_Status;
import jakarta.validation.constraints.NotNull;

public class StatusUpdate_requestDTO {
 
    @NotNull
    private Task_Status status;

    public Task_Status getStatus() {
        return status;
    }

    public void setStatus(Task_Status status) {
        this.status = status;
    }

    
}
