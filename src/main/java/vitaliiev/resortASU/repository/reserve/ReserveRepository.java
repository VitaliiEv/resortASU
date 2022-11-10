package vitaliiev.resortASU.repository.reserve;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.reserve.Reserve;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {

}
