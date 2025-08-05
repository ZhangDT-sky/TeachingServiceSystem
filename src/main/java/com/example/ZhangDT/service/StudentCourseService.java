package com.example.ZhangDT.service;

import com.example.ZhangDT.core.ResponseMessage;

public interface StudentCourseService {
    ResponseMessage<String> selectCourse(String studentId, Integer courseId,String semesterYear,String semesterTime);

    ResponseMessage<String> dropCourse(String studentId, Integer courseId,String semesterYear,String semesterTime);
}
