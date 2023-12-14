package com.wwerlang.expensemanager.employee;

import com.wwerlang.expensemanager.department.Department;
import com.wwerlang.expensemanager.department.DepartmentRepository;
import com.wwerlang.expensemanager.department.DepartmentService;
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

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    private static final String PATH = "/employee";

    private static final List<Employee> EMPLOYEES = List.of(
            new Employee(1, "John", "Johnson", true, new Department(1, "IT", "Fix computers.", true)),
            new Employee(2, "Smith", "Smithson", false, new Department(2, "HR", "Hires people.", false))
    );

    @Autowired
    MockMvc mockMvc;

    @SpyBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepository employeeRepository;

    @SpyBean
    private DepartmentService departmentService;

    @MockBean
    private DepartmentRepository departmentRepository;


    @BeforeEach
    void mockEmployeeRepository() {
        lenient().when(employeeRepository.findById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return EMPLOYEES.stream().filter(e -> e.getId() == id).findFirst();
        });

        lenient().when(employeeRepository.findAll()).thenReturn(EMPLOYEES);

        lenient().when(employeeRepository.save(any(Employee.class))).thenAnswer(invocationOnMock -> {
            Employee employee = invocationOnMock.getArgument(0);
            employee.setId(employee.getId() == 0 ? 3 : employee.getId());
            return employee;
        });

        lenient().when(employeeRepository.existsById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return EMPLOYEES.stream().anyMatch(e -> e.getId() == id);
        });
    }

    @Test
    void testFind() throws Exception {
        mockMvc.perform(get(PATH + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Johnson"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.department.id").value(1))
                .andExpect(jsonPath("$.department.name").value("IT"))
                .andExpect(jsonPath("$.department.description").value("Fix computers."));
    }

    @Test
    void testFindNonExistent() throws Exception {
        String response = mockMvc.perform(get(PATH + "/{id}", 3))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Employee not found with ID 3.", response);
    }

    @Test
    void testList() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Johnson"))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[0].department.id").value(1))
                .andExpect(jsonPath("$[0].department.name").value("IT"))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].firstName").value("Smith"))
                .andExpect(jsonPath("$[1].lastName").value("Smithson"))
                .andExpect(jsonPath("$[1].active").value(false))
                .andExpect(jsonPath("$[1].department.id").value(2))
                .andExpect(jsonPath("$[1].department.name").value("HR"));
    }

    @Test
    void testCreate() throws Exception {
        String input = "{ \"firstName\": \"Gunnar\", \"lastName\": \"Gunnarson\", \"active\": true, \"department\": { \"id\": 1, \"name\": \"IT\" } }";

        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("3"))
                .andExpect(jsonPath("$.firstName").value("Gunnar"))
                .andExpect(jsonPath("$.lastName").value("Gunnarson"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.department.id").value(1))
                .andExpect(jsonPath("$.department.name").value("IT"));
    }

    @Test
    void testUpdate() throws Exception {
        String input = "{ \"id\": \"2\", \"firstName\": \"Smith\", \"lastName\": \"Smithson\", \"active\": true, \"department\": { \"id\": 2, \"name\": \"HR\" } }";

        mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("2"))
                .andExpect(jsonPath("$.firstName").value("Smith"))
                .andExpect(jsonPath("$.lastName").value("Smithson"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.department.id").value(2))
                .andExpect(jsonPath("$.department.name").value("HR"));
    }

    @Test
    void testUpdateNonExistent() throws Exception {
        String input = "{ \"id\": \"3\", \"firstName\": \"Gunnar\", \"lastName\": \"Gunnarson\", \"active\": true, \"department\": { \"id\": 1, \"name\": \"IT\" } }";

        String response = mockMvc.perform(post(PATH).contentType(MediaType.APPLICATION_JSON).content(input))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        assertEquals("Employee not found with ID 3.", response);
    }
}
