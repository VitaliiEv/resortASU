package vitaliiev.resortASU.controller.suit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.suit.Suit;
import vitaliiev.resortASU.service.suit.SuitService;
import vitaliiev.resortASU.service.suit.SuitStatusService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.util.List;

@Slf4j
@Controller
public class SuitController {
    private static final String FRAGMENT_NAME = "suit";
    private static final String SECTION_NAME = "suit";
    private static final String REQUEST_MAPPING = "/" + SECTION_NAME + "/" + FRAGMENT_NAME;
    private final SuitService service;

    private final SuitTypeService suitTypeService;

    private final SuitStatusService suitStatusService;

    @Autowired
    public SuitController(SuitService service, SuitTypeService suitTypeService, SuitStatusService suitStatusService) {
        this.service = service;
        this.suitTypeService = suitTypeService;
        this.suitStatusService = suitStatusService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Suit> entities = service.findAllPresent();
            model.addAttribute("entities", entities);
        }
        Suit newEntity = new Suit();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        model.addAttribute("SuitTypes", this.suitTypeService.findAllPresent());
        model.addAttribute("SuitStatus", this.suitStatusService.findAll());
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") Suit entity,
                       RedirectAttributes redirectAttributes) {
        List<Suit> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") Suit entity, RedirectAttributes redirectAttributes) {
        try {
            service.create(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            redirectAttributes.addFlashAttribute("creationError", e.getMessage());
        }
        return "redirect:" + REQUEST_MAPPING;
    }

    @GetMapping(REQUEST_MAPPING + "/{id}")
    public String read(@PathVariable Long id, Model model) {
        Suit entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
            model.addAttribute("SuitTypes", this.suitTypeService.findAllPresent());
            model.addAttribute("SuitStatus", this.suitStatusService.findAll());
        }
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") Suit entity) {
        service.update(entity);
        return "redirect:/Suit/" + FRAGMENT_NAME + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
