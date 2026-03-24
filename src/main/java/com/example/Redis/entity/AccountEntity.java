package com.example.Redis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class AccountEntity {
    @Id
    private String ctvCode;
    private String reFreshToken;
}
