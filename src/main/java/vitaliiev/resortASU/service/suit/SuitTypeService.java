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
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.repository.suit.SuitTypeRepository;

import java.util.List;

@Slf4j
@Service
public class SuitTypeService {

    protected static final String ENTITY_NAME = SuitType.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues()
            .withMatcher("suitClass", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("beds", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("area", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("currentprice", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("minimumprice", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("mainphoto", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("deleted", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "lastchanged", "features", "services");

    private final SuitTypeRepository repository;

    @Autowired
    public SuitTypeService(SuitTypeRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public SuitType create(SuitType entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public SuitType findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<SuitType> find(SuitType entity) {
        Example<SuitType> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<SuitType> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<SuitType> findAllPresent() {
        SuitType entity = new SuitType();
        entity.setDeleted(false);
        Example<SuitType> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public SuitType update(SuitType entity) {

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
//        SuitType entity = this.findById(id);
//        entity.setDeleted(true);
        try {
//            repository.save(entity);
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }
    }
}
