package com.example.Redis.dto;

import com.example.Redis.entity.HrmDataEntity;
import lombok.Data;

@Data
public class CtvResponse {
    private String assessToken;
    private String reFreshToken;
    private HrmDataEntity ctv;
}
