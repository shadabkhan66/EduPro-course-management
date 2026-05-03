package com.eduproject.modules.course.mapper;


import com.eduproject.modules.course.dto.CourseRequest;
import com.eduproject.modules.course.dto.CourseResponse;
import com.eduproject.modules.course.entity.CourseEntity;
import org.springframework.beans.BeanUtils;

public class CourseMapper {


    public static CourseResponse toResponse(CourseEntity courseEntity) {
        CourseResponse courseResponse = new CourseResponse();
        BeanUtils.copyProperties(courseEntity,courseResponse); // i have to replace with batter logic

        return courseResponse;

    }

    public static CourseEntity toEntity(CourseRequest courseRequest) {
        CourseEntity courseEntity = new CourseEntity();
        BeanUtils.copyProperties(courseRequest,courseEntity);

        return courseEntity;
    }

    public static void updateEntityFromRequest(CourseRequest request, CourseEntity course) {
        BeanUtils.copyProperties(request, course, "id", "version", "createdBy", "createdDate");
    }

    // ==================== ENTITY → RESPONSE ====================

    public static CourseResponse toResponse(CourseEntity entity) {

        if (entity == null) return null;

        return CourseResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .departmentId(entity.getDepartment() != null ? entity.getDepartment().getId() : null)
                .instructorId(entity.getInstructor() != null ? entity.getInstructor().getId() : null)
                .isActive(entity.getIsActive())
                .enrolledCount(entity.getEnrolledCount())
                .build();
    }

    // ==================== REQUEST → ENTITY ====================

    public static CourseEntity toEntity(CourseRequest request) {

        if (request == null) return null;

        CourseEntity entity = new CourseEntity();

        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCredits(request.getCredits());

        return entity;
    }

    // ==================== UPDATE ENTITY ====================

    public static void updateEntityFromRequest(CourseRequest request, CourseEntity entity) {

        if (request == null || entity == null) return;

        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setCredits(request.getCredits());

        // intentionally NOT updating:
        // id, createdBy, createdDate, relationships
    }
}
