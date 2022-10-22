package vitaliiev.resortASU.controller.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

@RestController
@RequestMapping("/roles")
public class RoleController {
//    @Autowired
//    private RoleRepository roleRepository;

//    @GetMapping("/all")
//    public Iterable<Role> listAll() {
//        return roleRepository.findAll();
//    }
}
