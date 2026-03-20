package com.example.Redis.controller;

import com.example.Redis.dto.CtvOtpVerifyRequest;
import com.example.Redis.dto.SendOtpReq;
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
    public ResponseEntity<?> sendOtp(@RequestBody SendOtpReq sendOtpReq) {
        return ctvAuthService.sendOtp(sendOtpReq);
    }
    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody CtvOtpVerifyRequest request){
        return ctvAuthService.verifyOtp(request);
    }
}
