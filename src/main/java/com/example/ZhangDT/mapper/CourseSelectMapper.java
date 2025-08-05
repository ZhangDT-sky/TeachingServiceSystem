package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.bean.CourseSelect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CourseSelectMapper extends BaseMapper<CourseSelect> {
    @Select("select c2.course_name,c1.course_credits from course_select as c1 , course as c2 " +
            "where c1.major_id = #{majorId} and c1.semester_year = #{semesterYear} and c1.semester_time = #{semesterTime} " +
            "and c1.course_id = c2.course_id")
    List<Course> getAvailableCourses(Integer majorId, String semesterYear, Integer semesterTime);


    @Select("select exists(select 1 from course where course_id = #{id})")
    boolean save(@Param("id") Integer id);


}