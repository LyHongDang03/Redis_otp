package com.example.Redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ctv-auth")
@Data
public class CtvAuthConfig {
    private boolean otpTestPurpose = false;
    private boolean skipPartnerValidation = true;
    private boolean rateLimitEnabled = true;
    private String jwtSecret;
    private String jwtIssuer = "homehub-service";
    private String jwtAudience = "ctv-portal";
    private long accessExpireSeconds = 900;
    private long refreshExpireSeconds = 2592000;
    private long otpExpireSeconds = 60;
    private long otpSessionSeconds = 600;
    private long otpResendAfterSeconds = 60;
    private int otpMaxRetry = 5;
    private int otpMaxResend = 3;
    private int otpRateLimitPerPhone = 3;
    private int otpRateLimitPerIp = 10;
    private long otpRateLimitWindowSeconds = 600;
    private boolean qrCreateRateLimitEnabled = true;
    private int qrCreateRateLimitPerCtv = 10;
    private int qrCreateRateLimitPerIp = 20;
    private long qrCreateRateLimitWindowSeconds = 60;
    private boolean qrCreateDeduplicateEnabled = true;
    private long qrCreateDeduplicateWindowSeconds = 10;
    private boolean qrRedirectRateLimitEnabled = true;
    private int qrRedirectRateLimitPerIp = 120;
    private int qrRedirectRateLimitPerShortCode = 300;
    private int qrRedirectRateLimitPerIpShortCode = 20;
    private long qrRedirectRateLimitWindowSeconds = 60;
    private int qrShortCodeLength = 8;
    private long qrExpireDays = 30;
}
