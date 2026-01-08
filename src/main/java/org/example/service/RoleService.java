package org.example.service;

import org.example.model.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {
    Role saveRole(Role role);
    Role findRoleByName(String name);
    List<Role> findAllRoles();
    Set<Role> getRolesByNames(Set<String> roleNames);
}
