package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.Student;
import com.example.ZhangDT.bean.dto.StudentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StudentMapper extends BaseMapper<Student> {

    /**
     * 查询所有学生的DTO信息（可根据实际需要调整SQL，示例为简单查询）
     */
    @Select("SELECT s.student_id as studentId, s.student_name as studentName, s.student_phone as studentPhone, " +
            "s.student_birthday as studentBirthday, cl.class_name as className, c.college_name as collegeName, m.major_name as majorName " +
            "FROM student s " +
            "LEFT JOIN class cl ON s.class_id = cl.class_id " +
            "LEFT JOIN college c ON s.college_id = c.college_id " +
            "LEFT JOIN major m ON s.major_id = m.major_id")

    List<StudentDTO> selectStudentDTOList();

    /**
     * 根据studentId查询单个StudentDTO
     */
    @Select("SELECT s.student_id as studentId, s.student_name as studentName,"+
            " s.student_phone as studentPhone, s.student_birthday as studentBirthday, "+
            "cl.class_name as className, c.college_name as collegeName, m.major_name as majorName "+
            "FROM student s LEFT JOIN class cl ON s.class_id = cl.class_id LEFT JOIN college c ON s.college_id = c.college_id LEFT JOIN major m ON s.major_id = m.major_id WHERE s.student_id = #{studentId}")
    StudentDTO selectStudentDTOById(String studentId);

    @Select("select student_id from student where major_id = #{majorId}")
    List<String> getStudentByMajor(@Param("majorId") Integer majorId);
}
