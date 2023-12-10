package com.wwerlang.expensemanager.department;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDTO {

    private long id;
    private String name;
    private String description;
    private boolean active;
}
