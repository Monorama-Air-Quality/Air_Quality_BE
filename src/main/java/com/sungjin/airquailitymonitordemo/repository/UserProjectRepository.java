package com.sungjin.airquailitymonitordemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sungjin.airquailitymonitordemo.entity.HealthUser;
import com.sungjin.airquailitymonitordemo.entity.Project;
import com.sungjin.airquailitymonitordemo.entity.UserProject;

@Repository
public interface UserProjectRepository extends JpaRepository<UserProject, Long> {
    boolean existsByHealthUserAndProject(HealthUser healthUser, Project project);
} 