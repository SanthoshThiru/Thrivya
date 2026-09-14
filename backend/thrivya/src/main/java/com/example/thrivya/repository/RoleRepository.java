package com.example.thrivya.repository;

import com.example.thrivya.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository
        extends JpaRepository<Role, Long> {

    Optional<Role> findByTitleIgnoreCase(String title);
}