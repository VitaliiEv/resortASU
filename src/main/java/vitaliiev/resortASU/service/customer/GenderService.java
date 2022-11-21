package vitaliiev.resortASU.service.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.customer.Gender;
import vitaliiev.resortASU.repository.customer.GenderRepository;

import java.util.List;

@Slf4j
@Service
public class GenderService {


    protected static final String ENTITY_NAME = Gender.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("gender", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id");

    private final GenderRepository repository;

    @Autowired
    public GenderService(GenderRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Gender findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<Gender> find(Gender entity) {
        Example<Gender> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Gender> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Gender> findAllPresent() {
        Gender entity = new Gender();
        Example<Gender> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

}
