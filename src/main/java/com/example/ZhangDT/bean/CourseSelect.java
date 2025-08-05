package com.example.ZhangDT.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSelect {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @NotNull
    private Integer courseId;
    @NotNull
    private Integer majorId;
    @NotEmpty
    private String semesterYear;
    @NotNull
    private Integer semesterTime;

    @NotNull(message = "学分不能为空")
    private double courseCredits;

    @Min(value = 1,message = "课程容量必须大于0")
    private Integer courseCount;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm::ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm::ss")
    private LocalDateTime updateTime;

}
