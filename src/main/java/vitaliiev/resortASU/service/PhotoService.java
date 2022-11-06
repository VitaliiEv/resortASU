package vitaliiev.resortASU.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.repository.PhotoRepository;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class PhotoService {
    private static final String ENTITY_NAME = Photo.ENTITY_NAME;
    private static final String CACHE_NAME = ENTITY_NAME;
    private static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues() //todo
            .withIgnorePaths("id", "resorts"); // todo add suits, etc

    private final PhotoRepository repository;

    private final UploadService uploadService;

    @Autowired
    public PhotoService(PhotoRepository repository, UploadService uploadService) {
        this.repository = repository;
        this.uploadService = uploadService;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Photo create(Photo entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Photo create(MultipartFile entity) throws IOException, DataIntegrityViolationException, IllegalArgumentException {
        Photo photoEntity = multipartFileToPhoto(entity);
        String filename = photoEntity.getHash() + photoEntity.getFiletype();
        Photo savedPhoto = this.create(photoEntity);
        try {
            uploadService.store(entity, filename);
        } catch (IOException e) {
            throw new IOException(e);
        } catch (IllegalArgumentException iae) {
            throw new IllegalArgumentException(iae);
        } finally { //revert changes
            repository.deleteById(photoEntity.getId());
        }
        return savedPhoto;
    }


    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Photo findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Resource download(Long id) throws IOException {
        Photo photo = this.findById(id);
        String filename = photo.getHash() + photo.getFiletype();
        return this.uploadService.loadAsResource(filename);
    }

    public Resource download(String filename) throws IOException {
        return this.uploadService.loadAsResource(filename);
    }

    public List<Photo> find(Photo entity) {
        Example<Photo> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
        // todo
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
    public void delete(Long id) throws IOException, DataIntegrityViolationException {
        Photo entity = this.findById(id);
        uploadService.delete(entity.getHash() + entity.getFiletype());
        // todo, delete from suit, etc
//            entity.getResorts().forEach(r -> r.setPhoto(findRoleByClass(DEFAULT_CLASS))); //todo implement photo
        repository.deleteById(id);
    }

    private Photo multipartFileToPhoto(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new IOException("Filename not set, skipping.");
        }

        Photo photo = new Photo();

        String filename = Path.of(originalFilename)
                .getFileName()
                .toString();
        photo.setFilename(filename);

        String[] filenameParts = filename.split("\\.");
        String filetype = "." + filenameParts[filenameParts.length - 1];
        photo.setFiletype(filetype);

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            String hash = DatatypeConverter.printHexBinary(md.digest(multipartFile.getBytes()));
            photo.setHash(hash);
        } catch (NoSuchAlgorithmException e) {
            log.warn(e.getMessage());
        }

        photo.setCreated(Timestamp.from(Instant.now()));
        return photo;
    }

    public Path getStoragePath() {
        return this.uploadService.getStorage();
    }
}
