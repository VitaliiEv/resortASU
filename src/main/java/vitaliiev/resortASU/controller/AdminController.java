package vitaliiev.resortASU.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import vitaliiev.resortASU.entity.auth.User;
import vitaliiev.resortASU.service.auth.UserService;


import java.util.List;

@Controller
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    public String visit(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", user);
        return "/user";
    }

    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<User> users = userService.allUsers();
        model.addAttribute("users", users);
        return "/admin";
    }
}
