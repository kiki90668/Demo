package com.example.trading.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trading.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByMobileNo(String mobileNo);
    Optional<User> findByMobileNo(String mobileNo);
}
