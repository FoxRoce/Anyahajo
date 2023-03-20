package com.project.anyahajo.repository;

import com.project.anyahajo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update User u set u.isAdmin = ?1 where u.user_id = ?2")
    void updateDoneByIdAndOwner(boolean admin, Long id);
}
