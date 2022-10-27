package vitaliiev.resortASU.repository.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.entity.auth.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(String name);
}
