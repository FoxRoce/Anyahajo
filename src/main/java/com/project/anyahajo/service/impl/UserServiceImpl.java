package com.project.anyahajo.service.impl;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserForm> findAllUsers() {

        List<User> users = userRepository.findAll();
        return users.stream().map(user -> mapToUserForm(user)).toList();
    }

    @Override
    public void updateUserRole(Long id, Role role) {
        userRepository.updateRoleByUser_id(id,role);
    }

    @Override
    public User findUserByUser_id(Long id) {
        return userRepository.findByUser_id(id);
    }

    public User findUserByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    private UserForm mapToUserForm(User user) {

        return UserForm.builder()
                .id(user.getUser_id())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .locked(user.getLocked())
                .enabled(user.isEnabled())
                .build();
    }
}
