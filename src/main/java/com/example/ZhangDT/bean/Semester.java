package com.example.ZhangDT.bean;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Semester {

    @TableId(type = IdType.AUTO)
    private Integer semesterId;

    @Pattern(regexp = "^\\d{4}-\\d{4}$", message = "学年格式应为XXXX-XXXX")
    private String semesterYear;

    @Min(value = 1, message = "学期时间只能是1或2")
    @Max(value = 2, message = "学期时间只能是1或2")
    private Integer semesterTime;


    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm::ss")
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm::ss")
    private LocalDateTime updateTime;
}
