package com.druv.team_task_board.repository;

import com.druv.team_task_board.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}