package vitaliiev.resortASU.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.customer.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {

}
