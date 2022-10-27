package vitaliiev.resortASU.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.service.auth.UserService;

@Controller
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/admin")
    public String adminPanel(Model model) {
        boolean entityPersists = model.getAttribute("fragment") != null &&
                model.getAttribute("entities") != null;
        model.addAttribute("entityPersists", entityPersists);
        return "/admin";
    }


}
