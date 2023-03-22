package com.project.anyahajo.repository;

import com.project.anyahajo.model.Item;
import com.project.anyahajo.model.Rent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface RentRepository extends JpaRepository<Rent,Long> {
    @Transactional
    @Modifying
    @Query("update Rent r set r.item = ?1, r.startOfRent = ?2, r.endOfRent = ?3 where r.rent_id = ?4")
    void updateItemAndStartOfRentAndEndOfRentByRent_id(Item item, LocalDate startOfRent, LocalDate endOfRent, Long rent_id);

    @Query("select r from Rent r where r.rent_id = ?1")
    Rent findByRent_id(Long rent_id);

    @Transactional
    @Modifying
    @Query("update Rent r set r.item = ?1 where r.rent_id = ?2")
    int updateItemByRent_id(Item item, Long rent_id);

}