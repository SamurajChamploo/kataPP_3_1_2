package org.example.config;

import jakarta.annotation.PostConstruct;
import org.example.model.Role;
import org.example.model.User;
import org.example.repository.RoleRepository;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    @Transactional
    public void init() {

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ROLE_ADMIN");
                    return roleRepository.save(role);
                });

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role("ROLE_USER");
                    return roleRepository.save(role);
                });

        if (userRepository.findByEmail("admin@mail.ru").isEmpty()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("Adminov");
            admin.setAge(30);
            admin.setEmail("admin@mail.ru");

            String encodedPassword = passwordEncoder.encode("password");
            System.out.println("Admin password hash: " + encodedPassword);
            admin.setPassword(encodedPassword);

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(userRole);
            admin.setRoles(adminRoles);

            userRepository.save(admin);
        } else {
            User existingAdmin = userRepository.findByEmail("admin@mail.ru").get();

            boolean passwordCorrect = passwordEncoder.matches("password", existingAdmin.getPassword());

            if (!passwordCorrect) {
                existingAdmin.setPassword(passwordEncoder.encode("password"));
                userRepository.save(existingAdmin);
            }
        }

        if (userRepository.findByEmail("user@mail.ru").isEmpty()) {
            User user = new User();
            user.setFirstName("User");
            user.setLastName("Userov");
            user.setAge(25);
            user.setEmail("user@mail.ru");
            user.setPassword(passwordEncoder.encode("password"));

            Set<Role> userRoles = new HashSet<>();
            userRoles.add(userRole);
            user.setRoles(userRoles);

            userRepository.save(user);
        }
    }
}
