package com.project.anyahajo.repository;

import com.project.anyahajo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {
    @Transactional
    @Modifying
    @Query("delete from Item i")
    void deleteAll();

    @Query("SELECT i FROM Item i WHERE i.name LIKE CONCAT('%', :text, '%')")
    List<Item> findByText(String text);

}
