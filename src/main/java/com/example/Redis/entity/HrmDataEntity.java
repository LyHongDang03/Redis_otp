package com.example.Redis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Entity
@Data
@Table(name = "HH_HRM_DATA")
public class HrmDataEntity implements Serializable {
    @Id
    String code;
    String fullName;
    String phone;
    String email;
    String organization;
    String company;
    String department;
    String jobTitle;
    String position;
    Date birthday;
    String gender;
}
