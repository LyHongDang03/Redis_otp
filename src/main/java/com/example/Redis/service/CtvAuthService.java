package com.example.Redis.service;

import com.example.Redis.config.CtvAuthConfig;
import com.example.Redis.dto.CtvOtpResponse;
import com.example.Redis.dto.CtvOtpSession;
import com.example.Redis.dto.CtvOtpVerifyRequest;
import com.example.Redis.dto.SendOtpReq;
import com.example.Redis.entity.HrmDataEntity;
import com.example.Redis.repository.HrmDataRepository;
import com.example.Redis.util.Util;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CtvAuthService {
    private final ObjectMapper objectMapper;
    private final CtvAuthConfig config;
    private final Util util;
    private final HrmDataRepository hrmDataRepository;
    private static final String OTP_KEY_PREFIX = "ctv:otp:";
    private final RedisTemplate<String, Object> redisTemplate;


    public ResponseEntity<?> sendOtp(SendOtpReq req){
        HrmDataEntity ctv = hrmDataRepository.findByPhone(req.getPhone());
        String normalizedPhone = util.fmNumber(req.getPhone());
        String otp = Util.getOTP();
        CtvOtpSession session = buildSession(ctv, req.getDeviceId(), req.getChannel(), normalizedPhone, otp, 0);
        saveSession(session);
        CtvOtpResponse ctvOtpResponse = CtvOtpResponse.builder()
                .otpRequestId(session.getOtpRequestId())
                .expiredIn(config.getOtpExpireSeconds())
                .resendIn(config.getOtpResendAfterSeconds())
                .maskedPhone(normalizedPhone)
                .note( config.isSkipPartnerValidation() ? otp : "" )
                .build();
       return ResponseEntity.ok(ctvOtpResponse);
    }

    public ResponseEntity<?> verifyOtp(CtvOtpVerifyRequest request){
        HrmDataEntity ctv = hrmDataRepository.findByPhone(request.getPhone());
        String normalizedPhone = util.fmNumber(request.getPhone());
        CtvOtpSession session = getSession(request.getOtpRequestId());
        if (session.getResendCount() >= config.getOtpMaxResend()) {
            deleteSession(session.getOtpRequestId());
        }
        if (!session.getPhone().equals(normalizedPhone)) {
            throw new RuntimeException("Sai sdt");
        }
        if (!request.getOtp().equals(session.getOtpRequestId())) {
            throw new RuntimeException("Otp khong dung");
        }
        return  ResponseEntity.ok(ctv);
   }

    private CtvOtpSession buildSession(HrmDataEntity ctv,
                                       String deviceId,
                                       String channel,
                                       String normalizedPhone,
                                       String otp,
                                       int resendCount
    ){
        CtvOtpSession session = new CtvOtpSession();
        session.setOtpRequestId(UUID.randomUUID().toString().replace("-",""));
        session.setCtvCode(ctv.getCode());
        session.setPhone(normalizedPhone);
        session.setDeviceId(deviceId);
        session.setChannel(channel);
        session.setAttemptCount(0);
        session.setResendCount(resendCount);
        session.setOtpHash(otp);
        session.setOtpExpiredAt( Date.from(Instant.now().plusSeconds(config.getOtpExpireSeconds())) );
        return session;
    }

    private void saveSession(CtvOtpSession session){
        String key = OTP_KEY_PREFIX + session.getOtpRequestId();
        String value = objectMapper.writeValueAsString(session);
        Duration ttl = Duration.ofSeconds(config.getOtpSessionSeconds());
        redisTemplate.opsForValue().set(key,value,ttl);
    }

    private CtvOtpSession getSession(String otpRequestId){
        return (CtvOtpSession) (redisTemplate.opsForValue().get(OTP_KEY_PREFIX + otpRequestId));
    }

    private void deleteSession(String otpRequestId){
        redisTemplate.delete(OTP_KEY_PREFIX + otpRequestId);
    }
}
