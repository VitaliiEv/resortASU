package vitaliiev.resortASU.service.reserve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.reserve.ReserveStatus;
import vitaliiev.resortASU.repository.reserve.ReserveStatusRepository;

import java.util.List;

@Slf4j
@Service
public class ReserveStatusService {


    protected static final String ENTITY_NAME = ReserveStatus.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("status", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "reserves");

    private final ReserveStatusRepository repository;

    @Autowired
    public ReserveStatusService(ReserveStatusRepository repository) {
        this.repository = repository;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public ReserveStatus findById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public List<ReserveStatus> find(ReserveStatus entity) {
        Example<ReserveStatus> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<ReserveStatus> findAll() {
        return repository.findAll(Sort.by("id"));
    }

}
