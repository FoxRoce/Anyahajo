package com.project.anyahajo.service;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;

import java.util.List;

public interface UserService {

    List<UserForm> findAllUsers();

    void updateUserRole(Long id, Role tole);

    User findUserByUser_id(Long id);

    UserForm findUserById(long userId);

    void updateUser(UserForm user, Long userId);

    User findUserByUserEmail(String email);

    User findpasswordtoken(String token);

    UserForm mapToUserForm(User userGet);

    Object mapToUser(UserForm oldUser);
}
