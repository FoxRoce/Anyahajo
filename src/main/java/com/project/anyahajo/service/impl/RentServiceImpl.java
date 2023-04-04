package com.project.anyahajo.service.impl;

import com.project.anyahajo.model.Rent;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.RentRepository;
import com.project.anyahajo.service.RentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RentServiceImpl implements RentService {

    @NonNull
    private RentRepository rentRepository;




    @Override
    public void save(Rent rent) {
        rentRepository.save(rent);
    }

    @Override
    public void deleteById(Long id) {
        rentRepository.deleteById(id);
    }



    @Override
    public List<Rent> findAll() {
        return rentRepository.findAll();
    }

    @Override
    public Rent findByRent_id(Long id) {
        return rentRepository.findByRent_id(id);
    }

    @Override
    public List<Rent> findByUser(User user) {
        return rentRepository.findByUser(user);
    }

    @Override
    public List<Rent> findExpiredByUser_id(User user) {
        return rentRepository.findExpiredByUser_id(user);
    }

    @Override
    public List<Rent> findWaitAcceptByUser_id(User user) {
        return rentRepository.findWaitAcceptByUser_id(user);
    }

    @Override
    public List<Rent> findBorrowedByUser_id(User user) {
        return rentRepository.findBorrowedByUser_id(user);
    }

    @Override
    public List<Rent> findBorrowed() {
        return rentRepository.findBorrowed();
    }

    @Override
    public List<Rent> findWaitAccept() {
        return rentRepository.findWaitAccept();
    }

    @Override
    public List<Rent> findExpired() {
        return rentRepository.findExpired();
    }
}
