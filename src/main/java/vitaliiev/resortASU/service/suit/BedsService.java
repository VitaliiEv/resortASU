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
import vitaliiev.resortASU.ResortASUGeneralException;
import vitaliiev.resortASU.model.suit.Beds;
import vitaliiev.resortASU.repository.suit.BedsRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BedsService {


    protected static final String ENTITY_NAME = Beds.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("beds", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
            .withMatcher("beds_adult", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("beds_child", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "suitTypes");

    private final BedsRepository repository;

    @Autowired
    public BedsService(BedsRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Beds create(Beds entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Beds findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<Beds> find(Beds entity) {
        Example<Beds> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Beds> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    public Set<Beds> findDistinctByBedsQuantity(Collection<Beds> beds) {
        if (beds.isEmpty()) {
            throw new ResortASUGeneralException("Got empty list.");
        }
        return beds.stream()
                .map(b -> {
                    Beds newBed = new Beds();
                    newBed.setBeds_adult(b.getBeds_adult());
                    newBed.setBeds_child(b.getBeds_child());
                    return newBed;
                })
                .collect(Collectors.toSet());
    }

    public List<Beds> getSortedBeds(Collection<Beds> beds, Comparator<Beds> comparator) {
        if (beds.isEmpty()) {
            throw new ResortASUGeneralException("Got empty list.");
        }
        return beds.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Beds update(Beds entity) {
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
            Beds entity = this.findById(id);
            if (!entity.getSuitTypes().isEmpty()) {
                String message =
                        "Can't delete row because it is used in suit types:" + entity.getSuitTypes().toString();
                log.warn(message);
                throw new DataIntegrityViolationException(message);
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }
    }
}
