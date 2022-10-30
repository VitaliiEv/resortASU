package vitaliiev.resortASU.controller.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import vitaliiev.resortASU.model.auth.User;
import vitaliiev.resortASU.service.auth.UserService;

import javax.validation.Valid;

@Slf4j
@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    private String register(Model model) {
        model.addAttribute("user", new User());
        return "/register";
    }

    @PostMapping("/register")
    private String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return "/register";
        }
        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("passwordError", "Passwords dont match");
            return "/register";
        }
        try {
            user = userService.create(user);
            model.addAttribute("user", user);
            return "redirect:/";
        } catch (DataIntegrityViolationException e) {
            String message = "Can't create user: user already exists. ";
            log.warn(message + e.getMessage());
            model.addAttribute("usernameError", message);
            return "/register";
        }
    }

}
