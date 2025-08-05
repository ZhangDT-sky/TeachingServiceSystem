package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.StudentCourse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface StudentCourseMapper extends BaseMapper<StudentCourse> {

    // 判断是否已存在选课记录
    @Select("select exists(select 1 from student_course where student_id = #{studentId} and course_id = #{courseId})")
    boolean exists(@Param("studentId") String studentId, @Param("courseId") Integer courseId);

    // 删除选课记录
//    @Delete("delete count(*) from studentcourse where student_id = #{studentId} and course_id = #{courseId}")
//    int deleteByStudentIdAndCourseId(@Param("studentId") String studentId, @Param("courseId") Integer courseId);

    @Delete("DELETE FROM student_course WHERE student_id = #{studentId} AND course_id = #{courseId}")
    int deleteByStudentIdAndCourseId(@Param("studentId") String studentId,
                                     @Param("courseId") Integer courseId);

    @Select("select count(*) from student_course where course_id = #{courseId}")
    int coutStuentsByCourseId(Integer courseId);


    @Select("select student_id from student_course where course_id = #{courseId}")
    List<String> selectByCourseid(Integer courseId);


}
