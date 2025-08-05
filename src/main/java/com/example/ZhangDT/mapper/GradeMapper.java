package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    @Select("select * from grade where student_id = #{studentId} and semster_id=#{semesterId}")
    List<Grade> selectGradesByStudentAndSemester(String studentId, String semesterId);
}
