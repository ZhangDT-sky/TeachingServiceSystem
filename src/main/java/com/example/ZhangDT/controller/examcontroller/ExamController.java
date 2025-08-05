package com.example.ZhangDT.controller.examcontroller;

import com.example.ZhangDT.bean.exam.ArrangeRequest;
import com.example.ZhangDT.bean.exam.Exam;
import com.example.ZhangDT.bean.exam.ExamSchedule;
import com.example.ZhangDT.bean.exam.StudentExam;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.ClassroomService;
import com.example.ZhangDT.service.ExamService;
import com.example.ZhangDT.service.examArrangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/exam")
public class ExamController {
    @Autowired
    private ExamService examService;

    @Autowired
    private examArrangeService examArrangeService;

    @Autowired
    private ClassroomService classroomService;


    @PostMapping("/add")
    public ResponseMessage<Exam> addExam(@RequestBody Exam exam) {
        Exam examNew=examService.addExam(exam);
        if(examNew!=null) return ResponseMessage.success(examNew);
        return ResponseMessage.fail("插入失败");
    }

    @GetMapping("/list")
    public List<Exam> getAllExams() {
        return examService.getAllExams();
    }

    @GetMapping("/{examId}")
    public Exam getExamById(@PathVariable Integer examId) {
        return examService.getExamById(examId);
    }

    @PostMapping("/arrange")
    public ResponseMessage<List<StudentExam>> arrangeExams(@RequestBody ArrangeRequest request) {
        System.out.println(request.getExamIds());
        //删除与考试有关的旧排考安排
        examService.deleteList(request.getExamIds());
        List<ExamSchedule> result=examArrangeService.arrangeExams(examService.getlistByexamid(request.getExamIds()),examService.getExamScheduleByexamid(request.getExamIds()),classroomService.getAllclassroom());
        List<StudentExam> studentExams=examService.assign(result);
        return ResponseMessage.success(studentExams);
    }
}
