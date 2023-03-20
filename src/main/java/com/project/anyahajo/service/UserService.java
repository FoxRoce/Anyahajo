package com.project.anyahajo.service;

import com.project.anyahajo.form.UserForm;

import java.util.List;

public interface UserService {

    List<UserForm> findAllUsers();

    void updateIsadmin(Boolean admin, Long id);
}
