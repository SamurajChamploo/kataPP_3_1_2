package org.example.controller;

import org.example.model.Role;
import org.example.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.example.service.RoleService;
import org.example.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping
    public String showAdminPanel(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allUsers", userService.findAllUsers());
        model.addAttribute("allRoles", roleService.findAllRoles());
        model.addAttribute("newUser", new User());
        return "admin_panel";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute("user") User user,
                             @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles) {

        setRolesIfNotEmpty(user, selectedRoles);

        userService.saveUser(user);
        return "redirect:/admin";
    }

    private void setRolesIfNotEmpty(@ModelAttribute("user") User user, @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles) {
        if (selectedRoles != null && !selectedRoles.isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : selectedRoles) {
                Role role = roleService.findRoleByName(roleName);
                roles.add(role);
            }
            user.setRoles(roles);
        }
    }

    @GetMapping("/users/{id}")
    @ResponseBody
    public User getUserById(@PathVariable Long id) {
        return userService.findUserById(id);
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute("user") User user,
                             @RequestParam(value = "selectedRoles", required = false) List<String> selectedRoles) {

        setRolesIfNotEmpty(user, selectedRoles);

        userService.updateUser(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin";
    }
}
