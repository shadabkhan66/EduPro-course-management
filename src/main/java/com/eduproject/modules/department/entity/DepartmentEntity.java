package com.eduproject.modules.department.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "DEPARTMENT")
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60 , name = "DEPARTMENT_NAME", unique = true)
    private String name;

    @Column( unique = true, name="DEPARTMENT_CODE")
    private String code;

    @Column(name = "DEPARTMENT_DESCRIPTION")
    private String description;
}
