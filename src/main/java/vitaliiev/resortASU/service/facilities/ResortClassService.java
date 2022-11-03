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
import vitaliiev.resortASU.model.facilities.ResortClass;
import vitaliiev.resortASU.repository.facilities.ResortClassRepository;

import java.util.List;

@Slf4j
@Service
public class ResortClassService {
    private static final String ENTITY_NAME = ResortClass.ENTITY_NAME;
    private static final String CACHE_NAME = ENTITY_NAME;
    private static final String CACHE_LIST_NAME = ENTITY_NAME + "List";    
    private static final String DEFAULT_VALUE = "no class";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("resortClass", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withIgnorePaths("id", "resorts");

    private final ResortClassRepository repository;

    @Autowired
    public ResortClassService(ResortClassRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public ResortClass create(ResortClass entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public ResortClass findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<ResortClass> find(ResortClass entity) {
        Example<ResortClass> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<ResortClass> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public ResortClass update(ResortClass entity) {
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
        ResortClass entity = this.findById(id); // for maximum cache use
        try {
//            entity.getResorts().forEach(r -> r.setResortClass(findRoleByClass(DEFAULT_CLASS))); //todo implement photo
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }
}
