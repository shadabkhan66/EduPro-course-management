package com.eduproject.model;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@Setter
public class CourseResponse {

    private Long id;

//    @NotBlank(message = "Course title is required")
//    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

//    @NotBlank(message = "Course description is required")
//    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

//    @Min(value = 1, message = "Duration must be at least 1 hour")
    private Integer durationInHours;

//    @DecimalMin(value = "0.0", message = "Fees must be non-negative")
    private BigDecimal fees;

    private String instructor;
}
