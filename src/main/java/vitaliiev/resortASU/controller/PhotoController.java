package vitaliiev.resortASU.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.Photo;
import vitaliiev.resortASU.service.PhotoService;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class PhotoController {
    private static final String FRAGMENT_NAME = "photos";
    private static final String SECTION_NAME = "";
    private static final String REQUEST_MAPPING = "/" + FRAGMENT_NAME;
    private final PhotoService service;

    private final URI storage;

    @Autowired
    public PhotoController(PhotoService service) {
        this.service = service;
        this.storage = service.getStoragePath().toUri();
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Photo> entities = service.findAll();
            model.addAttribute("entities", entities);
        }
        Photo newEntity = new Photo();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        model.addAttribute("photoPath", this.storage.toString());
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") Photo entity,
                       RedirectAttributes redirectAttributes) {
        List<Photo> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@RequestParam(name = "image") MultipartFile[] entities,
                         RedirectAttributes redirectAttributes) {
        List<String> messages = new ArrayList<>();
        for (MultipartFile entity : entities) {
            try {
                service.create(entity);
            } catch (IOException e) {
                String message = e.getMessage();
                log.warn(message);
                messages.add(message);
            } catch (DataIntegrityViolationException e) {
                String message = "Cannot add file to repository: " + entity.getOriginalFilename();
                log.warn(message + ". " + e.getMessage());
                messages.add(message);
            }
        }
        if (!messages.isEmpty()) {
            redirectAttributes.addFlashAttribute("creationErrors", messages);
        }
        return "redirect:" + REQUEST_MAPPING;
    }

    @GetMapping(REQUEST_MAPPING + "/{id}")
    public String read(@PathVariable Long id, Model model) {
        Photo entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
        }
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") Photo entity) {
        service.update(entity);
        return "redirect:" + REQUEST_MAPPING + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        try {
            service.delete(id);
        } catch (IOException e) {
            log.warn("Cannot delete file from filesystem. " + e.getMessage());
        } catch (DataIntegrityViolationException e) {
            log.warn("Cannot delete file from repository. "  + e.getMessage());
        }
        return "redirect:" + REQUEST_MAPPING;
    }
}
