package com.project.anyahajo.repository;

import com.project.anyahajo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ItemRepository extends JpaRepository<Item,Long> {
    @Transactional
    @Modifying
    @Query("delete from Item i")
    void deleteAll();

}
