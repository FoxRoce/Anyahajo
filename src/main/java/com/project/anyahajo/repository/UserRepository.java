package com.project.anyahajo.repository;

import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = ?1")
    User findByUserEmail(String email);

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.role = ?2 where u.user_id = ?1")
    void updateRoleByUser_id(Long id, Role role);

    @Query("select u from User u where u.user_id = ?1")
    User findByUser_id(Long user_id);

    @Query("select u from User u where u.resetPasswordToken = ?1")
    User findByResetPasswordToken(String resetPasswordToken);
}

