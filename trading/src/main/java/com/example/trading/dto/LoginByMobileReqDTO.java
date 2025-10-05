package com.example.trading.dto;

import lombok.Data;

@Data
public class LoginByMobileReqDTO {
    private String mobileNo;
    private String smsCode;
}
