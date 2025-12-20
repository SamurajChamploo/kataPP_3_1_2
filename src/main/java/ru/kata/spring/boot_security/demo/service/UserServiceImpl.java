package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void saveUser(User user, Set<Long> roleIds) {
        Set<Role> roles = getRolesFromIds(roleIds);
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(Long id, String firstName, String lastName,
                           Integer age, String email, String password,
                           Set<Long> roleIds) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (!existingUser.getEmail().equals(email)) {
            boolean emailExists = userRepository.findByEmail(email).isPresent();
            if (emailExists) {
                throw new RuntimeException("Email already exists: " + email);
            }
        }

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setAge(age);
        existingUser.setEmail(email);

        if (password != null && !password.trim().isEmpty()) {
            if (!password.startsWith("$2a$")) {
                existingUser.setPassword(passwordEncoder.encode(password));
            } else if (!password.equals(existingUser.getPassword())) {
                existingUser.setPassword(password);
            }
        }

        Set<Role> roles = getRolesFromIds(roleIds);
        existingUser.setRoles(roles);

        userRepository.save(existingUser);
    }

    @Override
    @Transactional
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private Set<Role> getRolesFromIds(Set<Long> roleIds) {
        Set<Role> roles = new HashSet<>();

        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                roleRepository.findById(roleId)
                        .ifPresent(roles::add);
            }
        }

        if (roles.isEmpty()) {
            roleRepository.findByName("ROLE_USER")
                    .ifPresent(roles::add);
        }

        return roles;
    }
}