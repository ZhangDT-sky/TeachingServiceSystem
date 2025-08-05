package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.ClassSchedule;
import java.util.List;

public interface ClassScheduleService {
    ClassSchedule getById(Integer id);
    List<ClassSchedule> getAll();
    ClassSchedule add(ClassSchedule classSchedule);
    void delete(Integer id);
    ClassSchedule update(ClassSchedule classSchedule);
} 