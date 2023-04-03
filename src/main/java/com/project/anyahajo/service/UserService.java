package com.project.anyahajo.service;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    void save(User newUser);



    Object loadUserByUsername(String name);



    List<UserForm> findAllUsers();

    List<User> findAll();

    User findUserByUser_id(Long id);

    UserForm findUserById(long userId);

    Optional<User> findByEmail(String email);

    User findUserByUserEmail(String email);

    User findByEnableUrl(String url);

    User findpasswordtoken(String token);



    void updateUserRole(Long id, Role tole);

    void updateUser(UserForm user, Long userId);



    UserForm mapToUserForm(User userGet);

    Object mapToUser(UserForm oldUser);


}
