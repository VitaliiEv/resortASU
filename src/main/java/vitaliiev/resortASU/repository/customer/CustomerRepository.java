package vitaliiev.resortASU.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.customer.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
