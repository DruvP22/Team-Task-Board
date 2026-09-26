package com.druv.team_task_board.controller;

import com.druv.team_task_board.dto.Project_requestDTO;
import com.druv.team_task_board.dto.Project_responseDTO;
import com.druv.team_task_board.service.Project_Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController 
@RequestMapping ("/api/projects")
public class Project_Controller {
    
    @Autowired 
    private Project_Service projectService;
@PostMapping
    public ResponseEntity<Project_responseDTO> create(@Valid @RequestBody Project_requestDTO dto) {
        return ResponseEntity.ok(projectService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<Project_responseDTO>> findAll() {
        return ResponseEntity.ok(projectService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project_responseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Project_responseDTO> update(@PathVariable Long id, @Valid @RequestBody Project_requestDTO dto) {
        return ResponseEntity.ok(projectService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
