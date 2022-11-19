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
import vitaliiev.resortASU.model.reserve.PaymentType;
import vitaliiev.resortASU.repository.reserve.PaymentTypeRepository;

import java.util.List;

@Slf4j
@Service
public class PaymentTypeService {


    protected static final String ENTITY_NAME = PaymentType.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("enabled", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymenttype", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "reserves");

    private final PaymentTypeRepository repository;

    @Autowired
    public PaymentTypeService(PaymentTypeRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public PaymentType findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<PaymentType> find(PaymentType entity) {
        Example<PaymentType> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<PaymentType> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public PaymentType update(PaymentType entity) {
        try {
            PaymentType oldEntity = this.findById(entity.getId());
            entity.setId(oldEntity.getId());

            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return entity;
        }
    }
}
