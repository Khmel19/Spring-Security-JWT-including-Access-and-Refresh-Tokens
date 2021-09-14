package by.khmel.secureapplication.service;

import by.khmel.secureapplication.domain.Role;
import by.khmel.secureapplication.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String roleName);
    User getUser(String username);
    List<User> getUsers();
}
