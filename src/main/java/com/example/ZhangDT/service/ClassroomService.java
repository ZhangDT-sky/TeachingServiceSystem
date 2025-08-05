package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.exam.Classroom;
import java.util.List;

public interface ClassroomService {
    List<Classroom> getAllclassroom();
    Classroom getById(Integer classroomId);
    Classroom add(Classroom classroom);
    Classroom update(Classroom classroom);
    boolean delete(Integer classroomId);
}
