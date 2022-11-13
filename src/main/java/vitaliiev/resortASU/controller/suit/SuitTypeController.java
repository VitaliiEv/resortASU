package vitaliiev.resortASU.controller.suit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.service.PhotoService;
import vitaliiev.resortASU.service.UploadService;
import vitaliiev.resortASU.service.suit.BedsService;
import vitaliiev.resortASU.service.suit.SuitClassService;
import vitaliiev.resortASU.service.suit.SuitService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.util.List;

@Slf4j
@Controller
public class SuitTypeController {
    private static final String FRAGMENT_NAME = "suittypes";
    private static final String SECTION_NAME = "suit";
    private static final String REQUEST_MAPPING = "/" + SECTION_NAME + "/" + FRAGMENT_NAME;
    private final SuitTypeService service;

    private final SuitService suitService;

    private final SuitClassService suitClassService;

    private final BedsService bedsService;

    private final PhotoService photoService;
    private final UploadService uploadService;

    @Autowired
    public SuitTypeController(SuitTypeService service, SuitService suitTypeTypeService,
                              SuitClassService suitTypeClassService, BedsService bedsService,
                              PhotoService photoService, UploadService uploadService) {
        this.service = service;
        this.suitService = suitTypeTypeService;
        this.suitClassService = suitTypeClassService;
        this.bedsService = bedsService;
        this.photoService = photoService;
        this.uploadService = uploadService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<SuitType> entities = service.findAllPresent();
            model.addAttribute("entities", entities);
        }
        SuitType newEntity = new SuitType();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        model.addAttribute("beds", this.bedsService.findAll());
        model.addAttribute("suitClasses", this.suitClassService.findAll());
        model.addAttribute("pathToStorage", this.uploadService.getStorage().toString());
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") SuitType entity,
                       RedirectAttributes redirectAttributes) {
        List<SuitType> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") SuitType entity, RedirectAttributes redirectAttributes) {
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
        SuitType entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
            model.addAttribute("beds", this.bedsService.findAll());
            model.addAttribute("suitClasses", this.suitClassService.findAll());
            model.addAttribute("pathToStorage", this.uploadService.getStorage().toString());
        }
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") SuitType entity) {
        service.update(entity);
        return "redirect:/SuitType/" + FRAGMENT_NAME + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
