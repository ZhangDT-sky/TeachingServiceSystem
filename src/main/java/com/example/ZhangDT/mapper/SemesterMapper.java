package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.CourseSelect;
import com.example.ZhangDT.bean.Semester;
import org.apache.ibatis.annotations.Mapper;
 
@Mapper
public interface SemesterMapper extends BaseMapper<Semester> {
    boolean save(CourseSelect courseSelect);
}