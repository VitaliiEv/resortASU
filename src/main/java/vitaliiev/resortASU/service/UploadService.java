package vitaliiev.resortASU.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Getter
public class UploadService {

    private Pattern filetypePattern;
    private final Path directory;
    private final Path relativePath;
    private final Path basePath;
    private String[] filetypes;

    public UploadService(Path basePath, String[] filetypes) throws IOException {
        this(basePath, basePath.relativize(basePath), filetypes);
    }

    public UploadService(Path basePath) throws IOException {
        this(basePath, basePath.relativize(basePath));
    }

    public UploadService(Path basePath, Path relativePath) throws IOException {
        if (Files.exists(basePath) && Files.isDirectory(basePath)) {
            this.relativePath = relativePath;
            this.basePath = basePath;
            this.directory = basePath.resolve(relativePath);
            if (Files.exists(this.directory)) {
                if (!Files.isDirectory(this.directory)) {
                    throw new IllegalArgumentException("Given path is not directory: " + this.directory);
                }
            } else {
                Files.createDirectory(this.directory);
            }
        } else {
            throw new IllegalArgumentException("Given base path is not directory: " + basePath);
        }

    }

    public UploadService(Path basePath, Path relativePath, String[] filetypes) throws IOException {
        this(basePath, relativePath);
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

    void store(MultipartFile file, String newFilename) throws IOException {
        Objects.requireNonNull(file, newFilename);
        if (this.filetypePattern != null && filetypes != null) {
            Matcher matcher = this.filetypePattern.matcher(newFilename);
            if (!matcher.find()) {
                throw new IllegalArgumentException("Filetype not accepted. Got: " + newFilename + ". Accepted: " + Arrays.toString(filetypes));
            }
        }
        Path path = this.directory.resolve(newFilename);
        if (Files.exists(path)) {
            throw new IOException("File exists: " + newFilename);
        }
        file.transferTo(path);
    }

    void store(MultipartFile file) throws IOException {
        store(file, file.getOriginalFilename());
    }

    void delete(String filename) throws IOException {
        Path path = this.directory.resolve(filename);
        Files.delete(path);
    }

}
