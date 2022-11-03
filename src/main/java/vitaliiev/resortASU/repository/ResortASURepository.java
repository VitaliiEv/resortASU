package vitaliiev.resortASU.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.AbstractResortASUEntity;

public interface ResortASURepository<T extends AbstractResortASUEntity, ID> extends JpaRepository<T, ID> {

}
