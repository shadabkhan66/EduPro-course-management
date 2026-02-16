package com.eduproject.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "COURSES")
@Builder
public class CourseEntity {

	@Id
	@SequenceGenerator(allocationSize = 1, initialValue = 1_00_000, name = "SEQ_COURSE", sequenceName = "SEQ_COURSE")
	@GeneratedValue(generator = "SEQ_COURSE", strategy = GenerationType.SEQUENCE)
	private Long id;
	@Column(name = "COURSE_TITLE", unique = true, nullable = false, length = 100)
	private String title;
	@Column(name = "COURSE_DESCRIPTION", nullable = false, length = 500)
	private String description;
	@Column(name = "COURSE_DURATION_HOURS")
	private Integer durationInHours;
	@Column(name = "COURSE_INSTRUCTOR", length = 60)
	private String instructor;
	@Column(name = "COURSE_FEES")
	private BigDecimal fees; 
	
	
	
	@Version
	@Column(name = "VERSION")
	private Integer version;
	
	@Column(name= "CREATED_BY", updatable = false)
	private String createdBy;
	
	@Column(name = "CREATED_DATE", updatable = false)
	@CreationTimestamp 
	private LocalDateTime createdDate;
	
	@Column(name="UPDATED_DATE", insertable = false, updatable = true)
	@UpdateTimestamp
	private LocalDateTime updateDate ;
	
	@Column(name = "UPDATED_BY", insertable = false, updatable = true)
	private String updatedBy;
	
}