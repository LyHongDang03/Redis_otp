package com.example.Redis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CtvOtpVerifyRequest {
    @JsonProperty("otpRequestId")
    private String otpRequestId;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("otp")
    private String otp;
    @JsonProperty("deviceId")
    private String deviceId;
}
