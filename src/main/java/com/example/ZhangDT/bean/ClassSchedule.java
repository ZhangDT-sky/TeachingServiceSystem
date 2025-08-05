package com.example.ZhangDT.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassSchedule {
    @TableId(type = IdType.AUTO)
    private Integer id;
    //班级
    private Integer classId;
    private String className;
    //时间
    private Integer timeSlot;
    //老师
    private Integer teacherId;
    private String teacherName;
    //教室
    private Integer classRoomId;
    private String classRoomName;
    //课程
    private Integer courseId;
    private String courseName;

}
