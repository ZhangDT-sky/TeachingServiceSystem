package com.example.ZhangDT.bean.exam;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamSchedule {

    private Integer examId;

    private Integer timeSlot;

    private List<Integer> classroomId;


}
