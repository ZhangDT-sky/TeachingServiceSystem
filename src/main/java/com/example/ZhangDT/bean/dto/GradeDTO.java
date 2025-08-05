package com.example.ZhangDT.bean.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradeDTO {
    @TableId
    public Integer gradeId;

    public String courseName;

    public String studentName;

    public String semesterYear;

    public Integer semesterTime;

    public double grade;

    public double credits;
}
