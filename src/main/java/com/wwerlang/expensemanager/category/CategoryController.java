package com.wwerlang.expensemanager.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/{id}")
    private ResponseEntity<CategoryDTO> find(@PathVariable("id") long id) {
        CategoryDTO category = categoryService.find(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping
    private ResponseEntity<List<CategoryDTO>> list() {
        List<CategoryDTO> categories = categoryService.list();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    private ResponseEntity<CategoryDTO> save(@RequestBody CategoryDTO category) {
        category = categoryService.save(category);
        return ResponseEntity.ok(category);
    }
}
