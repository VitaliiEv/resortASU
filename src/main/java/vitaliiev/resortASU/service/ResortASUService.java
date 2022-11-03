package vitaliiev.resortASU.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.ExampleMatcher;
import vitaliiev.resortASU.model.AbstractResortASUEntity;

import java.util.List;

public interface ResortASUService<T extends AbstractResortASUEntity, ID> {

    String ENTITY_NAME = AbstractResortASUEntity.ENTITY_NAME; // override  this
    String CACHE_NAME = ENTITY_NAME;
    String CACHE_LIST_NAME = ENTITY_NAME + "List";

    T create(T entity) throws DataIntegrityViolationException;

    T findById(ID id);

    List<T> find(T entity);

    List<T> findAll();

    T update(T entity);

    void delete(ID id);

    ExampleMatcher getSearchConditions();
}
