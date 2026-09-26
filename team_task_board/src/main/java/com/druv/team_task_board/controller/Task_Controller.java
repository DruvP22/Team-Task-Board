package com.druv.team_task_board.controller;
import com.druv.team_task_board.dto.Task_requestDTO;
import com.druv.team_task_board.dto.Task_responseDTO;
import com.druv.team_task_board.service.Task_Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.druv.team_task_board.dto.StatusUpdate_requestDTO;
@RestController
@RequestMapping("/api/tasks")
public class Task_Controller {
     @Autowired
    private Task_Service taskService;

    @PostMapping
    public ResponseEntity<Task_responseDTO> create(@Valid @RequestBody Task_requestDTO dto) {
        return ResponseEntity.ok(taskService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<Task_responseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(taskService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task_responseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task_responseDTO> update(@PathVariable Long id, @Valid @RequestBody Task_requestDTO dto) {
        return ResponseEntity.ok(taskService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
public ResponseEntity<Task_responseDTO> changeStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdate_requestDTO dto) {
    return ResponseEntity.ok(taskService.changeStatus(id, dto.getStatus()));
}
}
