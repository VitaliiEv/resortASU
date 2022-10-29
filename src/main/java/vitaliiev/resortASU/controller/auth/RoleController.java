package vitaliiev.resortASU.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.auth.Role;
import vitaliiev.resortASU.service.auth.RoleService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class RoleController {
    private static final String ENTITY_NAME = "roles";

    private final RoleService service;

    @Autowired
    public RoleController(RoleService service) {
        this.service = service;
    }

    @GetMapping(ENTITY_NAME)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Role> entities = service.findAll();
            model.addAttribute("entities", entities);
        }
        Role newEntity = new Role();
        model.addAttribute("fragment", ENTITY_NAME);
        model.addAttribute("newEntity", newEntity);
        return "admin/" + ENTITY_NAME;
    }

    @PostMapping(ENTITY_NAME + "/find")
    public String find(@ModelAttribute(name = "entity") Role role,
                       @RequestParam(name = "enabled") String enabled,
                       RedirectAttributes redirectAttributes) {
        if (enabled.equals("all")) {
            role.setEnabled(null);
        } else {
            role.setEnabled(Boolean.valueOf(enabled));
        }
        List<Role> entities = service.find(role);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:/admin/" + ENTITY_NAME;
    }

    @PostMapping(ENTITY_NAME + "/create")
    public String create(@ModelAttribute(name = "entity") Role role) {
        service.create(role);
        return "redirect:/admin/" + ENTITY_NAME;
    }

    @GetMapping(ENTITY_NAME + "/{id}")
    public String read(@PathVariable Integer id, Model model) {
        Role entity = service.findRoleById(id);
        if (entity != null) {
            model.addAttribute("fragment", ENTITY_NAME);
            model.addAttribute("entity", entity);
        }
        return "admin/" + ENTITY_NAME;
    }

    @PostMapping(ENTITY_NAME + "/update")
    public String update(@ModelAttribute(name = "entity") Role entity) {
        service.update(entity);
        return "redirect:/admin/" + ENTITY_NAME + "/" + entity.getId();
    }

    @PostMapping(ENTITY_NAME + "/delete")
    public String delete(@RequestParam(name = "id") Integer id) {
        service.delete(id);
        return "redirect:/admin/" + ENTITY_NAME;
    }
}
