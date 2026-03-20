package com.example.Redis.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CtvOtpSession {
    private String otpRequestId;
    private String ctvCode;
    private String phone;
    private String deviceId;
    private String channel;
    private String otpHash;
    private int attemptCount;
    private int resendCount;
    private Date otpExpiredAt;
}
