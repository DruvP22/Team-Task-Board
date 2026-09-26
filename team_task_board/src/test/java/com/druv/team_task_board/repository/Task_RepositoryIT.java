package com.druv.team_task_board.repository;

import com.druv.team_task_board.model.*;
import com.github.dockerjava.api.model.TaskStatus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class Task_RepositoryIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
     @Autowired
    private Task_Repository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void savedTask_canBeRetrievedById() {
        Project project = new Project();
        project.setName("Test Project");
        project = projectRepository.save(project);

        Task task = new Task();
        task.setTitle("Integration test task");
        task.setStatus(Task_Status.BACKLOG);
        task.setPriority(Priority.MEDIUM);
        task.setProject(project);
        Task saved = taskRepository.save(task);

        var found = taskRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Integration test task");
    }
}
