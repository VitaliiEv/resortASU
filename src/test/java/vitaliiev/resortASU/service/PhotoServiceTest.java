package vitaliiev.resortASU.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.mock.web.MockMultipartFile;
import vitaliiev.resortASU.ResortApplicationTests;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.repository.PhotoRepository;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PhotoServiceTest extends ResortApplicationTests {

    private static final String ENTITY_NAME = Photo.ENTITY_NAME;
    private static final String CACHE_NAME = ENTITY_NAME;
    private static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    MockMultipartFile imageFile;
    MockMultipartFile[] imageFiles;
    Photo photo;

    @Autowired
    private PhotoRepository repository;
    @Autowired
    PhotoService photoService;
    @Autowired
    UploadService uploadService;
    @Autowired
    CacheManager cacheManager;

    @BeforeEach
    void setUp() throws IOException {
        this.imageFile = new MockMultipartFile("data", "image.svg", "image/svg", "some xml".getBytes());
        this.imageFiles = new MockMultipartFile[] {imageFile};
        this.photo = new Photo();
        this.photo.setFilename("image.svg");
        this.photo.setFiletype(".svg");
        this.photo.setCreated(Timestamp.from(Instant.now()));
        this.photo.setHash("image.");
    }

    @AfterEach
    void tearDown() {
        this.imageFile = null;
        this.imageFiles = null;
        this.photo = null;
    }

    @Test
    void create() {
        Photo photo = photoService.create(this.photo);
        Cache cacheIds = cacheManager.getCache(CACHE_NAME);
        assertNotNull(repository.findById(photo.getId()).orElse(null));
        assertNotNull(cacheIds.get(photo.getId()));
        repository.deleteById(photo.getId());
    }

    @Test
    void testCreateFromMultipartFile() throws IOException {
        Cache cacheIds = cacheManager.getCache(CACHE_NAME);
        Photo photo = photoService.create(this.imageFile);
        assertNotNull(repository.findById(photo.getId()).orElse(null));
        assertNotNull(cacheIds.get(photo.getId()));
        photoService.delete(photo.getId());
    }

    @Test
    void testCreateFromMultipartFiles() {
    }

    @Test
    void findById() {
    }

    @Test
    void find() {
    }

    @Test
    void findAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void getStoragePath() {
    }
}