package com.example.Redis.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CtvOtpResponse {
    private String otpRequestId;
    private long expiredIn;
    private long resendIn;
    private String maskedPhone;
    private String note;
}
