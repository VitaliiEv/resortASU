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
import vitaliiev.resortASU.model.facilities.ResortType;
import vitaliiev.resortASU.repository.ResortASURepository;
import vitaliiev.resortASU.service.ResortASUService;

import java.util.List;

@Slf4j
@Service
public class ResortTypeService implements ResortASUService<ResortType, Integer> {


    protected static final String ENTITY_NAME = ResortType.ENTITY_NAME; // override  this
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching() // fixme
            .withIncludeNullValues()
            .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withIgnorePaths("id", "resorts");

    private final ResortASURepository<ResortType, Integer> repository;

    @Autowired
    public ResortTypeService(ResortASURepository<ResortType, Integer> repository) {
        this.repository = repository;
    }

    @Override
    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public ResortType create(ResortType entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Override
    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public ResortType findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ResortType> find(ResortType entity) {
        Example<ResortType> example = Example.of(entity, getSearchConditions());
        return repository.findAll(example, Sort.by("id"));
    }

    @Override
    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<ResortType> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Override
    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public ResortType update(ResortType entity) {
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
    public void delete(Integer id) {
        try {
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }

    @Override
    public ExampleMatcher getSearchConditions() {
        return SEARCH_CONDITIONS_MATCH_ALL;
    }
}
