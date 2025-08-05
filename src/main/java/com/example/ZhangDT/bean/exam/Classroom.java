package com.example.ZhangDT.bean.exam;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classroom {
    @TableId(type = IdType.AUTO)
    private Integer classroomId;
    private String classroomName;
    private Integer capacity;
}
