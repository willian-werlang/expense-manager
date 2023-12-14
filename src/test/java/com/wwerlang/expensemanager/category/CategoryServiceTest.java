package com.wwerlang.expensemanager.category;

import com.wwerlang.expensemanager.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    private static final List<Category> CATEGORIES = List.of(
            new Category(1, "Meal", "Lunch, dinner and breakfast.", true),
            new Category(2, "Hotel", "Home accommodation.", false)
    );

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @BeforeEach
    void mockDepartmentRepository() {
        lenient().when(categoryRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return CATEGORIES.stream().filter(e -> e.getId() == id).findFirst();
        });

        lenient().when(categoryRepository.findAll()).thenReturn(CATEGORIES);

        lenient().when(categoryRepository.save(any(Category.class))).thenAnswer(invocationOnMock -> {
            Category category = invocationOnMock.getArgument(0);
            category.setId(category.getId() == 0 ? 3 : category.getId());
            return category;
        });

        lenient().when(categoryRepository.existsById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return CATEGORIES.stream().anyMatch(e -> e.getId() == id);
        });
    }

    @Test
    void testFind() {
        CategoryDTO category = categoryService.find(1L);
        assertEquals(1, category.getId());
        assertEquals("Meal", category.getName());
        assertEquals("Lunch, dinner and breakfast.", category.getDescription());
        assertTrue(category.isActive());
    }

    @Test
    void testFindNonExistent() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> categoryService.find(3L));
        assertEquals("Category not found with ID 3.", e.getMessage());
    }

    @Test
    void testList() {
        List<CategoryDTO> categories = categoryService.list();
        assertEquals(2, categories.size());

        assertEquals(1, categories.get(0).getId());
        assertEquals("Meal", categories.get(0).getName());
        assertEquals("Lunch, dinner and breakfast.", categories.get(0).getDescription());
        assertTrue(categories.get(0).isActive());

        assertEquals(2, categories.get(1).getId());
        assertEquals("Hotel", categories.get(1).getName());
        assertEquals("Home accommodation.", categories.get(1).getDescription());
        assertFalse(categories.get(1).isActive());
    }

    @Test
    void testCreate() {
        CategoryDTO category = new CategoryDTO(0, "Flight", "Air flights.", true);
        category = categoryService.save(category);

        assertEquals(3, category.getId());
        assertEquals("Flight", category.getName());
        assertEquals("Air flights.", category.getDescription());
        assertTrue(category.isActive());
    }

    @Test
    void testUpdate() {
        CategoryDTO category = new CategoryDTO(2, "Hotel", "Home accommodations.", true);
        category = categoryService.save(category);

        assertEquals(2, category.getId());
        assertEquals("Hotel", category.getName());
        assertEquals("Home accommodations.", category.getDescription());
        assertTrue(category.isActive());
    }

    @Test
    void testUpdateNonExistent() {
        CategoryDTO category = new CategoryDTO(3, "Flight", "Air flights.", true);
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> categoryService.save(category));
        assertEquals("Category not found with ID 3.", e.getMessage());
    }

    @Test
    void testParseResponse() {
        Category category = new Category(1, "Meal", "Lunch, dinner and breakfast.", true);
        CategoryDTO categoryDTO = categoryService.parseResponse(category);

        assertEquals(1, categoryDTO.getId());
        assertEquals("Meal", categoryDTO.getName());
        assertEquals("Lunch, dinner and breakfast.", categoryDTO.getDescription());
        assertTrue(categoryDTO.isActive());
    }

    @Test
    void testParseRequest() {
        CategoryDTO categoryDTO = new CategoryDTO(2, "Hotel", "Home accommodations.", false);
        Category category = categoryService.parseRequest(categoryDTO);

        assertEquals(2, category.getId());
        assertEquals("Hotel", category.getName());
        assertEquals("Home accommodations.", category.getDescription());
        assertFalse(category.isActive());
    }
}