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
public class UserController {
    private static final String FRAGMENT_NAME = "users";
    private static final String SECTION_NAME = "admin";

    private static final String MAIN_VIEW = SECTION_NAME + "/" + FRAGMENT_NAME;
    private static final String REQUEST_MAPPING = "/" + MAIN_VIEW;

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<User> entities = userService.findAll();
            model.addAttribute("entities", entities);
        }
        User newEntity = new User();
        model.addAttribute("newEntity", newEntity);
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("roles", roleService.findAll());
        return MAIN_VIEW;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "entity") User entity,
                       RedirectAttributes redirectAttributes) {
        List<User> entities = userService.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @GetMapping(REQUEST_MAPPING + "/{id}")
    public String read(@PathVariable Long id, Model model) {
        User entity = userService.findUserById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
            model.addAttribute("roles", roleService.findAll());
        }
        return MAIN_VIEW;
    }

    @PostMapping (REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") User entity) {
        userService.update(entity);
        return "redirect:" + REQUEST_MAPPING + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        userService.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }
}
