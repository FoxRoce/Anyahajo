package com.project.anyahajo.repository;

import com.project.anyahajo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
