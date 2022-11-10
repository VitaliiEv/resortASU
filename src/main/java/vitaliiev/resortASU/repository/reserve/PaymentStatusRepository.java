package vitaliiev.resortASU.repository.reserve;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.reserve.PaymentStatus;

public interface PaymentStatusRepository extends JpaRepository<PaymentStatus, Integer> {

}
