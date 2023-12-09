package com.wwerlang.expensemanager.department;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping("/{id}")
    private ResponseEntity<DepartmentDTO> find(@PathVariable("id") long id) {
        DepartmentDTO department = departmentService.find(id);
        return ResponseEntity.ok(department);
    }

    @GetMapping
    private ResponseEntity<List<DepartmentDTO>> list() {
        List<DepartmentDTO> departments = departmentService.list();
        return ResponseEntity.ok(departments);
    }

    @PostMapping
    private ResponseEntity<DepartmentDTO> save(@RequestBody DepartmentDTO department) {
        department = departmentService.save(department);
        return ResponseEntity.ok(department);
    }
}
