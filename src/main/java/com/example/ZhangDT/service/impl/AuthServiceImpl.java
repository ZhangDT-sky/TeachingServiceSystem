package com.example.ZhangDT.service.impl;

import com.example.ZhangDT.bean.Admin;
import com.example.ZhangDT.bean.Student;
import com.example.ZhangDT.bean.Teacher;
import com.example.ZhangDT.mapper.AdminMapper;
import com.example.ZhangDT.mapper.StudentMapper;
import com.example.ZhangDT.mapper.TeacherMapper;
import com.example.ZhangDT.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private TeacherMapper teacherMapper;

    public String login(String userType, String id, String password) {
        switch (userType) {
            case "admin":
                Admin admin = adminMapper.selectById(Integer.valueOf(id));
                if (admin != null && admin.getAdminPassword().equals(password)) {
                    return jwtTokenUtil.generateToken(
                        String.valueOf(admin.getAdminId()), // userId
                        userType,                           // userType
                        admin.getAdminName()                // username
                    );
                }
                break;
            case "student":
                Student student = studentMapper.selectById(id);
                if(student != null&&student.getStudentPassword().equals(password)) {
                    return jwtTokenUtil.generateToken(
                            student.getStudentId(),
                            userType,
                            student.getStudentName()
                    );
                }
                break;
            case "teacher":
                Teacher teacher = teacherMapper.selectById(Integer.valueOf(id));
                if(teacher != null&&teacher.getPassword().equals(password)) {
                    return jwtTokenUtil.generateToken(
                            String.valueOf(teacher.getTeacherId()),
                            userType,
                            teacher.getName()
                    );
                }
                break;
            default:
                return null;
        }
        return null;
    }

}
