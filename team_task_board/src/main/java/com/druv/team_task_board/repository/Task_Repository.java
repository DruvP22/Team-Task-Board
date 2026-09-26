package com.druv.team_task_board.repository;

import com.druv.team_task_board.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Task_Repository extends JpaRepository<Task,Long>{
    List<Task> findByProjectId(Long projectId);

    @EntityGraph(attributePaths = {"assignee", "project"})
    Page<Task> findAll(Pageable pageable);  
}
