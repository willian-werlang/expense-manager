package com.wwerlang.expensemanager.category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    private static final String PATH = "/category";

    private static final List<Category> CATEGORIES = List.of(
            new Category(1, "Meal", "Lunch, dinner and breakfast.", true),
            new Category(2, "Hotel", "Home accommodation.", false)
    );

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    @BeforeEach
    void mockCategoryRepository() {
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
    void testFind() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Meal"))
                .andExpect(jsonPath("$.description").value("Lunch, dinner and breakfast."))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testFindNonExistent() throws Exception {
        String response = mockMvc.perform(get(PATH + "/{id}", 3))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Category not found with ID 3.", response);
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("Meal"))
                .andExpect(jsonPath("$[0].description").value("Lunch, dinner and breakfast."))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("Hotel"))
                .andExpect(jsonPath("$[1].description").value("Home accommodation."))
                .andExpect(jsonPath("$[1].active").value(false));
    }

    @Test
    void testCreate() throws Exception {
        String input = "{ \"name\": \"Flight\", \"description\": \"Air flights.\", \"active\": true }";

        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.name").value("Flight"))
                .andExpect(jsonPath("$.description").value("Air flights."))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdate() throws Exception {
        String input = "{ \"id\": \"2\", \"name\": \"Hotel\", \"description\": \"Home accommodation.\", \"active\": true }";

        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.name").value("Hotel"))
                .andExpect(jsonPath("$.description").value("Home accommodation."))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdateNonExistent() throws Exception {
        String input = "{ \"id\": \"3\", \"name\": \"Flight\", \"description\": \"Air flights.\", \"active\": true }";

        String response = mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Category not found with ID 3.", response);
    }
}
