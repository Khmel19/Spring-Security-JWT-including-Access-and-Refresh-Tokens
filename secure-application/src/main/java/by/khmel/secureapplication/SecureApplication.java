package by.khmel.secureapplication;

import by.khmel.secureapplication.domain.Role;
import by.khmel.secureapplication.domain.User;
import by.khmel.secureapplication.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class SecureApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CommandLineRunner run(UserService userService) {
        return args -> {
            userService.saveRole(new Role(null, "ROLE_USER"));
            userService.saveRole(new Role(null, "ROLE_MANAGER"));
            userService.saveRole(new Role(null, "ROLE_ADMIN"));
            userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

            userService.saveUser(new User(null, "John Travolta", "john", "123", new ArrayList<>()));
            userService.saveUser(new User(null, "Bruce Willis", "bru", "123", new ArrayList<>()));
            userService.saveUser(new User(null, "Will Smith", "willy", "123", new ArrayList<>()));
            userService.saveUser(new User(null, "Jim Carry", "legend", "123", new ArrayList<>()));

            userService.addRoleToUser("john", "ROLE_USER");
            userService.addRoleToUser("bru", "ROLE_MANAGER");
            userService.addRoleToUser("john", "ROLE_MANAGER");
            userService.addRoleToUser("willy", "ROLE_USER");
            userService.addRoleToUser("legend", "ROLE_USER");
            userService.addRoleToUser("legend", "ROLE_ADMIN");
            userService.addRoleToUser("legend", "ROLE_SUPER_ADMIN");
        };
    }
}
