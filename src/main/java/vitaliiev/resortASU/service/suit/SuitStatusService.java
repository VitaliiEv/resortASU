package vitaliiev.resortASU.service.suit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.suit.SuitStatus;
import vitaliiev.resortASU.repository.suit.SuitStatusRepository;

import java.util.List;

@Slf4j
@Service
public class SuitStatusService {


    protected static final String ENTITY_NAME = SuitStatus.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "suits");

    private final SuitStatusRepository repository;

    @Autowired
    public SuitStatusService(SuitStatusRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public SuitStatus findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<SuitStatus> find(SuitStatus entity) {
        Example<SuitStatus> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<SuitStatus> findAll() {
        return repository.findAll(Sort.by("id"));
    }

}
