package vitaliiev.resortASU.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.AbstractResortASUEntity;
import vitaliiev.resortASU.repository.ResortASURepository;

import java.util.List;

@Slf4j
@Service
public abstract class AbstractResortASUService<T extends AbstractResortASUEntity<ID>, ID extends Number>
        implements ResortASUService<T, ID> {

    protected static final String CLASS_NAME = "AbstractResortASUService"; // override  this
    protected static final String CACHE_NAME = CLASS_NAME;
    protected static final String CACHE_LIST_NAME = CLASS_NAME + "List";

    private final ResortASURepository<T, ID> repository;

    @Autowired
    public AbstractResortASUService(ResortASURepository<T, ID> repository) { // FIXME: 02.11.2022
        this.repository = repository;
    }

    @Override
    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public T create(T entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public T findById(ID id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<T> find(T entity) {
        Example<T> example = Example.of(entity, getSearchConditions());
        return repository.findAll(example, Sort.by("id"));
    }

    @Override
    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<T> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public T update(T entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return entity;
        }
    }

    @Override
    @Caching(
            evict = {@CacheEvict(cacheNames = CACHE_NAME, key = "#id"),
                    @CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public void delete(ID id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }

}
