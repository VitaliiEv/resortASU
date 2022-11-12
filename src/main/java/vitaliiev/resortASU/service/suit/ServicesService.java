package vitaliiev.resortASU.service.suit;

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
import vitaliiev.resortASU.model.suit.Services;
import vitaliiev.resortASU.repository.suit.ServicesRepository;

import java.util.List;

@Slf4j
@Service
public class ServicesService {


    protected static final String ENTITY_NAME = Services.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("service", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("currentprice", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("minimumprice", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("service", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("service", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("service", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("deleted", ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnorePaths("id", "lastchanged", "suitTypes", "reserveServices");

    private final ServicesRepository repository;

    @Autowired
    public ServicesService(ServicesRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Services create(Services entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Services findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<Services> find(Services entity) {
        Example<Services> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Services> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Services> findAllPresent() {
        Services entity = new Services();
        entity.setDeleted(false);
        Example<Services> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Services update(Services entity) {

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
        Services entity = this.findById(id);
        entity.setDeleted(true);
        try {
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }

}
