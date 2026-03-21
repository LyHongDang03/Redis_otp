package com.example.Redis.service;

import com.example.Redis.config.CtvAuthConfig;
import com.example.Redis.dto.CtvOtpResponse;
import com.example.Redis.dto.CtvOtpSession;
import com.example.Redis.dto.CtvOtpVerifyRequest;
import com.example.Redis.dto.SendOtpReq;
import com.example.Redis.entity.HrmDataEntity;
import com.example.Redis.handle.BusinessException;
import com.example.Redis.handle.ErrorCode;
import com.example.Redis.repository.HrmDataRepository;
import com.example.Redis.util.Util;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
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
    private static final String OTP_RATE_PHONE_PREFIX = "ctv:otp:rate:phone";
    private final RedisTemplate<String, Object> redisTemplate;


    public CtvOtpResponse sendOtp(SendOtpReq req){
        HrmDataEntity ctv = hrmDataRepository.findByPhone(req.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PHONE));
        String normalizedPhone = util.fmNumber(req.getPhone());
        String otp = Util.getOTP();
        CtvOtpSession session = buildSession(ctv, req.getDeviceId(), req.getChannel(), normalizedPhone, otp, 0);
        validateLimit(normalizedPhone);
        saveSession(session);
        return CtvOtpResponse.builder()
                .otpRequestId(session.getOtpRequestId())
                .expiredIn(config.getOtpExpireSeconds())
                .resendIn(config.getOtpResendAfterSeconds())
                .maskedPhone(normalizedPhone)
                .note( config.isSkipPartnerValidation() ? otp : "" )
                .build();
    }

    public HrmDataEntity verifyOtp(CtvOtpVerifyRequest request){
        HrmDataEntity ctv = hrmDataRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PHONE));
        String normalizedPhone = util.fmNumber(request.getPhone());
        CtvOtpSession session = getSession(request.getOtpRequestId());  // Lấy từ redis ra
        if (!request.getOtp().equals(session.getOtpHash()) ||
                !request.getDeviceId().equals(session.getDeviceId()) ||
                !session.getPhone().equals(normalizedPhone)
        ) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        if (session.getResendCount() >= config.getOtpMaxResend()) {
            deleteSession(session.getOtpRequestId());
        }
        deleteSession(request.getOtpRequestId()); // Xoá khỏi redis

        return ctv;
   }

    public CtvOtpResponse reSendOtp(){
        return null;
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
        if(!redisTemplate.hasKey(OTP_KEY_PREFIX + otpRequestId)){
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        }
        String json = (String) redisTemplate.opsForValue().get(OTP_KEY_PREFIX + otpRequestId);
        return objectMapper.readValue(json, CtvOtpSession.class);
    }

    private void validateLimit(String phone){
        if(!config.isRateLimitEnabled()){
            return;
        }
        validateCounter(OTP_RATE_PHONE_PREFIX + phone, config.getOtpRateLimitPerPhone());

    }

    private void validateCounter(String key, int maxCount){
        Long count = redisTemplate.opsForValue().increment(key);
        if (count != null && count == 1L){
            redisTemplate.expire(key, Duration.ofSeconds(config.getOtpRateLimitWindowSeconds()));
        }
        if (count != null && count > maxCount){
            throw new BusinessException(ErrorCode.OTP_LIMIT_EXCEEDED);
        }
    }

    private void deleteSession(String otpRequestId){
        redisTemplate.delete(OTP_KEY_PREFIX + otpRequestId);
    }
}
