package vitaliiev.resortASU.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.customer.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Integer> {

}
