package com.project.anyahajo.service.impl;

import com.project.anyahajo.form.UserForm;
import com.project.anyahajo.model.Role;
import com.project.anyahajo.model.User;
import com.project.anyahajo.repository.UserRepository;
import com.project.anyahajo.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    @NonNull
    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    @NonNull
    private UserRepository userRepository;
    @NonNull
    private PasswordEncoder passwordEncoder;



    @Override
    public void save(User newUser) {
        userRepository.save(newUser);
    }



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                String.format(USER_NOT_FOUND_MSG, email)
                        )
                );
    }



    @Override
    public List<UserForm> findAllUsers() {

        List<User> users = userRepository.findAll();
        return users.stream().map(this::mapToUserForm).toList();
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserForm findUserById(long userId) {
        User user = userRepository.findById(userId).get();
        return mapToUserForm(user);
    }

    @Override
    public User findUserByUser_id(Long id) {
        return userRepository.findByUser_id(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findUserByUserEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    @Override
    public User findByEnableUrl(String url) {
        return userRepository.findByEnableUrl(url);
    }

    public User findpasswordtoken(String resetPasswordToken){
        return userRepository.findByResetPasswordToken(resetPasswordToken);
    }



    @Override
    public void updateUserRole(Long id, Role role) {
        userRepository.updateRoleByUser_id(id, role);
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




    public UserForm mapToUserForm(User user) {

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
                .tokenExpiration(user.getTokenExpiration())
                .build();
    }

    public User mapToUser(UserForm userForm) {
        return new User(userForm.getId(),
                userForm.getName(),
                userForm.getEmail(),
                passwordEncoder.encode(userForm.getPassword()),
                userForm.getPhoneNumber(),
                userForm.getLocked(),
                userForm.getEnabled(),
                userForm.getEnableUrl(),
                userForm.getRole(),
                userForm.getResetPasswordToken(),
                userForm.getTokenExpiration(),
                userForm.getBasket()
        );

    }

}



