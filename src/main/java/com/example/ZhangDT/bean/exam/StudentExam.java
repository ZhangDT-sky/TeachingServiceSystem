package com.example.ZhangDT.bean.exam;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentExam {

    private String studentId;
    private Integer examId;

    private String courseName;

    private Integer classroomId;

    private Integer timeSlot;
}
