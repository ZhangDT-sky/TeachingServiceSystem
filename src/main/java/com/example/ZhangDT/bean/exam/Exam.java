package com.example.ZhangDT.bean.exam;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    @TableId(type = IdType.AUTO)
    private Integer examId;

    @NotNull
    private Integer courseId;

    private String courseName;

    private Integer examCount;
}
