package vitaliiev.resortASU.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vitaliiev.resortASU.entity.auth.User;
import vitaliiev.resortASU.repository.auth.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/all")
    public Iterable<User> listAll() {
        return userRepository.findAll();
    }
}
