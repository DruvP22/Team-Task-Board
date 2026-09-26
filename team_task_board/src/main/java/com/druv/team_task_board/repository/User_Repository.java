package com.druv.team_task_board.repository;

import com.druv.team_task_board.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;


public interface User_Repository extends JpaRepository<User,Long> { 
    Optional<User> findByEmail(String email);
}