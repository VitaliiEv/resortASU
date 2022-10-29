package vitaliiev.resortASU.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vitaliiev.resortASU.model.auth.User;
import vitaliiev.resortASU.service.auth.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserProfileController {

    private final UserService userService;

    @Autowired
    public UserProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String userProfile(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userService.findUserByUsername(user.getUsername()));
        return "user";
    }

    @PostMapping
    private String changePassword(@ModelAttribute(name = "user") @Valid User user, BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "user";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Passwords dont match");
            return "user";
        }
        user = userService.changePassword(user);
        model.addAttribute("user", user);
        return "user";
    }
}
