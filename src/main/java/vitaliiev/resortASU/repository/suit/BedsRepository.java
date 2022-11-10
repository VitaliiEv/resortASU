package vitaliiev.resortASU.repository.suit;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.reserve.PaymentStatus;

public interface BedsRepository extends JpaRepository<PaymentStatus, Integer> {

}
