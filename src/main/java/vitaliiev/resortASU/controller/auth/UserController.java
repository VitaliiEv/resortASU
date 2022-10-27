package vitaliiev.resortASU.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.entity.auth.User;
import vitaliiev.resortASU.service.auth.RoleService;
import vitaliiev.resortASU.service.auth.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    private static final String ENTITY_NAME = "users";
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/user")
    public String userProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "/user";
    }

    @PostMapping("/user")
    private String changePassword(@ModelAttribute(name = "user") @Valid User user, BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "/user";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Passwords dont match");
            return "/user";
        }
        user = userService.changePassword(user);
        model.addAttribute("user", user);
        return "/user";
    }

    @GetMapping("/admin/"+ ENTITY_NAME)
    public String list(Model model) {
        List<User> entities = userService.findAll();
        model.addAttribute("fragment", ENTITY_NAME);
        model.addAttribute("entities", entities);
        return "admin/" + ENTITY_NAME;
    }

    @PostMapping("/admin/"+ ENTITY_NAME)
    public String listSearchResults(RedirectAttributes redirectAttributes,
                                    @RequestParam String username,
                                    @RequestParam String roles) {
        // todo
        List<User> entities = userService.searchUsers(username, roles);
        redirectAttributes.addFlashAttribute("fragment", ENTITY_NAME);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:/admin";
    }

    @GetMapping("/admin/"+ ENTITY_NAME + "/{id}")
    public String read(@PathVariable Long id, Model model) {
        User entity = userService.findUserById(id);
        if (entity != null) {
            model.addAttribute("fragment", ENTITY_NAME);
            model.addAttribute("entity", entity);
            model.addAttribute("roles", roleService.findAll());
        }
        return "admin/" + ENTITY_NAME;
    }

    @PostMapping ("/admin/"+ ENTITY_NAME + "/update")
    public String update(@ModelAttribute(name = "entity") User entity) {
        userService.updateUser(entity);
        return "redirect:/admin/" + ENTITY_NAME + "/" + entity.getId();
    }

    @PostMapping("/admin/" + ENTITY_NAME + "/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/admin/" + ENTITY_NAME;
    }
}
