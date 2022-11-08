package vitaliiev.resortASU.repository.facilities;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.facilities.Building;

public interface BuildingRepository extends JpaRepository<Building, Integer> {
}
