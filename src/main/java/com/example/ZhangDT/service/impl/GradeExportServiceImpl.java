package com.example.ZhangDT.service.impl;

import com.alibaba.fastjson2.JSON;
import com.example.ZhangDT.bean.ExportTask;
import com.example.ZhangDT.bean.Grade;
import com.example.ZhangDT.bean.Semester;
import com.example.ZhangDT.mapper.GradeMapper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
public class GradeExportServiceImpl {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private SemesterServiceImpl semesterService;

    @Autowired
    private StudentServiceImpl studentService;

    @Autowired
    private CourseServiceImpl courseService;

    /**
     * 创建导出人物
     */

    public String createExportTask(String studentId,String semesterId){
        String taskId= UUID.randomUUID().toString();
        ExportTask task=new ExportTask();
        task.setTaskId(taskId);
        task.setTaskType("grade_export");
        task.setStatus("processing");
        task.setProgress(0);
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());

        //存储到redis
        String taskKey="export:task"+taskId;
        stringRedisTemplate.opsForValue().set(taskKey, JSON.toJSONString(task),2, TimeUnit.HOURS);

        //异步编程更高效地处理非阻塞操作
        CompletableFuture.runAsync(()->processExportTask(taskId,studentId,semesterId));

        return taskId;
    }

    /**
     * 异步处理导出任务
     */
    private void processExportTask(String taskId,String studentId,String semesterId){
        String taskKey="export:task"+taskId;
        try{
            //更新进度为10%
            updateTaskProgress(taskId,10,"proceessing");

            //查询成绩数据
            List<Grade> grades =
                    gradeMapper.selectGradesByStudentAndSemester(studentId,semesterId);
            //更新进度为50%
            updateTaskProgress(taskId,50,"proceessing");


            //生成Excel
            String fileName="grades_"+studentId+"_"+semesterId+".xlsx";
            String filePath=generateExcelFile(grades,fileName);

            //更新进度90%
            updateTaskProgress(taskId,90,"proceessing");

            //完成
            ExportTask task=new ExportTask();
            task.setTaskId(taskId);
            task.setTaskType("grade_export");
            task.setStatus("completed");
            task.setProgress(100);
            task.setResult(filePath);
            task.setCreateTime(LocalDateTime.now());
            stringRedisTemplate.opsForValue().set(taskKey, JSON.toJSONString(task),2, TimeUnit.HOURS);
        }catch (Exception e){
            //fail
            ExportTask task=new ExportTask();
            task.setTaskId(taskId);
            task.setTaskType("grade_export");
            task.setStatus("failed");
            task.setProgress(0);
            task.setErrorMessage(e.getMessage());
            task.setUpdateTime(LocalDateTime.now());
            stringRedisTemplate.opsForValue().set(taskKey, JSON.toJSONString(task),2, TimeUnit.HOURS);
        }
    }

    /**
     * 更新任务状态
     * @param taskId
     * @param progress
     * @param status
     */
    private void updateTaskProgress(String taskId,int progress,String status){
        String taskKey="export:task"+taskId;
        String taskJson=stringRedisTemplate.opsForValue().get(taskKey);
        if(taskJson!=null){
            try{
                ExportTask task=JSON.parseObject(taskJson,ExportTask.class);
                task.setProgress(progress);
                task.setStatus(status);
                stringRedisTemplate.opsForValue()
                        .set(taskKey, taskJson,2, TimeUnit.HOURS);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 查询任务状态
     * @param taskId
     * @return
     */
    public ExportTask getTaskStatus(String taskId){
        String taskKey="export:task"+taskId;
        String taskJson=stringRedisTemplate.opsForValue().get(taskKey);
        if(taskJson!=null){
            try{
                return JSON.parseObject(taskJson,ExportTask.class);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 生成Excel文件
     * @param grades
     * @param fileName
     * @return
     */
    private String generateExcelFile(List<Grade> grades, String fileName){
        try(Workbook workbook=new XSSFWorkbook()){
            Sheet sheet=workbook.createSheet("成绩表");

            //创建表头
            Row row=sheet.createRow(0);
            row.createCell(0).setCellValue("学生姓名");
            row.createCell(1).setCellValue("课程名称");
            row.createCell(2).setCellValue("成绩");
            row.createCell(3).setCellValue("学期信息");

            int rowNum=1;
            for(Grade grade:grades){
                Row row1=sheet.createRow(rowNum++);
                row1.createCell(0).setCellValue(studentService.getStudentByid(grade.getStudentId()).getStudentName());
                row1.createCell(1).setCellValue(courseService.getCourseById(grade.getCourseId()).getCourseName());
                row1.createCell(2).setCellValue(grade.getGradeScore());
                Semester semester=semesterService.getSemesterById(grade.getSemesterId());
                row1.createCell(3).setCellValue(semester.getSemesterYear()+"--"+semester.getSemesterTime().toString());
            }
            // 保存文件到本地
            String filePath = "/exports/" + fileName;
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
            return filePath;
        }catch (Exception e){
            throw new RuntimeException("生成Excel文件失败", e);
        }
    }
}
