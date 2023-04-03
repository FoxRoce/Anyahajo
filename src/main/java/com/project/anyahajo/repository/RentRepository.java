package com.project.anyahajo.repository;

import com.project.anyahajo.model.Item;
import com.project.anyahajo.model.Rent;
import com.project.anyahajo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface RentRepository extends JpaRepository<Rent,Long> {

    @Query("select r from Rent r where r.rent_id = ?1")
    Rent findByRent_id(Long rent_id);

    @Query("select r from Rent r where r.user = ?1")
    List<Rent> findByUser_id(User user);

    @Query("select r from Rent r where r.user = ?1")
    List<Rent> findByUser(User user);

    @Query("select r from Rent r where r.history IS NULL AND r.startOfRent IS NOT NULL ")
    List<Rent> findBorrowed();

    @Query("select r from Rent r where r.startOfRent IS NULL ")
    List<Rent> findWaitAccept();

    @Query("select r from Rent r where r.history IS NOT NULL ")
    List<Rent> findExpired();

    @Query("select r from Rent r where r.history IS NULL AND r.startOfRent IS NOT NULL AND r.user = ?1 ")
    List<Rent> findBorrowedByUser_id(User user);

    @Query("select r from Rent r where r.startOfRent IS NULL AND r.user = ?1 ")
    List<Rent> findWaitAcceptByUser_id(User user);

    @Query("select r from Rent r where r.history IS NOT NULL AND r.user = ?1 ")
    List<Rent> findExpiredByUser_id(User user);
}