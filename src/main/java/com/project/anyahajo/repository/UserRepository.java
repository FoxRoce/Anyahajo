package com.project.anyahajo.repository;

import com.project.anyahajo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

//    @Transactional
//    @Modifying
//    @Query("delete from User.ah_user_basket b where b.owner_id = ?1 and b.item_id = ?2")
//    void deleteByUser_id(Long user_id, Long item_id);

//    @Modifying
//    @Query("delete from UserBasket b where b.ownerId = :userId and b.itemId = :itemId")
//    void deleteByOwnerIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);
}
