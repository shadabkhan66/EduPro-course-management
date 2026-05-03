package com.eduproject.modules.course.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.eduproject.modules.department.entity.DepartmentEntity;
import com.eduproject.modules.users.entity.UserEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA entity representing a course in the database.
 *
 * Uses @Version for optimistic locking (prevents concurrent update conflicts).
 * Uses @CreationTimestamp / @UpdateTimestamp for automatic audit fields.
 * ID: {@link jakarta.persistence.GenerationType#IDENTITY} — same as other domain entities (H2 + Oracle 12c+).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COURSES")
@Builder
public class CourseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "COURSE_TITLE", unique = true, nullable = false, length = 100)
	private String title;

	@Column(name = "COURSE_DESCRIPTION", nullable = false, length = 500)
	private String description;

	@Column(name = "COURSE_DURATION_HOURS")
	private Integer durationInHours;


	@Column(name = "COURSE_FEES")
	private BigDecimal fees;

    @Column(name= "COURSE_CAPACITY")
    private Integer capacity;

    @Column(name = "COURSE_ENROLLED_COUNT")
    private Integer enrolledCount = 0;  //need proper logic for enrollment count and given and default value 0

    @Column(name = "IS_COURSE_ACTIVE")
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private DepartmentEntity department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTRUCTOR_ID") //user with roel instructor
    private UserEntity instructor;

//    ================= META-DATA ==============================

	@Version
	@Column(name = "VERSION")
	private Integer version;

	@Column(name = "CREATED_BY",length =40 , updatable = false)
	private String createdBy;

	@Column(name = "CREATED_DATE", updatable = false)
	@CreationTimestamp
	private LocalDateTime createdDate;

	@Column(name = "UPDATED_DATE", insertable = false)
	@UpdateTimestamp
	private LocalDateTime updatedDate;

	@Column(name = "UPDATED_BY", insertable = false)
	private String updatedBy;

//    this logic is very wrong, instead i will be building logic in service class
//    @PrePersist
//    protected void incrementEnrolledCount() {
//
//        this.enrolledCount = this.enrolledCount + 1;
//    }

}
