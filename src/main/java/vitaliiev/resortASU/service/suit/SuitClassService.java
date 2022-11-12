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
import vitaliiev.resortASU.model.suit.SuitClass;
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.repository.suit.SuitClassRepository;

import java.util.List;

@Slf4j
@Service
public class SuitClassService {


    protected static final String ENTITY_NAME = SuitClass.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("suitclass", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "lastchanged", "suitTypes");

    private final SuitClassRepository repository;

    @Autowired
    public SuitClassService(SuitClassRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public SuitClass create(SuitClass entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public SuitClass findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<SuitClass> find(SuitClass entity) {
        Example<SuitClass> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<SuitClass> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public SuitClass update(SuitClass entity) {

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
        try {
            SuitClass entity = this.findById(id);
            for (SuitType suitType : entity.getSuitTypes()) {
                suitType.setSuitClass(null);
            }
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }
}
