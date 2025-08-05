package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.Major;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MajorMapper extends BaseMapper<Major> {
    // 统计某专业人数
    @Select("SELECT COUNT(*) FROM student WHERE major_id = #{majorId}")
    int countStudentsByMajorId(@Param("majorId") Integer majorId);

}