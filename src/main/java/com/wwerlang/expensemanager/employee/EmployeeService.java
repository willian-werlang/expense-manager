package com.wwerlang.expensemanager.employee;

import com.wwerlang.expensemanager.department.Department;
import com.wwerlang.expensemanager.department.DepartmentDTO;
import com.wwerlang.expensemanager.department.DepartmentService;
import com.wwerlang.expensemanager.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentService departmentService;

    public EmployeeDTO find(long id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);

        if (employeeOptional.isPresent()) {
            Employee employee = employeeOptional.get();
            return parseResponse(employee);
        } else {
            throw new EntityNotFoundException("Employee", id);
        }
    }

    public List<EmployeeDTO> list() {
        List<EmployeeDTO> employees = new ArrayList<>();
        employeeRepository.findAll().forEach(e -> employees.add(parseResponse(e)));
        return employees;
    }

    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        long id = employeeDTO.getId();
        boolean isUpdate = id != 0;

        if (isUpdate && !employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee", id);
        }

        Employee employee = parseRequest(employeeDTO);
        employee = employeeRepository.save(employee);
        return parseResponse(employee);
    }

    public EmployeeDTO parseResponse(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setActive(employee.isActive());

        DepartmentDTO departmentDTO = departmentService.parseResponse(employee.getDepartment());
        employeeDTO.setDepartment(departmentDTO);

        return employeeDTO;
    }

    public Employee parseRequest(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setId(employeeDTO.getId());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setActive(employeeDTO.isActive());

        Department department = departmentService.parseRequest(employeeDTO.getDepartment());
        employee.setDepartment(department);

        return employee;
    }
}
