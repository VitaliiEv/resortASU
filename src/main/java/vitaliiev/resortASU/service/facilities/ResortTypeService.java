package vitaliiev.resortASU.service.facilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.model.facilities.ResortType;
import vitaliiev.resortASU.repository.ResortASURepository;
import vitaliiev.resortASU.service.AbstractResortASUService;

@Slf4j
@Service
public class ResortTypeService extends AbstractResortASUService<ResortType, Integer> {

    public ResortTypeService(ResortASURepository<ResortType, Integer> repository) {
        super(repository);
    }

    @Override
    public ExampleMatcher getSearchConditions() {
        return ExampleMatcher
                .matching()
                .withIncludeNullValues()
                .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase())
                .withIgnorePaths("id", "resorts");
    }
}
