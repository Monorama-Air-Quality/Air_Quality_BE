package com.sungjin.airquailitymonitordemo.repository;

import com.sungjin.airquailitymonitordemo.entity.HealthUser;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthUserRepository extends JpaRepository<HealthUser, Long> {
    Optional<HealthUser> findByEmail(String email);
}
