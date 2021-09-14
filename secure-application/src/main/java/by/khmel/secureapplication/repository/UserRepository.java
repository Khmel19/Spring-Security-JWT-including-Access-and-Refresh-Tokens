package by.khmel.secureapplication.repository;

import by.khmel.secureapplication.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findAppUserByUsername(String username);
}
