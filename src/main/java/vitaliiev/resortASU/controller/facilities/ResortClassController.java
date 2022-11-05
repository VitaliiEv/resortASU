package vitaliiev.resortASU.controller.facilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.facilities.ResortClass;
import vitaliiev.resortASU.service.facilities.ResortClassService;

import java.util.List;

@Slf4j
@Controller
//@RequestMapping("/resort")
public class ResortClassController {
    private static final String FRAGMENT_NAME = "classes";
    private static final String SECTION_NAME = "resort";
    private static final String REQUEST_MAPPING = "/" + SECTION_NAME + "/" + FRAGMENT_NAME;

    private final ResortClassService service;

    @Autowired
    public ResortClassController(ResortClassService service) {
        this.service = service;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<ResortClass> entities = service.findAll();
            model.addAttribute("entities", entities);
        }
        ResortClass newEntity = new ResortClass();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") ResortClass entity,
                       RedirectAttributes redirectAttributes) {
        List<ResortClass> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") ResortClass entity, RedirectAttributes redirectAttributes) {
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
        ResortClass entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
        }
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") ResortClass entity) {
        service.update(entity);
        return "redirect:/resort/" + FRAGMENT_NAME + "/" + entity.getId();
    }

    @PostMapping(FRAGMENT_NAME + "/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
