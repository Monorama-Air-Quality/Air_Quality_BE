package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.HealthUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthUserRepository extends JpaRepository<HealthUser, Long> {
    Boolean existsHealthUserByEmail(String email);
}
