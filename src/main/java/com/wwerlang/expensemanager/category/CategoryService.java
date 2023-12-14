package com.wwerlang.expensemanager.category;

import com.wwerlang.expensemanager.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public CategoryDTO find(long id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            return parseResponse(category);
        } else {
            throw new EntityNotFoundException("Category", id);
        }
    }

    public List<CategoryDTO> list() {
        List<CategoryDTO> categories = new ArrayList<>();
        categoryRepository.findAll().forEach(e -> categories.add(parseResponse(e)));
        return categories;
    }

    public CategoryDTO save(CategoryDTO categoryDTO) {
        long id = categoryDTO.getId();
        boolean isUpdate = id != 0;

        if (isUpdate && !categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category", id);
        }

        Category category = parseRequest(categoryDTO);
        category = categoryRepository.save(category);
        return parseResponse(category);
    }

    public CategoryDTO parseResponse(Category category) {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setId(category.getId());
        categoryDTO.setName(category.getName());
        categoryDTO.setDescription(category.getDescription());
        categoryDTO.setActive(category.isActive());
        return categoryDTO;
    }

    public Category parseRequest(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setActive(categoryDTO.isActive());
        return category;
    }
}
