package com.example.ZhangDT.controller;

import com.example.ZhangDT.bean.dto.LoginRequestDTO;
import com.example.ZhangDT.core.ResponseMessage;
import com.example.ZhangDT.service.impl.AuthServiceImpl;
import com.example.ZhangDT.service.impl.CaptchaServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthServiceImpl authService;

    @Autowired
    private CaptchaServiceImpl captchaService;


    @GetMapping("/captcha")
    public ResponseMessage<String> getCaptcha(){
        String captchaId = captchaService.generateCaptcha();
        return ResponseMessage.success(captchaId);
    }

    @PostMapping("/login")
    public ResponseMessage<String> login(@RequestBody @Validated LoginRequestDTO loginRequestDTO) {
        //先校验验证码
        if(!captchaService.validateCaptcha(loginRequestDTO.getCaptchaId(),loginRequestDTO.getCaptcha())){
            return ResponseMessage.fail("验证码错误");
        }

        // 验证码正确后，继续原有登录逻辑
        String token =  authService.login(loginRequestDTO.getUserType(),loginRequestDTO.getUserId(),loginRequestDTO.getPassword());
        if(token != null) {
            return ResponseMessage.success(token);
        }
        return ResponseMessage.fail("用户不存在或密码错误");
    }

}
