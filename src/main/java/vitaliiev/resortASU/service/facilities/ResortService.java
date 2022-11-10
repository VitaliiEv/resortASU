package vitaliiev.resortASU.service.facilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.facilities.Resort;
import vitaliiev.resortASU.repository.facilities.ResortRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class ResortService {


    protected static final String ENTITY_NAME = Resort.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("deleted", ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnorePaths("id", "resorts", "lastChanged");

    private final ResortRepository repository;

    @Autowired
    public ResortService(ResortRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Resort create(Resort entity) throws DataIntegrityViolationException {
        entity.setLastChanged(Timestamp.from(Instant.now()));
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Resort findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<Resort> find(Resort entity) {
        Example<Resort> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Resort> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Resort> findAllPresent() {
        Resort entity = new Resort();
        entity.setDeleted(false);
        Example<Resort> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Resort update(Resort entity) {
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return entity;
        }
    }

    @Caching(
            evict = {@CacheEvict(cacheNames = CACHE_NAME, key = "#id"),
                    @CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public void delete(Integer id) {
        Resort entity = this.findById(id);
        entity.setDeleted(true);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }

}
