package vitaliiev.resortASU.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class UploadService {

    private final Pattern filetypePattern;
    @Getter
    private final Path storage;
    private final String[] filetypes;

    public UploadService(Path storage, String[] filetypes) {
        this.storage = storage;
        this.filetypes = filetypes;
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < filetypes.length; i++) {
            pattern.append(filetypes[i]);
            if (i == filetypes.length - 1) {
                break;
            }
            pattern.append("|");
        }
        this.filetypePattern = Pattern.compile("\\.(?i)(" + pattern + ")$");
    }

    public void store(MultipartFile file, String newFilename) throws IOException, IllegalArgumentException {
        Objects.requireNonNull(file, newFilename);
        if (this.filetypePattern != null && filetypes != null) {
            Matcher matcher = this.filetypePattern.matcher(newFilename);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Filetype not accepted. Got: " + newFilename + ". Accepted: " + Arrays.toString(filetypes));
            }
        }
        Path path = this.storage.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
        if (isOutsideOfStorage(path)) {
            throw new IOException("Trying to store file outside of storage directory: " + newFilename);
        }
        if (Files.exists(path)) {
            throw new IOException("File exists: " + newFilename);
        }
        file.transferTo(path);
    }

    public void store(MultipartFile file) throws IOException {
        store(file, file.getOriginalFilename());
    }

    public Path load(String filename) throws IOException {
        Path path = this.storage.resolve(filename);
        if (isOutsideOfStorage(path)) {
            throw new IOException("Trying to load file from outside of storage directory: " + filename);
        }
        if (!Files.exists(path)) {
            throw new IOException("File doesn't exist: " + filename);
        }
        return path;
    }

    public Resource loadAsResource(String filename) throws IOException {
        try {
            Path path = load(filename);
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new IOException("Could not access resource: " + resource);
            }
        } catch (MalformedURLException e) {
            throw new IOException("Could not read file: " + filename, e);
        }
    }

    void delete(String filename) throws IOException {
        Path path = this.storage.resolve(filename);
        Files.delete(path);
    }

    private boolean isOutsideOfStorage(Path path) {
        return path.normalize().toAbsolutePath().getParent().equals(this.storage);
    }

}
