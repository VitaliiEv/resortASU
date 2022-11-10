package vitaliiev.resortASU.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.customer.Gender;

public interface GenderRepository extends JpaRepository<Gender, Integer> {

}
