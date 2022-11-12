package vitaliiev.resortASU.repository.reserve;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.reserve.ReserveStatus;

public interface ReserveStatusRepository extends JpaRepository<ReserveStatus, Integer> {

}
