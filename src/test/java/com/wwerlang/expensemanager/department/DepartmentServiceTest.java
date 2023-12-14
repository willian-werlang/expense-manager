package com.wwerlang.expensemanager.department;

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
class DepartmentServiceTest {

    private static final List<Department> DEPARTMENTS = List.of(
            new Department(1, "IT", "Fix computers.", true),
            new Department(2, "HR", "Hires people.", false)
    );

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
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
    void testFind() {
        DepartmentDTO department = departmentService.find(1L);
        assertEquals(1, department.getId());
        assertEquals("IT", department.getName());
        assertEquals("Fix computers.", department.getDescription());
        assertTrue(department.isActive());
    }

    @Test
    void testFindNonExistent() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> departmentService.find(3L));
        assertEquals("Department not found with ID 3.", e.getMessage());
    }

    @Test
    void testList() {
        List<DepartmentDTO> departments = departmentService.list();
        assertEquals(2, departments.size());

        assertEquals(1, departments.get(0).getId());
        assertEquals("IT", departments.get(0).getName());
        assertEquals("Fix computers.", departments.get(0).getDescription());
        assertTrue(departments.get(0).isActive());

        assertEquals(2, departments.get(1).getId());
        assertEquals("HR", departments.get(1).getName());
        assertEquals("Hires people.", departments.get(1).getDescription());
        assertFalse(departments.get(1).isActive());
    }

    @Test
    void testCreate() {
        DepartmentDTO department = new DepartmentDTO(0, "Sales", "Sell, sell, sell!", true);
        department = departmentService.save(department);

        assertEquals(3, department.getId());
        assertEquals("Sales", department.getName());
        assertEquals("Sell, sell, sell!", department.getDescription());
        assertTrue(department.isActive());
    }

    @Test
    void testUpdate() {
        DepartmentDTO department = new DepartmentDTO(2, "HR", "Hires people.", true);
        department = departmentService.save(department);

        assertEquals(2, department.getId());
        assertEquals("HR", department.getName());
        assertEquals("Hires people.", department.getDescription());
        assertTrue(department.isActive());
    }

    @Test
    void testUpdateNonExistent() {
        DepartmentDTO department = new DepartmentDTO(3, "Sales", "Sell, sell, sell!", true);
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> departmentService.save(department));
        assertEquals("Department not found with ID 3.", e.getMessage());
    }

    @Test
    void testParseResponse() {
        Department department = new Department(1, "IT", "Fix computers.", true);
        DepartmentDTO departmentDTO = departmentService.parseResponse(department);

        assertEquals(1, departmentDTO.getId());
        assertEquals("IT", departmentDTO.getName());
        assertEquals("Fix computers.", departmentDTO.getDescription());
        assertTrue(departmentDTO.isActive());
    }

    @Test
    void testParseRequest() {
        DepartmentDTO departmentDTO = new DepartmentDTO(2, "HR", "Hires people.", false);
        Department department = departmentService.parseRequest(departmentDTO);

        assertEquals(2, department.getId());
        assertEquals("HR", department.getName());
        assertEquals("Hires people.", department.getDescription());
        assertFalse(department.isActive());
    }
}
