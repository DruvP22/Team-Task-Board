package com.druv.team_task_board.service;

import com.druv.team_task_board.dto.Project_requestDTO;
import com.druv.team_task_board.dto.Project_responseDTO;
import com.druv.team_task_board.model.Project;
import com.druv.team_task_board.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service 
public class Project_Service {
    @Autowired
    private ProjectRepository projectRepository;

    public Project_responseDTO create(Project_requestDTO  dto) {
        Project project = new Project();
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        Project saved = projectRepository.save(project);
        return toResponseDTO(saved);
    }

    public List<Project_responseDTO> findAll() {
        return projectRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public Project_responseDTO findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
        return toResponseDTO(project);
    }

public Project_responseDTO update(Long id, Project_requestDTO dto) {        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
        project.setName(dto.getName());
        project.setDescription(dto.getDescription());
        return toResponseDTO(projectRepository.save(project));
    }

    public void delete(Long id) {
        projectRepository.deleteById(id);
    }

    private Project_responseDTO toResponseDTO(Project project) {
        Project_responseDTO dto = new Project_responseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        return dto;
    }
}