package com.druv.team_task_board.dto;

public class Aut_responseDTO {
    
    private String token;

    public Aut_responseDTO(String token) { 
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    
}
