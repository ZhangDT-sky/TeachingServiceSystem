package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.Teacher;
import com.example.ZhangDT.bean.dto.TeacherDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    // 根据学院ID查询该学院所有教师信息
    @Select("SELECT t.teacher_id, t.name, t.phone, t.email, t.course_id, c.name AS course_name, t.college_id, col.name AS college_name, t.create_time, t.update_time " +
            "FROM teacher t " +
            "LEFT JOIN college col ON t.college_id = col.id " +
            "LEFT JOIN course c ON t.course_id = c.id " +
            "WHERE col.name LIKE CONCAT('%',#{collegeName},'%')")
    List<TeacherDTO> selectByCollegeName(@Param("collegeName") String collegeName);
    
    // 根据课程名称查询相关教师信息
    @Select("SELECT t.teacher_id, t.name, t.phone, t.email, t.course_id, c.name AS course_name, t.college_id, col.name AS college_name, t.create_time, t.update_time " +
            "FROM teacher t " +
            "LEFT JOIN college col ON t.college_id = col.id " +
            "LEFT JOIN course c ON t.course_id = c.id " +
            "WHERE c.name LIKE CONCAT('%', #{courseName}, '%')")
    List<TeacherDTO> selectByCourseName(@Param("courseName") String courseName);

    @Select("select teacher_id from teacher where course_id = #{courseId}")
    List<Integer> getTeacherIdByCourseId(Integer courseId);
}
