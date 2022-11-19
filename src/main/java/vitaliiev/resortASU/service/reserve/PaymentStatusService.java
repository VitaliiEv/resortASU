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
import vitaliiev.resortASU.model.reserve.PaymentStatus;
import vitaliiev.resortASU.repository.reserve.PaymentStatusRepository;

import java.util.List;

@Slf4j
@Service
public class PaymentStatusService {


    protected static final String ENTITY_NAME = PaymentStatus.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymentstatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "reserves");

    private final PaymentStatusRepository repository;

    @Autowired
    public PaymentStatusService(PaymentStatusRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public PaymentStatus findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<PaymentStatus> find(PaymentStatus entity) {
        Example<PaymentStatus> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<PaymentStatus> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public PaymentStatus update(PaymentStatus entity) {
        try {
            PaymentStatus oldEntity = this.findById(entity.getId());
            entity.setId(oldEntity.getId());

            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return entity;
        }
    }
}
