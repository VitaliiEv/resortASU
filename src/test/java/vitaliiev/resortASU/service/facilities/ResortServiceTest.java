package vitaliiev.resortASU.service.facilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import vitaliiev.resortASU.ResortApplicationTests;
import vitaliiev.resortASU.model.facilities.Resort;
import vitaliiev.resortASU.model.facilities.ResortClass;
import vitaliiev.resortASU.model.facilities.ResortType;
import vitaliiev.resortASU.repository.facilities.ResortRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResortServiceTest extends ResortApplicationTests  {

    protected static final String ENTITY_NAME = Resort.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";
    Resort entity;

    @Autowired
    private ResortRepository repository;
    @Autowired
    ResortService service;
    @Autowired
    ResortClassService resortClassService;
    @Autowired
    ResortTypeService resortTypeService;
    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        this.entity = new Resort();
        this.entity.setName("name");
        this.entity.setDescription("description");
        ResortClass resortClass = resortClassService.findAll().get(1);
        this.entity.setResortClass(resortClass);
        ResortType resortType = resortTypeService.findAll().get(1);
        this.entity.setResorttype(resortType);
    }

    @AfterEach
    void tearDown() {
        this.entity = null;
    }

    @Test
    void create() {
        Resort entity = service.create(this.entity);
        Cache cacheIds = cacheManager.getCache(CACHE_NAME);
        assertNotNull(repository.findById(entity.getId()).orElse(null));
        assertNotNull(cacheIds.get(entity.getId()));
        repository.deleteById(entity.getId());
    }
}