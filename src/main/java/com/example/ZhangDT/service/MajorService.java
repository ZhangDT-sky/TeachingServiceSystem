package com.example.ZhangDT.service;

import com.example.ZhangDT.bean.Major;
import java.util.List;

public interface MajorService {
    List<Major> getAllMajors();
    Major getMajorById(Integer majorId);
    Major add(Major major);
    Major delete(Integer majorId);
    Major update(Major major);
    int countStudentsByMajorId(Integer majorId);
} 