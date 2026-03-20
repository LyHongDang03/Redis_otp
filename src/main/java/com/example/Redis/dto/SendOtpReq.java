package com.example.Redis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendOtpReq {
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("deviceId")
    private String deviceId;
    @JsonProperty("channel")
    private String channel;
    public boolean isValid() {
        return phone != null && !phone.isBlank()
                && deviceId != null && !deviceId.isBlank()
                && channel != null && !channel.isBlank();
    }
}
