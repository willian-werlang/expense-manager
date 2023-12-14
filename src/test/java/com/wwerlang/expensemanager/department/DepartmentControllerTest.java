package com.wwerlang.expensemanager.department;

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

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    private static final String PATH = "/department";

    private static final List<Department> DEPARTMENTS = List.of(
            new Department(1, "IT", "Fix computers.", true),
            new Department(2, "HR", "Hires people.", false)
    );

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    private DepartmentService departmentService;

    @MockBean
    private DepartmentRepository departmentRepository;

    @BeforeEach
    void mockDepartmentRepository() {
        lenient().when(departmentRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return DEPARTMENTS.stream().filter(e -> e.getId() == id).findFirst();
        });

        lenient().when(departmentRepository.findAll()).thenReturn(DEPARTMENTS);

        lenient().when(departmentRepository.save(any(Department.class))).thenAnswer(invocationOnMock -> {
            Department department = invocationOnMock.getArgument(0);
            department.setId(department.getId() == 0 ? 3 : department.getId());
            return department;
        });

        lenient().when(departmentRepository.existsById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return DEPARTMENTS.stream().anyMatch(e -> e.getId() == id);
        });
    }

    @Test
    void testFind() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("IT"))
                .andExpect(jsonPath("$.description").value("Fix computers."))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testFindNonExistent() throws Exception {
        String response = mockMvc.perform(get(PATH + "/{id}", 3))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Department not found with ID 3.", response);
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].name").value("IT"))
                .andExpect(jsonPath("$[0].description").value("Fix computers."))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].name").value("HR"))
                .andExpect(jsonPath("$[1].description").value("Hires people."))
                .andExpect(jsonPath("$[1].active").value(false));
    }

    @Test
    void testCreate() throws Exception {
        String input = "{ \"name\": \"Sales\", \"description\": \"Sell, sell, sell!\", \"active\": true }";

        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.name").value("Sales"))
                .andExpect(jsonPath("$.description").value("Sell, sell, sell!"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdate() throws Exception {
        String input = "{ \"id\": \"2\", \"name\": \"HR\", \"description\": \"Hires people.\", \"active\": true }";

        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.name").value("HR"))
                .andExpect(jsonPath("$.description").value("Hires people."))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void testUpdateNonExistent() throws Exception {
        String input = "{ \"id\": \"3\", \"name\": \"Sales\", \"description\": \"Sell, sell, sell!\", \"active\": true }";

        String response = mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Department not found with ID 3.", response);
    }
}
