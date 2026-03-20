package com.example.Redis.repository;

import com.example.Redis.entity.HrmDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HrmDataRepository extends JpaRepository<HrmDataEntity, String> {
    HrmDataEntity findByPhone(String phone);
}
