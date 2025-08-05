package com.example.ZhangDT.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.bean.Grade;
import com.example.ZhangDT.bean.Semester;
import com.example.ZhangDT.bean.Student;
import com.example.ZhangDT.bean.dto.GradeDTO;
import com.example.ZhangDT.mapper.CourseMapper;
import com.example.ZhangDT.mapper.GradeMapper;
import com.example.ZhangDT.mapper.SemesterMapper;
import com.example.ZhangDT.mapper.StudentMapper;
import com.example.ZhangDT.service.GradeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GradeServiceimpl implements GradeService {

    @Autowired
    GradeMapper gradeMapper;

    @Autowired
    SemesterMapper semesterMapper;

    @Autowired
    StudentMapper studentMapper;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    StringRedisTemplate redisTemplate;
    /**
     * Java 8 Stream 批量组装
     * @return
     */
    @Override
    public List<GradeDTO> getAllGrade() {
        List<Grade> list=gradeMapper.selectList(null);
        return list.stream().map(grade -> {
            GradeDTO dto = new GradeDTO();
            BeanUtils.copyProperties(grade,dto);

            dto.setGrade(grade.getGradeScore());

            Semester semesterNew=semesterMapper.selectById(grade.getSemesterId());
            dto.setSemesterYear(semesterNew.getSemesterYear());
            dto.setSemesterTime(semesterNew.getSemesterTime());

            Student student=studentMapper.selectById(grade.getStudentId());
            dto.setStudentName(student.getStudentName());

            Course course =courseMapper.selectById(grade.getCourseId());
            dto.setCourseName(course.getCourseName());
            dto.setCredits(course.getCourseCredits());

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 构建DTO返回前端
     * @param gradeId
     * @return
     */
    @Override
    public GradeDTO getGradebyid(Integer gradeId) {
        Grade grade=gradeMapper.selectById(gradeId);
        if (grade == null) return null;

        GradeDTO dto = new GradeDTO();
        BeanUtils.copyProperties(grade,dto);

        Semester semesterNew=semesterMapper.selectById(grade.getSemesterId());
        dto.setSemesterYear(semesterNew.getSemesterYear());
        dto.setSemesterTime(semesterNew.getSemesterTime());

        Student student=studentMapper.selectById(grade.getStudentId());
        dto.setStudentName(student.getStudentName());

        Course course =courseMapper.selectById(grade.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setCredits(course.getCourseCredits());

        return dto;
    }

    @Override
    public Grade add(Grade grade) {
        int result=gradeMapper.insert(grade);
        if(result==0){return null;}
        String key = "gpa:" + grade.getStudentId();
        // 用JSON字符串存储分数和学分
        double credit = courseMapper.selectById(grade.getCourseId()).getCourseCredits();
        String value = String.format("{\"score\":%.2f,\"credit\":%.2f}", grade.getGradeScore(), credit);
        redisTemplate.opsForHash().put(key, String.valueOf(grade.getCourseId()), value);
        return grade;
    }

    @Override
    public void delete(Integer gradeId) {
        Grade grade = gradeMapper.selectById(gradeId);
        if (grade != null) {
            String key = "gpa:" + grade.getStudentId();
            redisTemplate.opsForHash().delete(key, grade.getCourseId());
        }
        gradeMapper.deleteById(gradeId);
    }

    @Override
    public Grade update(Grade grade) {
        int result = gradeMapper.updateById(grade);
        if (result > 0) {
            // 更新Redis中的成绩和学分
            String key = "gpa:" + grade.getStudentId();
            double credit = courseMapper.selectById(grade.getCourseId()).getCourseCredits();
            String value = String.format("{\"score\":%.2f,\"credit\":%.2f}", grade.getGradeScore(), credit);
            redisTemplate.opsForHash().put(key, grade.getCourseId(), value);
            return gradeMapper.selectById(grade.getGradeId());
        } else {
            return null;
        }
    }

    @Override
    public double calculateGpaFromRedis(String studentId) {
        String key = "gpa:" + studentId;
        Map<Object, Object> courseMap = redisTemplate.opsForHash().entries(key);
        if (courseMap == null || courseMap.isEmpty()) {
            return 0.0;
        }
        double totalWeightedGpa = 0;
        double totalCredit = 0;
        for (Object value : courseMap.values()) {
            JSONObject obj = JSON.parseObject(value.toString());
            double score = obj.getDoubleValue("score");
            double credit = obj.getDoubleValue("credit");
            // 绩点算法举例：绩点 = 分数/20，实际按你的规则调整
            double gpa = score / 20.0;
            totalWeightedGpa += gpa * credit;
            totalCredit += credit;
        }
        return totalCredit > 0 ? totalWeightedGpa / totalCredit : 0.0;
    }

    @Override
    public List<Grade> getStudentgrade(String studentId) {
        QueryWrapper<Grade> wrapper=new QueryWrapper<>();
        wrapper.eq("studentId",studentId);
        List<Grade> list=gradeMapper.selectList(wrapper);
        return list;
    }
}
