package com.project.anyahajo.service.impl;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserForm> findAllUsers() {

        List<User> users = userRepository.findAll();
        return users.stream().map(user -> mapToUserForm(user)).toList();
    }

    @Override
    public void updateUserRole(Long id, Role role) {
        userRepository.updateRoleByUser_id(id, role);
    }

    @Override
    public User findUserByUser_id(Long id) {
        return userRepository.findByUser_id(id);
    }

    @Override
    public UserForm findUserById(long userId) {
        User user = userRepository.findById(userId).get();
        return mapToUserForm(user);
    }

    @Override
    public void updateUser(UserForm userForm, Long userId) {
        User user = userRepository.findById(userId).get();

        userForm.setEnabled(user.getEnabled());
        userForm.setLocked(user.getLocked());
        userForm.setRole(user.getRole());
        User UpdatedUser = mapToUser(userForm);
        userRepository.save(UpdatedUser);
    }

    private User mapToUser(UserForm userForm) {

        return new User(userForm.getId(),
                userForm.getName(),
                userForm.getEmail(),
                passwordEncoder.encode(userForm.getPassword()),
                userForm.getPhoneNumber(),
                userForm.getLocked(),
                userForm.getEnabled(),
                userForm.getEnableUrl(),
                userForm.getRole(),
                userForm.getBasket(),
                userForm.getResetPasswordToken()
        );
    }

    public User findUserByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }
   public User findpasswordtoken(String resetPasswordToken){
        return userRepository.findByResetPasswordToken(resetPasswordToken);
   }

    private UserForm mapToUserForm(User user) {

        return UserForm.builder()
                .id(user.getUser_id())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .locked(user.getLocked())
                .enabled(user.isEnabled())
                .basket(user.getBasket())
                .resetPasswordToken(user.getResetPasswordToken())
                .build();
    }
}
