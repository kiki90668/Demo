package com.example.trading.service.impl;


import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.trading.dto.LoginByMobileReqDTO;
import com.example.trading.dto.LoginByMobileResDTO;
import com.example.trading.dto.SendSmsCodeReqDTO;
import com.example.trading.dto.SendSmsCodeResDTO;
import com.example.trading.dto.UserBalanceResDTO;
import com.example.trading.dto.UserLoginReqDTO;
import com.example.trading.dto.UserLoginResDTO;
import com.example.trading.dto.UserRegisterReqDTO;
import com.example.trading.dto.UserRegisterResDTO;
import com.example.trading.entity.BizException;
import com.example.trading.entity.User;
import com.example.trading.entity.UserSmsCode;
import com.example.trading.mapper.UserMapper;
import com.example.trading.repo.UserRepository;
import com.example.trading.repo.UserSmsCodeRepository;
import com.example.trading.service.JwtService;
import com.example.trading.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserSmsCodeRepository userSmsCodeRepository;

    @Override
    public UserRegisterResDTO register(UserRegisterReqDTO reqDTO) {
        //檢查使用者是否存在
        if (userRepository.existsByUsername(reqDTO.getUsername())) {
            throw new BizException(400, "User already exists");
        }

        //檢查手機號碼是否已註冊
        if (userRepository.existsByMobileNo(reqDTO.getMobileNo())) {
            throw new BizException(400, "Mobile number already registered");
        }

        //註冊新使用者
        User user = User.builder()
                .username(reqDTO.getUsername())
                .password(passwordEncoder.encode(reqDTO.getPassword()))
                .email(reqDTO.getEmail())
                .mobileNo(reqDTO.getMobileNo())
                .build();
       userRepository.save(user); //儲存到資料庫

       return UserMapper.toResDTO(user);
    }

    @Override
    public UserLoginResDTO login(UserLoginReqDTO reqDTO) {
        //檢查使用者是否存在
        User user = userRepository.findByUsername(reqDTO.getUsername())
                .orElseThrow(() -> new BizException(400, "User not found"));
        //檢查密碼是否正確
        if (!passwordEncoder.matches(reqDTO.getPassword(), user.getPassword())) {
            throw new BizException(400, "Invalid password");
        }

        //生成JWT token
        String token = jwtService.generateToken(user.getUsername());
        return UserMapper.toLoginResDTO(user, token);
    }

    @Override
    public SendSmsCodeResDTO sendSmsCode(SendSmsCodeReqDTO reqDTO) {
        //查詢最新一筆的發送紀錄
        Optional<UserSmsCode> lastCode = userSmsCodeRepository
                .findTopByMobileNoAndIsUsedOrderBySendTimeDesc(reqDTO.getMobileNo(), false);

        //檢查是否在60秒內重複發送
        if (lastCode.isPresent()) {
            LocalDateTime lastTime = lastCode.get().getSendTime();
            if (lastTime.plusSeconds(60).isAfter(LocalDateTime.now())) {
                throw new BizException(429, "Please wait 60 seconds before requesting a new code");
            }
        }

        //檢查今日發送次數是否超過5次
        long sentCount = userSmsCodeRepository.countTodaySent(reqDTO.getMobileNo());
        if (sentCount >= 5) {
            throw new BizException(429, "You have reached the daily limit of 5 SMS requests.");
        }

        //生成六位數字的驗證碼
        String smsCode = String.format("%06d", new SecureRandom().nextInt(1000000));
        UserSmsCode userSmsCode = UserSmsCode.builder()
                .mobileNo(reqDTO.getMobileNo())
                .smsCode(smsCode)
                .isUsed(false)
                .sendTime(LocalDateTime.now())
                .build();
        userSmsCodeRepository.save(userSmsCode);

        
        //模擬簡訊發送成功
        System.out.println("SMS code sent to " + reqDTO.getMobileNo() + " , code : " + smsCode);

        SendSmsCodeResDTO resDTO = new SendSmsCodeResDTO();
        resDTO.setSuccess(true);
        resDTO.setMessage("SMS code sent successfully");
        return resDTO;
    }

    @Override
    public LoginByMobileResDTO loginByMobile(LoginByMobileReqDTO reqDTO) {

        //檢查使用者是否存在
        User user = userRepository.findByMobileNo(reqDTO.getMobileNo())
                .orElseThrow(() -> new BizException(400, "User not found"));

        //查詢最新一筆的發送紀錄
        Optional<UserSmsCode> smsCodeOtp = userSmsCodeRepository
                .findTopByMobileNoAndIsUsedOrderBySendTimeDesc(reqDTO.getMobileNo(), false);


        //檢查驗證碼是否正確
        if (smsCodeOtp.isEmpty() || !smsCodeOtp.get().getSmsCode().equals(reqDTO.getSmsCode())) {
            throw new BizException(400, "Invalid or expired SMS code");
        }

        //檢查驗證碼是否過期
        if (smsCodeOtp.get().getSendTime().plusMinutes(5).isBefore(LocalDateTime.now())) {
            throw new BizException(400, "SMS code expired");
        }

        smsCodeOtp.get().setIsUsed(true); //標記為已使用
        userSmsCodeRepository.save(smsCodeOtp.get());

        //生成JWT token
        String token = jwtService.generateToken(user.getUsername());

        LoginByMobileResDTO resDTO  = new LoginByMobileResDTO();
        resDTO.setUsername(user.getUsername());
        resDTO.setToken(token);
        return resDTO;
    }

    //查詢餘額
    @Override
    public UserBalanceResDTO getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BizException(400, "User not found"));
        return UserMapper.getUserBalance(user, user.getBalance());
    }
}