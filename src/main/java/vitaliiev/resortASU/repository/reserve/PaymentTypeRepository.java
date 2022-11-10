package vitaliiev.resortASU.repository.reserve;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.reserve.PaymentType;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {

}
