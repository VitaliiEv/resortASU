package vitaliiev.resortASU.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vitaliiev.resortASU.entity.auth.User;
import vitaliiev.resortASU.service.auth.UserService;

import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String userProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "/user";
    }

    @PostMapping("/user")
    private String changePassword(@ModelAttribute(name = "user") @Valid User user, BindingResult bindingResult,
                                  Model model, @AuthenticationPrincipal User principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "/user";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Passwords dont match");
            return "/user";
        }
        user.setUsername(principal.getUsername());
        user = userService.changePassword(user);
        model.addAttribute("user", user);
        return "/user";
    }
}
