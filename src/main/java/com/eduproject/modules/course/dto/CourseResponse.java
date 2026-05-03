package com.eduproject.modules.course.dto;

import com.eduproject.modules.department.dto.DepartmentResponse;
import com.eduproject.modules.department.entity.DepartmentEntity;
import com.eduproject.modules.users.entity.UserEntity;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private Integer durationInHours;
    private BigDecimal fees;
    private Integer capacity;
    private Integer enrolledCount;
    private Boolean isActive;
    private DepartmentResponse department;
    private UserEntity instructor;
}
