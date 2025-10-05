package com.example.trading.mapper;

import java.math.BigDecimal;

import com.example.trading.dto.UserBalanceResDTO;
import com.example.trading.dto.UserLoginResDTO;
import com.example.trading.dto.UserRegisterReqDTO;
import com.example.trading.dto.UserRegisterResDTO;
import com.example.trading.entity.User;

public class UserMapper {
    public static User toEntiy (UserRegisterReqDTO dto) {
        return User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword()) //尚未加密
                .mobileNo(dto.getMobileNo())
                .balance(BigDecimal.ZERO) // 初始餘額設為0
                .build();
    }

    public static UserRegisterResDTO toResDTO(User user) {
        UserRegisterResDTO resDTO = new UserRegisterResDTO();
        resDTO.setId(user.getId());
        resDTO.setUsername(user.getUsername());
        resDTO.setEmail(user.getEmail());
        resDTO.setMobileNo(user.getMobileNo());
        return resDTO;
    }

    public static UserLoginResDTO toLoginResDTO(User user, String token) {
        UserLoginResDTO resDTO = new UserLoginResDTO();
        resDTO.setUsername(user.getUsername());
        resDTO.setToken(token);
        return resDTO;
    }

    public static UserBalanceResDTO getUserBalance(User user, BigDecimal balance) {
        UserBalanceResDTO resDTO = new UserBalanceResDTO();
        resDTO.setUsername(user.getUsername());
        resDTO.setBalance(balance);
        return resDTO;
    }

}
