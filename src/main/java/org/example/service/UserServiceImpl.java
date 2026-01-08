package org.example.service;

import org.example.model.Role;
import org.example.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleService roleService,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User saveUser(User user) {

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String encoded = passwordEncoder.encode(user.getPassword());
            user.setPassword(encoded);
        }

        Set<Role> managedRoles = new HashSet<>();
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            Set<String> roleNames = new HashSet<>();
            for (Role role : user.getRoles()) {
                roleNames.add(role.getName());
            }
            managedRoles = roleService.getRolesByNames(roleNames);
        } else {
            Role userRole = roleService.findRoleByName("ROLE_USER");
            managedRoles.add(userRole);
        }

        user.setRoles(managedRoles);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = findUserById(id);

        existingUser.setFirstName(userDetails.getFirstName());
        existingUser.setLastName(userDetails.getLastName());
        existingUser.setAge(userDetails.getAge());
        existingUser.setEmail(userDetails.getEmail());

        if (userDetails.getPassword() != null &&
                !userDetails.getPassword().isEmpty() &&
                !userDetails.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getRoles() != null && !userDetails.getRoles().isEmpty()) {
            Set<String> roleNames = new HashSet<>();
            for (Role role : userDetails.getRoles()) {
                roleNames.add(role.getName());
            }
            Set<Role> managedRoles = roleService.getRolesByNames(roleNames);
            existingUser.setRoles(managedRoles);
        }

        return userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
