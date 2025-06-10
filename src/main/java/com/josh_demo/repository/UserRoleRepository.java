package com.josh_demo.repository;

import com.josh_demo.model.UserRole;
import com.josh_demo.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    List<UserRole> findByUserId(Long userId);
    void deleteByUserId(Long userId);
} 