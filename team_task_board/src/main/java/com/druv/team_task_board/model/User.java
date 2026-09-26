package com.druv.team_task_board.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Users") 
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (unique = true,nullable = false)
    private String email;

    @Column (nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false)
    private Role role;

    //getters and setters 
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    
}
