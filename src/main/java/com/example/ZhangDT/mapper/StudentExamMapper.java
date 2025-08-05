package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.exam.StudentExam;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface StudentExamMapper extends BaseMapper<StudentExam> {
    @Select("SELECT * FROM student_exam WHERE exam_id = #{examId}")
    List<StudentExam> findByExamId(Integer examId);

    @Select("SELECT * FROM student_exam WHERE student_id = #{studentId}")
    List<StudentExam> findByStudentId(String studentId);


    @Update("UPDATE student_exam SET course_name = #{courseName}, classroom_id = #{classroomId} WHERE student_id = #{studentId} AND exam_id = #{examId}")
    int update(StudentExam studentExam);

    @Delete("DELETE FROM student_exam WHERE student_id = #{studentId} AND exam_id = #{examId}")
    int delete(@Param("studentId") String studentId, @Param("examId") Integer examId);

    @Select("SELECT * FROM student_exam")
    List<StudentExam> findAll();

    @Delete("delete from student_exam where exam_id = #{examId}")
    void deleteByexamid(@Param("examId") Integer examId);
}