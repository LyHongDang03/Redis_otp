package com.example.Redis.repository;

import com.example.Redis.entity.HrmDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HrmDataRepository extends JpaRepository<HrmDataEntity, String> {
    Optional<HrmDataEntity> findByPhone(String phone);
    Optional<HrmDataEntity> findByCode(String code);
}
