package org.example.service;

import org.example.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    User updateUser(Long id, User user);
    void deleteUserById(Long id);
    User findUserById(Long id);
    User findUserByEmail(String email);
    List<User> findAllUsers();
    boolean existsByEmail(String email);
}
