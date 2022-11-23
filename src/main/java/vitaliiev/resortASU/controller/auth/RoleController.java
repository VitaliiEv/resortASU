package vitaliiev.resortASU.controller.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.auth.Role;
import vitaliiev.resortASU.service.auth.RoleService;

import java.util.List;

@Slf4j
@Controller
public class RoleController {
    private static final String FRAGMENT_NAME = "roles";
    private static final String SECTION_NAME = "admin";

    private static final String MAIN_VIEW = SECTION_NAME + "/" + FRAGMENT_NAME;
    private static final String REQUEST_MAPPING = "/" + MAIN_VIEW;



    private final RoleService service;

    @Autowired
    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Role> entities = service.findAll();
            model.addAttribute("entities", entities);
        }
        Role newEntity = new Role();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        return MAIN_VIEW;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "entity") Role entity,
                       RedirectAttributes redirectAttributes) {
        List<Role> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") Role role, RedirectAttributes redirectAttributes) {
        try {
            service.create(role);
        } catch (DataIntegrityViolationException e) {
            String message = "Role already exists. ";
            log.warn(message + e.getMessage());
            redirectAttributes.addFlashAttribute("creationError", message);
        }
        return "redirect:" + REQUEST_MAPPING;
    }

    @GetMapping(REQUEST_MAPPING + "/{id}")
    public String read(@PathVariable Integer id, Model model) {
        Role entity = service.findRoleById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
        }
        return MAIN_VIEW;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") Role entity) {
        service.update(entity);
        return "redirect:" + REQUEST_MAPPING + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
