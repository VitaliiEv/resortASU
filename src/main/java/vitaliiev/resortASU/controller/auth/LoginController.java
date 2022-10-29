package vitaliiev.resortASU.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vitaliiev.resortASU.model.auth.User;

@Controller
public class LoginController {

    @GetMapping("/login")
    private String login(Model model) {
        model.addAttribute("user", new User());
        return "/login";
    }

}
