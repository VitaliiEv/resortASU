package vitaliiev.resortASU.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.auth.User;
import vitaliiev.resortASU.service.auth.RoleService;
import vitaliiev.resortASU.service.auth.UserService;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class UserController {

    private static final String ENTITY_NAME = "users";
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(ENTITY_NAME)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<User> entities = userService.findAll();
            model.addAttribute("entities", entities);
        }
        List<User> entities = userService.findAll();
        model.addAttribute("fragment", ENTITY_NAME);
        return "admin/" + ENTITY_NAME;
    }

    @PostMapping(ENTITY_NAME)
    public String find(@ModelAttribute(name = "entity") User entity,
//                       @RequestParam String roles, // todo
                       @RequestParam(name = "enabled") String enabled,
                       RedirectAttributes redirectAttributes) {
        if (enabled.equals("all")) {
            entity.setEnabled(null);
        } else {
            entity.setEnabled(Boolean.valueOf(enabled));
        }
        List<User> entities = userService.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:/admin/" + ENTITY_NAME;
    }

    @GetMapping(ENTITY_NAME + "/{id}")
    public String read(@PathVariable Long id, Model model) {
        User entity = userService.findUserById(id);
        if (entity != null) {
            model.addAttribute("fragment", ENTITY_NAME);
            model.addAttribute("entity", entity);
            model.addAttribute("roles", roleService.findAll());
        }
        return "admin/" + ENTITY_NAME;
    }

    @PostMapping (ENTITY_NAME + "/update")
    public String update(@ModelAttribute(name = "entity") User entity) {
        userService.update(entity);
        return "redirect:/admin/" + ENTITY_NAME + "/" + entity.getId();
    }

    @PostMapping(ENTITY_NAME + "/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        userService.delete(id);
        return "redirect:/admin/" + ENTITY_NAME;
    }
}
