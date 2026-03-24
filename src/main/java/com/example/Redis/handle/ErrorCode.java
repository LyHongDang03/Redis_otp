package com.example.Redis.handle;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    INVALID_PHONE(1001, "Số điện thoại không đúng", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1001, "Otp không đúng", HttpStatus.BAD_REQUEST),
    OTP_LIMIT_EXCEEDED(1002, "Bạn gửi OTP quá nhiều lần", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1003, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_REDIS(1004, "Không có trong redis",  HttpStatus.BAD_REQUEST),
    INVALID_RESEND(1005, "Chưa đến thời gian gửi lại otp", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
