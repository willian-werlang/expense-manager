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
            new Department(2, "HR", "Hires people.", true),
            new Department(3, "Sales", "Sells stuff.", false)
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
            department.setId(department.getId() == 0 ? 1 : department.getId());
            return department;
        });

        lenient().when(departmentRepository.existsById(anyLong())).thenAnswer(invocationOnMock -> {
            long id = invocationOnMock.getArgument(0);
            return DEPARTMENTS.stream().anyMatch(e -> e.getId() == id);
        });
    }

    @Test
    void testFind() {
        DepartmentDTO departmentDTO = departmentService.find(1L);
        assertEquals(1, departmentDTO.getId());
        assertEquals("IT", departmentDTO.getName());
        assertEquals("Fix computers.", departmentDTO.getDescription());
        assertTrue(departmentDTO.isActive());
    }

    @Test
    void testFindNonExistent() {
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> departmentService.find(4L));
        assertEquals("Department not found with ID 4.", e.getMessage());
    }

    @Test
    void testList() {
        List<DepartmentDTO> departments = departmentService.list();

        assertEquals(1, departments.get(0).getId());
        assertEquals("IT", departments.get(0).getName());
        assertEquals("Fix computers.", departments.get(0).getDescription());
        assertTrue(departments.get(0).isActive());

        assertEquals(2, departments.get(1).getId());
        assertEquals("HR", departments.get(1).getName());
        assertEquals("Hires people.", departments.get(1).getDescription());
        assertTrue(departments.get(1).isActive());

        assertEquals(3, departments.get(2).getId());
        assertEquals("Sales", departments.get(2).getName());
        assertEquals("Sells stuff.", departments.get(2).getDescription());
        assertFalse(departments.get(2).isActive());
    }

    @Test
    void testCreate() {
        DepartmentDTO department = new DepartmentDTO(0, "IT", "Fix computers.", true);
        department = departmentService.save(department);

        assertEquals(1, department.getId());
        assertEquals("IT", department.getName());
        assertEquals("Fix computers.", department.getDescription());
        assertTrue(department.isActive());
    }

    @Test
    void testUpdate() {
        DepartmentDTO department = new DepartmentDTO(2, "Sales", "Sells stuff.", false);
        department = departmentService.save(department);

        assertEquals(2, department.getId());
        assertEquals("Sales", department.getName());
        assertEquals("Sells stuff.", department.getDescription());
        assertFalse(department.isActive());
    }

    @Test
    void testUpdateNonExistent() {
        DepartmentDTO departmentDTO = new DepartmentDTO(4, "IT", "Fix computers.", true);
        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> departmentService.save(departmentDTO));
        assertEquals("Department not found with ID 4.", e.getMessage());
    }

    @Test
    void testParseResponse() {
        Department department = new Department(2, "Sales", "Sells stuff.", false);
        DepartmentDTO departmentDTO = departmentService.parseResponse(department);

        assertEquals(2, departmentDTO.getId());
        assertEquals("Sales", departmentDTO.getName());
        assertEquals("Sells stuff.", departmentDTO.getDescription());
        assertFalse(departmentDTO.isActive());
    }

    @Test
    void testParseRequest() {
        DepartmentDTO departmentDTO = new DepartmentDTO(1, "IT", "Fix computers.", true);
        Department department = departmentService.parseRequest(departmentDTO);

        assertEquals(1, department.getId());
        assertEquals("IT", department.getName());
        assertEquals("Fix computers.", department.getDescription());
        assertTrue(department.isActive());
    }
}