package com.example.ZhangDT.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ZhangDT.bean.Semester;
import com.example.ZhangDT.mapper.SemesterMapper;
import com.example.ZhangDT.service.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemesterServiceImpl implements SemesterService {

    @Autowired
    private SemesterMapper semesterMapper;

    @Override
    public List<Semester> getAllSemesters(){
        return semesterMapper.selectList(null);
    }

    @Override
    public Semester getSemesterById(Integer semesterId) {
        return semesterMapper.selectById(semesterId);
    }

    @Override
    public Semester addSemester(Semester semester) {
        int result = semesterMapper.insert(semester);
        return result > 0 ? semester : null;
    }

    @Override
    public Semester updateSemester(Semester semester) {
        int result = semesterMapper.updateById(semester);
        if (result > 0) {
            return semesterMapper.selectById(semester.getSemesterId());
        } else {
            return null;
        }
    }

    @Override
    public Semester deleteSemester(Integer semesterId) {
        Semester semester = semesterMapper.selectById(semesterId);
        int result = semesterMapper.deleteById(semesterId);
        return result > 0 ? semester : null;
    }
} 