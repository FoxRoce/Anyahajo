package com.project.anyahajo.repository;

import com.project.anyahajo.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {

    @Query("SELECT i FROM Item i WHERE i.name LIKE CONCAT('%', :text, '%')")
    List<Item> findByText(String text);

}
