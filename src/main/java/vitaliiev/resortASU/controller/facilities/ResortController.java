package vitaliiev.resortASU.controller.facilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.facilities.Resort;
import vitaliiev.resortASU.service.UploadService;
import vitaliiev.resortASU.service.facilities.ResortClassService;
import vitaliiev.resortASU.service.facilities.ResortService;
import vitaliiev.resortASU.service.facilities.ResortTypeService;

import java.util.List;

@Slf4j
@Controller
public class ResortController {
    private static final String FRAGMENT_NAME = "resort";
    private static final String SECTION_NAME = "resort";
    private static final String REQUEST_MAPPING = "/" + SECTION_NAME + "/" + FRAGMENT_NAME;
    private final ResortService service;

    private final ResortTypeService resortTypeService;

    private final ResortClassService resortClassService;
    private final UploadService uploadService;

    @Autowired
    public ResortController(ResortService service, ResortTypeService resortTypeService,
                            ResortClassService resortClassService, UploadService uploadService) {
        this.service = service;
        this.resortTypeService = resortTypeService;
        this.resortClassService = resortClassService;
        this.uploadService = uploadService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Resort> entities = service.findAllPresent();
            model.addAttribute("entities", entities);
        }
        Resort newEntity = new Resort();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        model.addAttribute("resortTypes", this.resortTypeService.findAll());
        model.addAttribute("resortClasses", this.resortClassService.findAll());

        model.addAttribute("pathToStorage", this.uploadService.getStorage().toString());
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") Resort entity,
                       RedirectAttributes redirectAttributes) {
        List<Resort> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") Resort entity, RedirectAttributes redirectAttributes) {
        try {
            service.create(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            redirectAttributes.addFlashAttribute("creationError", e.getMessage());
        }
        return "redirect:" + REQUEST_MAPPING;
    }

    @GetMapping(REQUEST_MAPPING + "/{id}")
    public String read(@PathVariable Integer id, Model model) {
        Resort entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
        }
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") Resort entity) {
        service.update(entity);
        return "redirect:/resort/" + FRAGMENT_NAME + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
