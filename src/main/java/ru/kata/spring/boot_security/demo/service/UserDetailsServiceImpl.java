package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Looking for user with: '" + username + "'");

        Optional<User> userOpt = userRepository.findByEmail(username);

        if (userOpt.isEmpty() && !username.contains("@")) {
            String[] domains = {"@mail.ru", "@gmail.com", "@yandex.ru"};
            for (String domain : domains) {
                userOpt = userRepository.findByEmail(username + domain);
                if (userOpt.isPresent()) {
                    break;
                }
            }
        }

        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsernameContaining(username);
        }

        User user = userOpt.orElseThrow(() -> {
            System.err.println("Failed to find user with any variation of: '" + username + "'");
            List<User> allUsers = userRepository.findAll();
            System.err.println("Available users: " +
                    allUsers.stream().map(User::getEmail).collect(Collectors.joining(", ")));
            return new UsernameNotFoundException("User '" + username + "' not found");
        });

        System.out.println("Found user: " + user.getEmail());
        return user;
    }
}