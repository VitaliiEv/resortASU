package vitaliiev.resortASU.repository.facilities;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.facilities.Resort;

public interface ResortRepository extends JpaRepository<Resort, Integer> {
}
