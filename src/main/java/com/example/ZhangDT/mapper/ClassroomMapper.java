package com.example.ZhangDT.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.ZhangDT.bean.exam.Classroom;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ClassroomMapper extends BaseMapper<Classroom> {
    @Select("SELECT * FROM classroom WHERE classroom_id = #{classroomId}")
    Classroom findById(Integer classroomId);

    @Select("SELECT * FROM classroom")
    List<Classroom> findAll();


}
