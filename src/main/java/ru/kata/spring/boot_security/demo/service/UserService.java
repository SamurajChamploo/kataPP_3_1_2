package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    List<User> findAll();
    Optional<User> findUserById(Long id);
    void saveUser(User user, Set<Long> roleIds);
    void updateUser(Long id, String firstName, String lastName, Integer age, String email, String password, Set<Long> roleIds);
    void deleteUserById(Long id);
    List<Role> findAllRoles();
    UserDetails loadUserByUsername(String email);
}
