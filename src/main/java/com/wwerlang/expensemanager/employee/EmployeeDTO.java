package com.wwerlang.expensemanager.employee;

import com.wwerlang.expensemanager.department.DepartmentDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {

    private long id;
    private String firstName;
    private String lastName;
    private boolean active;
    private DepartmentDTO department;
}
