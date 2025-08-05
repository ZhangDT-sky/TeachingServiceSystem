package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.CourseSelect;
import com.example.ZhangDT.bean.Course;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.CourseSelectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/courseOfferMajor")
public class CourseSelectController {

    @Autowired
    private CourseSelectService courseSelectService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    // 1. 管理员发布选课（添加课程开放信息）
    @PostMapping("/add")
    public ResponseMessage<String> addCourseOfferMajor(@RequestBody CourseSelect courseSelect) {
        boolean saved = courseSelectService.save(courseSelect);

        // 初始化Redis容量
        redisTemplate.opsForValue().set("course:"+ courseSelect.getCourseId()+":capacity", String.valueOf(courseSelect.getCourseCount()));
        if(saved){
            // 初始化Redis容量
            redisTemplate.opsForValue().set("course:"+ courseSelect.getCourseId()+":capacity", String.valueOf(courseSelect.getCourseCount()));
            courseSelectService.insert(courseSelect);
            return ResponseMessage.success("发布成功");

        }
        return ResponseMessage.fail("发布失败");
    }

    // 2. 学生查询自己可选课程
    @GetMapping("/available")
    public ResponseMessage<List<Course>> getAvailableCourses(@RequestParam Integer majorId,
                                                            @RequestParam String semesterYear,
                                                            @RequestParam Integer semesterTime) {
        List<Course> availableCourses = courseSelectService.getAvailableCourses(majorId, semesterYear, semesterTime);
        return ResponseMessage.success(availableCourses);
    }
} 