package com.example.datn_be.repository.NhanVien;

import com.example.datn_be.entiy.NhanVien.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserApp, Integer> {
    Optional<UserApp> findByUsername(String username);


}
