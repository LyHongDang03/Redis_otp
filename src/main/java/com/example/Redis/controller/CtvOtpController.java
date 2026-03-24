package com.example.Redis.controller;

import com.example.Redis.dto.*;
import com.example.Redis.entity.HrmDataEntity;
import com.example.Redis.service.CtvAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/otp")
@AllArgsConstructor
public class CtvOtpController {
    private final CtvAuthService ctvAuthService;
    @PostMapping("/send")
    public ResponseEntity<ApiResponse<CtvOtpResponse>> sendOtp(@RequestBody SendOtpReq sendOtpReq) {
        return ResponseEntity.ok(ApiResponse.<CtvOtpResponse>builder()
                .result(ctvAuthService.sendOtp(sendOtpReq))
                .build());
    }
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<HrmDataEntity>> verify(@RequestBody CtvOtpVerifyRequest request){
        return ResponseEntity.ok(ApiResponse.<HrmDataEntity>builder()
                .result(ctvAuthService.verifyOtp(request))
                .build());
    }
    @PostMapping("/resend")
    public ResponseEntity<ApiResponse<CtvOtpResponse>> resendOtp(@RequestBody CtvOtpResendRequest request){
        return ResponseEntity.ok(ApiResponse.<CtvOtpResponse>builder()
                .result(ctvAuthService.reSendOtp(request))
                .build());
    }
}
