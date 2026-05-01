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
}
