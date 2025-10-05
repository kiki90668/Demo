package com.example.trading.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trading.dto.LoginByMobileReqDTO;
import com.example.trading.dto.LoginByMobileResDTO;
import com.example.trading.dto.SendSmsCodeReqDTO;
import com.example.trading.dto.SendSmsCodeResDTO;
import com.example.trading.dto.UserBalanceResDTO;
import com.example.trading.dto.UserLoginReqDTO;
import com.example.trading.dto.UserLoginResDTO;
import com.example.trading.dto.UserRegisterReqDTO;
import com.example.trading.dto.UserRegisterResDTO;
import com.example.trading.entity.ApiResponse;
import com.example.trading.service.impl.UserServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userService;

    @PostMapping("/register")
    public ApiResponse<UserRegisterResDTO> register(@RequestBody UserRegisterReqDTO reqDTO) {
        return ApiResponse.success(userService.register(reqDTO));
    }

    @PostMapping("/login")
    public ApiResponse<UserLoginResDTO> login(@RequestBody UserLoginReqDTO reqDTO) {
        return ApiResponse.success(userService.login(reqDTO));
    }

    @PostMapping("/sendSmsCode")
    public ApiResponse<SendSmsCodeResDTO> sendSmsCode(@RequestBody SendSmsCodeReqDTO reqDTO) {
        return ApiResponse.success(userService.sendSmsCode(reqDTO));
    }

    @PostMapping("/login/mobile")
    public ApiResponse<LoginByMobileResDTO> loginByMobile(@RequestBody LoginByMobileReqDTO reqDTO) {
        return ApiResponse.success(userService.loginByMobile(reqDTO));
    }

// {
//   "username": "testuser01",
//   "password": "Test1234!"
// }

    @GetMapping("/balance")
    public ApiResponse<UserBalanceResDTO> getBalance() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ApiResponse.success(userService.getBalance(username));
    }
}