package vitaliiev.resortASU.service.reserve;

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
import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.repository.reserve.ReserveRepository;

import java.util.List;

@Slf4j
@Service
public class ReserveService {


    protected static final String ENTITY_NAME = Reserve.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("number", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("checkin", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("checkout", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymentstatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymenttype", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("reserveStatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "created", "lastchanged", "customers", "reserveReserve");

    private final ReserveRepository repository;

    @Autowired
    public ReserveService(ReserveRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve create(Reserve entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Reserve findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Reserve> find(Reserve entity) {
        Example<Reserve> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Reserve> findAll() {
        return repository.findAll(Sort.by("id"));
    }


    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve update(Reserve entity) {

        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return entity;
        }
    }

//    @Caching(
//            evict = {@CacheEvict(cacheNames = CACHE_NAME, key = "#id"),
//                    @CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
//    )
//    public void delete(Long id) {
//        Reserve entity = this.findById(id);
//        entity.setDeleted(true);
//        try {
//            repository.save(entity);
//        } catch (DataIntegrityViolationException e) {
//            log.warn(e.getMessage());
//        }
//
//    }

}
