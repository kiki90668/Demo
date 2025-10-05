package com.example.trading.dto;

import lombok.Data;

@Data
public class UserRegisterReqDTO {
    private String username;
    private String password;
    private String email;
    private String mobileNo;
}
