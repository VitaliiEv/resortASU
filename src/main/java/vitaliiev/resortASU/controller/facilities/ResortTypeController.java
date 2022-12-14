package vitaliiev.resortASU.controller.facilities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.facilities.ResortType;
import vitaliiev.resortASU.service.facilities.ResortTypeService;

import java.util.List;

@Slf4j
@Controller
public class ResortTypeController {
    private static final String FRAGMENT_NAME = "types";
    private static final String SECTION_NAME = "resort";
    private static final String MAIN_VIEW = SECTION_NAME + "/" + FRAGMENT_NAME;
    private static final String REQUEST_MAPPING = "/" + MAIN_VIEW;
    private final ResortTypeService service;

    @Autowired
    public ResortTypeController(ResortTypeService service) {
        this.service = service;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<ResortType> entities = service.findAll();
            model.addAttribute("entities", entities);
        }
        ResortType newEntity = new ResortType();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        return MAIN_VIEW;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") ResortType entity,
                       RedirectAttributes redirectAttributes) {
        List<ResortType> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") ResortType entity, RedirectAttributes redirectAttributes) {
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
        ResortType entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
        }
        return MAIN_VIEW;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") ResortType entity) {
        service.update(entity);
        return "redirect:" + REQUEST_MAPPING + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
