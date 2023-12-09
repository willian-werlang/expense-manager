package com.wwerlang.expensemanager.department;

import com.wwerlang.expensemanager.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public DepartmentDTO find(long id) {
        Optional<Department> departmentOptional = departmentRepository.findById(id);

        if (departmentOptional.isPresent()) {
            Department department = departmentOptional.get();
            return parseResponse(department);
        } else {
            throw new EntityNotFoundException("Department", id);
        }
    }

    public List<DepartmentDTO> list() {
        List<DepartmentDTO> departments = new ArrayList<>();
        departmentRepository.findAll().forEach(e -> departments.add(parseResponse(e)));
        return departments;
    }

    public DepartmentDTO save(DepartmentDTO departmentDTO) {
        long id = departmentDTO.getId();
        boolean isUpdate = id != 0;

        if (isUpdate && !departmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Department", id);
        }

        Department department = parseRequest(departmentDTO);
        department = departmentRepository.save(department);
        return parseResponse(department);
    }

    public DepartmentDTO parseResponse(Department department) {
        return DepartmentDTO.builder()
                .id(department.getId())
                .name(department.getName())
                .description(department.getDescription())
                .active(department.isActive())
                .build();
    }

    public Department parseRequest(DepartmentDTO departmentDTO) {
        Department department = new Department();
        department.setId(departmentDTO.getId());
        department.setName(departmentDTO.getName());
        department.setDescription(departmentDTO.getDescription());
        department.setActive(departmentDTO.isActive());
        return department;
    }
}
