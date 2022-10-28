package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleService {
    private final static String DEFAULT_ADMIN = "ROLE_ADMIN";
    private final static String DEFAULT_USER = "ROLE_USER";

    private final RoleRepository roleRepository;

    private final List<Role> roles;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
        this.roles = roleRepository.findAll();
    }

    public void create(Role role) {
        if (role == null) {
            log.warn("Expected non null argument. Role not created");
            return;
        }
        String name = role.getName();
        if (!name.startsWith("ROLE_")) {
            log.info("Expected role name with prefix ROLE_. Got {}", name);
            name = "ROLE_" + name;
            role.setName(name);
            log.warn("Adding prefix automatically. New role name: {}", name);
        }
        try {
            Role savedRole = roleRepository.save(role);
            roles.add(savedRole);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
        }
    }

    public Role findRoleById(Integer id) { //fixme use Optional
        return roles.stream()
                .filter(i -> i.getId().equals(id))
                .findAny().orElse(null);
    }

    public Role findRoleByName(String name) { //fixme use Optional
        return roles.stream()
                .filter(i -> i.getName().equals(name))
                .findAny().orElse(null);
    }

    public List<Role> findRoleByNames(String[] names) {
        return roles.stream()
                .filter(r -> {
                    for (String name : names) {
                        if (name.contains(r.getName())) {
                            return true;
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
    }

    public List<Role> findAll() {
        return roles;
    }

    public void update(Role role) {
        if (!role.getEnabled() && (role.getName().equals(DEFAULT_ADMIN) || role.getName().equals(DEFAULT_USER))) {
            log.warn("Can't disable ADMIN or USER built in role.");
            role.setEnabled(true);
        }
        try {
            Role savedRole = roleRepository.save(role);
            roles.add(savedRole);
        } catch (DataIntegrityViolationException e) {
            log.error(e.getMessage());
        }
    }

    public void delete(Integer id) {
        Role role = this.findRoleById(id);
        if (role != null) {
            if (role.getName().equals(DEFAULT_ADMIN) || role.getName().equals(DEFAULT_USER)) {
                log.warn("Cant delete predefined USER or ADMIN roles.");
            } else {
                roleRepository.deleteById(id);
                roles.remove(role);
            }
        }
    }


    public Role getAdmin() {
        return this.findRoleByName(DEFAULT_ADMIN);
    }

    public Role getUser() {
        return this.findRoleByName(DEFAULT_USER);
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
