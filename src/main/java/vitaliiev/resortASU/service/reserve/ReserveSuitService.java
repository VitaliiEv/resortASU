package vitaliiev.resortASU.service.reserve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.reserve.ReserveSuit;
import vitaliiev.resortASU.repository.reserve.ReserveSuitRepository;

import java.util.List;

@Slf4j
@Service
public class ReserveSuitService {


    protected static final String ENTITY_NAME = ReserveSuit.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("reserve", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("suit", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("price", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "reserveServices");

    private final ReserveSuitRepository repository;

    @Autowired
    public ReserveSuitService(ReserveSuitRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public ReserveSuit findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<ReserveSuit> find(ReserveSuit entity) {
        Example<ReserveSuit> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<ReserveSuit> findAll() {
        return repository.findAll(Sort.by("id"));
    }

}
