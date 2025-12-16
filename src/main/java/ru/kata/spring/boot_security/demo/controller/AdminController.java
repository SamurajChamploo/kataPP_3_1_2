package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String adminPage(@AuthenticationPrincipal User currentUser, Model model) {
        List<User> users = userService.findAll();
        List<Role> roles = userService.findAllRoles();
        model.addAttribute("users", users);
        model.addAttribute("roles", roles);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("newUser", new User());
        return "admin";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("newUser") User user,
                             @RequestParam("roleIds") Set<Long> roleIds) {
        userService.save(user, roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/edit")
    public String updateUser(@RequestParam("id") Long id,
                             @RequestParam("firstName") String firstName,
                             @RequestParam("lastName") String lastName,
                             @RequestParam("age") Integer age,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam("roleIds") Set<Long> roleIds) {
        userService.update(id, new User(firstName, lastName, age, email, password), roleIds);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin";
    }
}