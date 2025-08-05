package com.example.ZhangDT.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseOffer {

//    @TableId(value = "course_id", type = IdType.INPUT)
    @NotNull
    private Integer courseId; // 课程代码


    @NotNull
//    @TableId(value = "major_id", type = IdType.INPUT)
    private Integer majorId;  // 专业代码

    @NotNull
//    @TableId(value = "semester_id", type = IdType.INPUT)
    private Integer semesterId; // 学期代码

    @NotEmpty
    private String courseName;
    @NotNull
    private double courseCredits;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm::ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm::ss")
    private LocalDateTime updateTime;

}