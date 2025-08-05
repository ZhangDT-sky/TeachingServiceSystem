package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.CourseOffer;
import com.example.ZhangDT.bean.dto.CourseOfferingDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Update;
import java.util.List;

@Mapper
public interface CourseOfferingMapper extends BaseMapper<CourseOffer> {
    // 根据专业ID查询该专业所有课程开设及学期信息
    @Select("SELECT co.course_id, c.course_name AS course_name, co.major_id, m.major_name AS major_name, co.semester_id, s.semester_year, s.semester_time " +
            "FROM course_offer co " +
            "JOIN course c ON co.course_id = c.course_id " +
            "JOIN major m ON co.major_id = m.major_id " +
            "JOIN semester s ON co.semester_id = s.semester_id " +
            "WHERE co.major_id = #{majorId}")
    List<CourseOfferingDTO> selectByMajorId(@Param("majorId") Integer majorId);

    // 根据学生ID查询其所属专业的所有课程开设及学期信息
    @Select("SELECT co.course_id, c.course_name AS course_name, co.major_id, m.major_name AS major_name, co.semester_id, s.semester_year, s.semester_time " +
            "FROM student st " +
            "JOIN course_offer co ON st.major_id = co.major_id " +
            "JOIN course c ON co.course_id = c.course_id " +
            "JOIN major m ON co.major_id = m.major_id " +
            "JOIN semester s ON co.semester_id = s.semester_id " +
            "WHERE st.student_id = #{studentId}")
    List<CourseOfferingDTO> selectByStudentId(@Param("studentId") Integer studentId);

    // 根据学期代码和专业代码查询课程代码
    @Select("SELECT co.course_id, c.course_name AS course_name, co.major_id, m.major_name AS major_name, co.semester_id, s.semester_year, s.semester_time " +
            "FROM course_offer co " +
            "JOIN course c ON co.course_id = c.course_id " +
            "JOIN major m ON co.major_id = m.major_id " +
            "JOIN semester s ON co.semester_id = s.semester_id " +
            "WHERE s.semester_year = #{semesterYear} AND m.major_name = #{majorName}")
    List<CourseOfferingDTO> selectBySemesterYearAndMajorName(@Param("semesterYear") String semesterYear, @Param("majorName") String majorName);

    @Select("select major_id from course_offer where course_id=#{courseId}")
    Integer selectMajorId(@Param("courseId") Integer courseId);

    @Select("select major_id from course_offer where course_id = #{courseId}")
    List<Integer> getMajoridByCourseid(@Param("courseId") Integer courseId);

    @Select("select course_id from course_offer where major_id =#{majorId}")
    List<Integer> getCourseIdByMajorId(@Param("majorId") Integer majorId);

    // 联合主键查找
    @Select("SELECT * FROM course_offer WHERE course_id = #{courseId} AND major_id = #{majorId} AND semester_id = #{semesterId}")
    CourseOffer selectByIds(@Param("courseId") Integer courseId, @Param("majorId") Integer majorId, @Param("semesterId") Integer semesterId);

    // 联合主键删除
    @Delete("DELETE FROM course_offer WHERE course_id = #{courseId} AND major_id = #{majorId} AND semester_id = #{semesterId}")
    int deleteByIds(@Param("courseId") Integer courseId, @Param("majorId") Integer majorId, @Param("semesterId") Integer semesterId);

    // 联合主键更新
    @Update("UPDATE course_offer SET course_name = #{courseOffer.courseName}, course_credits = #{courseOffer.courseCredits}, update_time = NOW() WHERE course_id = #{courseOffer.courseId} AND major_id = #{courseOffer.majorId} AND semester_id = #{courseOffer.semesterId}")
    int updateByIds(@Param("courseOffer") CourseOffer courseOffer);


    @Select("select student_id from course_offer as co  " +
            "Left join  " +
            "student as s " +
            "on co.major_id = s.major_id " +
            "where co.course_id = #{courseId} ")
    List<String> selectStudentIdByCourseId(@Param("courseId") Integer courseId);

}