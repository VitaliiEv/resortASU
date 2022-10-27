package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

import java.util.*;

@Slf4j
@Service
public class RoleService {
    private final static String DEFAULT_ADMIN = "ROLE_ADMIN";
    private final static String DEFAULT_USER = "ROLE_USER";

    private final RoleRepository roleRepository;

    private final Role admin;
    private final Role user;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.admin = this.roleRepository.findByName(DEFAULT_ADMIN);
        this.user = this.roleRepository.findByName(DEFAULT_USER);
    }

    public void create(Role role) {
        String name = role.getName();
        if (!name.startsWith("ROLE_")) {
            log.info("Expected role name with prefix ROLE_. Got {}", name);
            name = "ROLE_" + name;
            role.setName(name);
            log.warn("Adding prefix automatically. New role name: {}", name);
        }
        roleRepository.save(role);
    }

    public Role findRoleById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> findRoleByNames(String[] names) {
        List<Role> rolesList = new ArrayList<>();
        if (names == null || names.length == 0) {
            return rolesList;
        }
        List<String> roleNamesList = Arrays.asList(names);
        for (String roleName : names) {
            Role role = roleRepository.findByName(roleName);
            if (role != null) {
                rolesList.add(role);
            }
        }
        return rolesList;
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public void update(Role role) {
        if (!role.getEnabled() && (role.getName().equals(DEFAULT_ADMIN) || role.getName().equals(DEFAULT_USER))) {
            log.warn("Can't disable ADMIN or USER built in role.");
            role.setEnabled(true);
        }
        roleRepository.save(role);
    }

    public void delete(Integer id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        optionalRole.ifPresent(r -> {
            if (r.getName().equals(this.admin.getName()) ||
                    r.getName().equals(this.user.getName())) {
                log.warn("Cant delete predefined USER or ADMIN roles.");
            } else {
                roleRepository.deleteById(id);
            }
        });
    }


    public Role getAdmin() {
        return copyRole(this.admin);
    }

    public Role getUser() {
        return copyRole(this.user);
    }

    private Role copyRole(Role role) {
        Role copy = new Role();
        copy.setId(role.getId());
        copy.setName(role.getName());
        copy.setEnabled(role.getEnabled());
        copy.setUsers(role.getUsers());
        return copy;
    }
}
