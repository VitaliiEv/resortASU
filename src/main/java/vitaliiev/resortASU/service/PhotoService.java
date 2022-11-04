package vitaliiev.resortASU.service;

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
import org.springframework.web.multipart.MultipartFile;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.repository.PhotoRepository;
import vitaliiev.resortASU.utils.CollectionElementFieldMatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class PhotoService {
    private static final String ENTITY_NAME = Photo.ENTITY_NAME;
    private static final String CACHE_NAME = ENTITY_NAME;
    private static final String CACHE_LIST_NAME = ENTITY_NAME + "List";
//    private static final String DEFAULT_VALUE = "no class";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues() //todo
            .withIgnorePaths("id", "resorts"); // todo add suits, etc

    private final PhotoRepository repository;

    @Autowired
    public PhotoService(PhotoRepository repository) {
        this.repository = repository;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Photo create(Photo entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    public List<Photo> multipartFileToPhotoList(MultipartFile[] entities) {
        return Arrays.stream(entities)
                .map(entity -> {
                    try {
                        return multipartFileToPhoto(entity);
                    } catch (IOException | NoSuchAlgorithmException | SQLException e) {
                        log.warn("Cannot map MultiPartFile to Photo: " + e.getMessage());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .filter(CollectionElementFieldMatcher.distinctByKey(Photo::getHash))
                .toList();
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Photo findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Photo> find(Photo entity) {
        Example<Photo> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Photo> findAll() {
        return repository.findAll(Sort.by("id"));
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Photo update(Photo entity) {
        try {
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
//        Photo entity = this.findById(id); // for maximum cache use
        try {
            // todo, delete from suit, etc
//            entity.getResorts().forEach(r -> r.setPhoto(findRoleByClass(DEFAULT_CLASS))); //todo implement photo
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
        }

    }

    private Photo multipartFileToPhoto(MultipartFile multipartFile) throws IOException, NoSuchAlgorithmException,
            SQLException {
        if (multipartFile.getOriginalFilename() == null && !multipartFile.isEmpty()) {
            throw new IOException("Filename not set, skipping.");
        }
//        multipartFile.getContentType(); // todo filter mimetype

        Photo photo = new Photo();
        byte[] image = multipartFile.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
        String hash = Arrays.toString(md.digest(image));
        String filename = Path.of(multipartFile.getOriginalFilename())
                .getFileName()
                .toString();

        photo.setImage(image);
        photo.setHash(hash);
        photo.setCreated(Timestamp.from(Instant.now()));
        photo.setFilename(filename);
        return photo;
    }
}
