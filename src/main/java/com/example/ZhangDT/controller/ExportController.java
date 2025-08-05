package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.ExportTask;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.impl.GradeExportServiceImpl;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
public class ExportController {

    private GradeExportServiceImpl gradeExportServiceImpl;

    /**
     * 创建成绩导出任务
     */
    @PostMapping("/")
    public ResponseMessage<String> createGradeExport(@RequestParam String studentId,@RequestParam String semesterId){
        String taskId= gradeExportServiceImpl.createExportTask(studentId,semesterId);
        return ResponseMessage.success(taskId);
    }

    /**
     * 查询导出任务状态
     */
    @GetMapping("/task/{taskId}")
    public ResponseMessage<ExportTask> getTaskStatus(@PathVariable String taskId) {
        ExportTask task = gradeExportServiceImpl.getTaskStatus(taskId);
        if (task != null) {
            return ResponseMessage.success(task);
        }
        return ResponseMessage.fail("任务不存在或已过期");
    }
}
