package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Class;
import java.util.List;

public interface ClassService {
    Class getClassById(Integer classId);
    List<Class> getAllClass();
    Class add(Class clazz);
    void delete(Integer classId);
    Class update(Class clazz);
} 