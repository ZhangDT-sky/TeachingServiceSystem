package com.example.ZhangDT.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExportTask {
    private String taskId;

    private String taskType;// "grade_export"

    private String status; //"processing","comleted","failed"

    private Integer progress;

    private String result; //结果信息，文件路径

    private String errorMessage; //错误信息

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
