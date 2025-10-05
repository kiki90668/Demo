package com.example.trading.repo;

import java.util.Optional;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.trading.entity.UserSmsCode;

public interface UserSmsCodeRepository extends JpaRepository<UserSmsCode, Long> {
    //查詢最新的驗證碼
    Optional<UserSmsCode> findTopByMobileNoAndIsUsedOrderBySendTimeDesc(String mobileNo, Boolean isUsed);

    //查詢在特定日期的發送次數
    @Query("SELECT COUNT(u) FROM UserSmsCode u " + "WHERE u.mobileNo = :mobileNo AND DATE(u.sendTime) = CURRENT_DATE")
    long countTodaySent(@Param("mobileNo") String mobileNo);
}
