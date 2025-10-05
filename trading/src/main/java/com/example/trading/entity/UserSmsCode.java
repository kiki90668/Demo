package com.example.trading.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sms_code")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ID

    @Column(name = "mobile_no", length = 15)
    private String mobileNo; // 使用者手機號碼

    @Column(name = "sms_code", length = 6, nullable = false)
    private String smsCode; // 簡訊驗證碼

    @Column(name = "is_used", columnDefinition = "TINYINT(1) DEFAULT 0")
    private Boolean isUsed; // 是否已使用(0=否,1=是)

    @Column(name = "send_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime sendTime; // 簡訊發送時間

    @Column(name = "create_time", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createTime; // 建立時間
}

