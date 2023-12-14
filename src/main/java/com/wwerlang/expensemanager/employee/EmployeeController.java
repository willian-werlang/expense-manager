package com.wwerlang.expensemanager.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/{id}")
    private ResponseEntity<EmployeeDTO> find(@PathVariable("id") long id) {
        EmployeeDTO category = employeeService.find(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    private ResponseEntity<List<EmployeeDTO>> list() {
        List<EmployeeDTO> categories = employeeService.list();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    private ResponseEntity<EmployeeDTO> save(@RequestBody EmployeeDTO category) {
        category = employeeService.save(category);
        return ResponseEntity.ok(category);
    }
}
