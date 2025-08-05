package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.CourseOffer;
import com.example.ZhangDT.bean.dto.CourseOfferingDTO;
import com.example.ZhangDT.mapper.CourseOfferingMapper;
import com.example.ZhangDT.service.CourseOfferingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseOfferingServiceImpl implements CourseOfferingService {
    @Autowired
    private CourseOfferingMapper courseOfferingMapper;

    @Override
    public List<CourseOfferingDTO> getOfferingsByMajorId(Integer majorId) {
        return courseOfferingMapper.selectByMajorId(majorId);
    }

    @Override
    public List<CourseOfferingDTO> getOfferingsByStudentId(Integer studentId) {
        return courseOfferingMapper.selectByStudentId(studentId);
    }

    @Override
    public List<CourseOfferingDTO> getOfferingsBySemesterCodeAndMajorCode(String semesterCode, String majorCode) {
        return courseOfferingMapper.selectBySemesterYearAndMajorName(semesterCode, majorCode);
    }

    @Override
    public CourseOffer add(CourseOffer courseOffer) {
        int result = courseOfferingMapper.insert(courseOffer);
        if(result == 0) { return null; }
        return courseOffer;
    }

    @Override
    public void delete(Integer courseId, Integer majorId, Integer semesterId) {
        // 联合主键删除
        courseOfferingMapper.deleteByIds(courseId, majorId, semesterId);
    }

    @Override
    public CourseOffer update(CourseOffer courseOffer) {
        int result = courseOfferingMapper.updateByIds(courseOffer);
        if(result > 0) {
            return courseOfferingMapper.selectByIds(courseOffer.getCourseId(), courseOffer.getMajorId(), courseOffer.getSemesterId());
        } else {
            return null;
        }
    }

    @Override
    public CourseOffer getByIds(Integer courseId, Integer majorId, Integer semesterId) {
        return courseOfferingMapper.selectByIds(courseId, majorId, semesterId);
    }

    @Override
    public List<CourseOffer> getAll() {
        return courseOfferingMapper.selectList(null);
    }
} 