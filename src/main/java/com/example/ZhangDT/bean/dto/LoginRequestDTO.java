package com.example.ZhangDT.bean.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoginRequestDTO {

    private  String userType;

    private String userId;

    private String password;

    private String captchaId;    // 验证码ID

    private String captcha;      // 用户输入的验证码

}
