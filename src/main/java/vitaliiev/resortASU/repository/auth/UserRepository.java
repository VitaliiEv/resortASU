package vitaliiev.resortASU.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.entity.auth.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findUsersById(Long id);
    User findByUsername(String username);

}
