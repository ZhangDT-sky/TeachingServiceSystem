package com.example.ZhangDT.bean.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDTO {

    @TableId
    private String studentId;

    private String studentName;

    private String studentPhone;

    private Date studentBirthday;

    private String collegeName;
    private String majorName;
    private String className;


}
