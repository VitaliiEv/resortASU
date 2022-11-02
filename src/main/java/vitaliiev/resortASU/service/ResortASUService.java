package vitaliiev.resortASU.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.repository.JpaRepository;
import vitaliiev.resortASU.model.AbstractResortASUEntity;

import java.util.List;

public interface ResortASUService<R extends JpaRepository<T extends AbstractResortASUEntity<ID>, ID extends Number>> {
//
    T create(T entity) throws DataIntegrityViolationException;

    T findById(ID id);

    List<T> find(T entity);

    List<T> findAll();

    T update(T entity);

    void delete(ID id);

    ExampleMatcher getSearchConditions();
}
