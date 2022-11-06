package vitaliiev.resortASU.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import vitaliiev.resortASU.service.UploadService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;

@Configuration
public class SpringConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setFallbackToSystemLocale(false);
        messageSource.setBasename("messages/Messages");
        return messageSource;
    }

    @Bean
    public UploadService photoUploadService() throws IOException {
        String[] acceptedFiletypes = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
        String relativePathString = "photos/";
        Path relativePath = Path.of(relativePathString);
        return new UploadService(relativePath, acceptedFiletypes);
    }
}
