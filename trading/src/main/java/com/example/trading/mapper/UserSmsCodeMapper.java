package com.example.trading.mapper;

import com.example.trading.dto.LoginByMobileReqDTO;
import com.example.trading.entity.UserSmsCode;

public class UserSmsCodeMapper {

    public static UserSmsCode toEntiy (LoginByMobileReqDTO dto) {
        return UserSmsCode.builder()
                .mobileNo(dto.getMobileNo())
                .smsCode(dto.getSmsCode())
                .build();
    }

}
