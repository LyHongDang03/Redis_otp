package com.example.Redis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CtvOtpResendRequest {
    @JsonProperty("otpRequestId")
    private String otpRequestId;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("deviceId")
    private String deviceId;

    public boolean isValid() {
        return otpRequestId != null && !otpRequestId.isBlank()
                && phone != null && !phone.isBlank()
                && deviceId != null && !deviceId.isBlank();
    }
}
