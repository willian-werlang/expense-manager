package com.wwerlang.expensemanager.employee;

import com.wwerlang.expensemanager.department.Department;
import com.wwerlang.expensemanager.department.DepartmentDTO;
import com.wwerlang.expensemanager.department.DepartmentService;
import com.wwerlang.expensemanager.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    private static final List<Employee> EMPLOYEES = List.of(
            new Employee(1, "John", "Johnson", true, new Department(1, "IT", "Fix computers.", true)),
            new Employee(2, "Smith", "Smithson", false, new Department(2, "HR", "Hires people.", false))
    );

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private DepartmentService departmentService;

    @BeforeEach
    void mockDepartmentRepository() {
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
    void testFind() {
        EmployeeDTO employee = employeeService.find(1L);
        assertEquals(1, employee.getId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Johnson", employee.getLastName());
        assertTrue(employee.isActive());
        assertEquals(1, employee.getDepartment().getId());
        assertEquals("IT", employee.getDepartment().getName());
    }

    @Test
    void testFindNonExistent() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> employeeService.find(3L));
        assertEquals("Employee not found with ID 3.", e.getMessage());
    }

    @Test
    void testList() {
        List<EmployeeDTO> employees = employeeService.list();
        assertEquals(2, employees.size());

        assertEquals(1, employees.get(0).getId());
        assertEquals("John", employees.get(0).getFirstName());
        assertEquals("Johnson", employees.get(0).getLastName());
        assertTrue(employees.get(0).isActive());
        assertEquals(1, employees.get(0).getDepartment().getId());
        assertEquals("IT", employees.get(0).getDepartment().getName());

        assertEquals(2, employees.get(1).getId());
        assertEquals("Smith", employees.get(1).getFirstName());
        assertEquals("Smithson", employees.get(1).getLastName());
        assertFalse(employees.get(1).isActive());
        assertEquals(2, employees.get(1).getDepartment().getId());
        assertEquals("HR", employees.get(1).getDepartment().getName());
    }

    @Test
    void testCreate() {
        DepartmentDTO department = new DepartmentDTO(1, "IT", "Fix computers.", true);
        EmployeeDTO employee = new EmployeeDTO(0, "John", "Johnson", true, department);
        employee = employeeService.save(employee);

        assertEquals(3, employee.getId());
        assertEquals("John", employee.getFirstName());
        assertEquals("Johnson", employee.getLastName());
        assertTrue(employee.isActive());
        assertEquals(1, employee.getDepartment().getId());
        assertEquals("IT", employee.getDepartment().getName());
    }

    @Test
    void testUpdate() {
        DepartmentDTO department = new DepartmentDTO(2, "HR", "Hires people.", true);
        EmployeeDTO employee = new EmployeeDTO(2, "Smith", "Smithson", true, department);
        employee = employeeService.save(employee);

        assertEquals(2, employee.getId());
        assertEquals("Smith", employee.getFirstName());
        assertEquals("Smithson", employee.getLastName());
        assertTrue(employee.isActive());
        assertEquals(2, employee.getDepartment().getId());
        assertEquals("HR", employee.getDepartment().getName());
    }

    @Test
    void testUpdateNonExistent() {
        DepartmentDTO department = new DepartmentDTO(2, "HR", "Hires people.", true);
        EmployeeDTO employee = new EmployeeDTO(3, "Gunnar", "Gunnarson", true, department);
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> employeeService.save(employee));
        assertEquals("Employee not found with ID 3.", e.getMessage());
    }

    @Test
    void testParseResponse() {
        Department department = new Department(1, "IT", "Fix computers.", true);
        Employee employee = new Employee(1, "John", "Johnson", true, department);
        EmployeeDTO employeeDTO = employeeService.parseResponse(employee);

        assertEquals(1, employeeDTO.getId());
        assertEquals("John", employeeDTO.getFirstName());
        assertEquals("Johnson", employeeDTO.getLastName());
        assertTrue(employeeDTO.isActive());
        assertEquals(1, employee.getDepartment().getId());
        assertEquals("IT", employee.getDepartment().getName());
    }

    @Test
    void testParseRequest() {
        DepartmentDTO departmentDTO = new DepartmentDTO(2, "HR", "Hires people.", true);
        EmployeeDTO employeeDTO = new EmployeeDTO(2, "Smith", "Smithson", false, departmentDTO);
        Employee employee = employeeService.parseRequest(employeeDTO);

        assertEquals(2, employeeDTO.getId());
        assertEquals("Smith", employeeDTO.getFirstName());
        assertEquals("Smithson", employeeDTO.getLastName());
        assertFalse(employeeDTO.isActive());
        assertEquals(2, employee.getDepartment().getId());
        assertEquals("HR", employee.getDepartment().getName());
    }
}
