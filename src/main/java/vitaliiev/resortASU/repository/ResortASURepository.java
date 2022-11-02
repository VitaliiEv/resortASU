package vitaliiev.resortASU.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.AbstractResortASUEntity;

public interface ResortASURepository<T extends AbstractResortASUEntity<ID>, ID extends Number> extends JpaRepository<T, ID> {

}
