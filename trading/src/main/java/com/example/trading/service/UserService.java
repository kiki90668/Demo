package com.example.trading.service;

import com.example.trading.dto.LoginByMobileReqDTO;
import com.example.trading.dto.LoginByMobileResDTO;
import com.example.trading.dto.SendSmsCodeReqDTO;
import com.example.trading.dto.SendSmsCodeResDTO;
import com.example.trading.dto.UserBalanceResDTO;
import com.example.trading.dto.UserLoginReqDTO;
import com.example.trading.dto.UserLoginResDTO;
import com.example.trading.dto.UserRegisterReqDTO;
import com.example.trading.dto.UserRegisterResDTO;

public interface UserService {
    UserRegisterResDTO register(UserRegisterReqDTO reqDTO);

    UserLoginResDTO login(UserLoginReqDTO reqDTO);

    LoginByMobileResDTO loginByMobile(LoginByMobileReqDTO reqDTO);

    SendSmsCodeResDTO sendSmsCode(SendSmsCodeReqDTO reqDTO);

    UserBalanceResDTO getBalance(String username);

}
