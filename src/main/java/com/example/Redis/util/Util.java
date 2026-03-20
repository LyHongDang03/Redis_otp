package com.example.Redis.util;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Random;
@Service
public class Util {
    public static String getOTP(){
        Random random = new Random();
        int number = random.nextInt(999999);
        return  String.format("%06d", number);
    }

    public boolean isPhoneNumber(String phone){
        return (phone.length() == 10 && phone.startsWith("0")) ||
                (phone.length() == 11 && phone.startsWith("84")) ||
                (phone.length() == 12 && phone.startsWith("+84"));
    }

    public String fmNumber(String phoneNumber){
        if (!StringUtils.hasText(phoneNumber)) {
            return null;
        }
        if (phoneNumber.length() == 10 && phoneNumber.startsWith("0")) {
            phoneNumber = "84" + phoneNumber.substring(1);
        }
        if (phoneNumber.length() == 12 && phoneNumber.startsWith("+84")) {
            phoneNumber = phoneNumber.substring(1);
        }
        return phoneNumber;
    }
//
//    public static String hashPassword(String password) {
//        return Hashing.sha256()
//                .hashString(password, StandardCharsets.UTF_8)
//                .toString();
//    }
}
