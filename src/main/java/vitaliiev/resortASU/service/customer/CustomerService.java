package vitaliiev.resortASU.service.customer;

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
import vitaliiev.resortASU.model.customer.Customer;
import vitaliiev.resortASU.repository.customer.CustomerRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class CustomerService {


    protected static final String ENTITY_NAME = Customer.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues()
            .withMatcher("firstname", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("surname", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("middlename", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("phone", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("email", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("gender", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("phone", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "documents", "reserves");

    private final CustomerRepository repository;

    @Autowired
    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Customer create(Customer entity) throws DataIntegrityViolationException {
        entity.setLastchanged(Timestamp.from(Instant.now()));
        return repository.save(entity);
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Customer findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Customer> find(Customer entity) {
        Example<Customer> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Customer> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Customer> findAllPresent() {
        Customer entity = new Customer();
        Example<Customer> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Customer update(Customer entity) {

        try {
            entity.setLastchanged(Timestamp.from(Instant.now()));
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
    public void delete(Long id) {
        try {
            Customer entity = this.findById(id);
            entity.getReserves().forEach(reserve -> reserve.removeCustomer(entity));
            repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }

}
