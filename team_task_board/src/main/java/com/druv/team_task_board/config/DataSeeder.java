package com.druv.team_task_board.config;
import com.druv.team_task_board.model.*;
import com.druv.team_task_board.repository.ProjectRepository;
import com.druv.team_task_board.repository.User_Repository;
import com.druv.team_task_board.repository.Task_Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component 
public class DataSeeder implements CommandLineRunner {
    @Autowired 
    private User_Repository userRepository;

    @Autowired 
    private ProjectRepository projectRepository;

    @Autowired
    private Task_Repository taskRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (taskRepository.count() > 0) {
            System.out.println("Seed data already exists, skipping.");
            return;
        }

        System.out.println("Seeding database with test data...");

        // Create 20 users
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            User user = new User();
            user.setEmail("seeduser" + i + "@test.com");
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setRole(Role.MEMBER);
            users.add(userRepository.save(user));
        }

        // Create 5 projects
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Project project = new Project();
            project.setName("Seed Project " + i);
            project.setDescription("Auto-generated project for performance testing");
            projects.add(projectRepository.save(project));
        }

        // Create 5,000 tasks, randomly assigned
        Random random = new Random();
        Task_Status[] statuses = Task_Status.values();
        Priority[] priorities = Priority.values();

        List<Task> tasks = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            Task task = new Task();
            task.setTitle("Seed Task " + i);
            task.setDescription("Auto-generated task #" + i);
            task.setStatus(statuses[random.nextInt(statuses.length)]);
            task.setPriority(priorities[random.nextInt(priorities.length)]);
            task.setAssignnee(users.get(random.nextInt(users.size())));
            task.setProject(projects.get(random.nextInt(projects.size())));
            tasks.add(task);
        }
        taskRepository.saveAll(tasks);

        System.out.println("Seeding complete: " + users.size() + " users, "
                + projects.size() + " projects, " + tasks.size() + " tasks.");
    }
}
