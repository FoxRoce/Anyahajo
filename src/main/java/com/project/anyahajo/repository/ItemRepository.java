package com.project.anyahajo.repository;

import com.project.anyahajo.model.Availability;
import com.project.anyahajo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {

    @Transactional
    @Modifying
    @Query("delete from Item i")
    void deleteAll();

    @Query("SELECT i FROM Item i WHERE i.name LIKE CONCAT('%', :text, '%')")
    List<Item> findByText(String text);

    @Query("SELECT i FROM Item i WHERE i.name LIKE CONCAT('%', :text, '%') AND TYPE(i) = :type")
    List<Item> findBySearch(String text, Class type);


    @Query("select i from Item i where i.item_id = ?1")
    Item findByItem_id(Long item_id);

    @Query("select i from Item i where i.availability = ?1 and i.isActive = true")
    List<Item> findRentable(Availability availability);



}
