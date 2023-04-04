package com.project.anyahajo.service;

import com.project.anyahajo.model.Rent;
import com.project.anyahajo.model.User;

import java.util.List;

public interface RentService {

    void save(Rent rent);

    void deleteById(Long id);



    List<Rent> findAll();

    Rent findByRent_id(Long id);

    List<Rent> findByUser(User user);

    List<Rent> findExpiredByUser_id(User user);

    List<Rent> findWaitAcceptByUser_id(User user);

    List<Rent> findBorrowedByUser_id(User user);

    List<Rent> findBorrowed();

    List<Rent> findWaitAccept();

    List<Rent> findExpired();
}
