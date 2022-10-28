package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

import java.util.*;

@Slf4j
@Service
@CacheConfig(cacheNames = "roles")
public class RoleService {
    private final static String DEFAULT_ADMIN = "ROLE_ADMIN";
    private final static String DEFAULT_USER = "ROLE_USER";

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @CachePut(key = "#result?.id")
    public Role create(Role role) {
        String name = role.getName();
        if (!name.startsWith("ROLE_")) {
            log.info("Expected role name with prefix ROLE_. Got {}", name);
            name = "ROLE_" + name;
            role.setName(name);
            log.warn("Adding prefix automatically. New role name: {}", name);
        }
        try {
            return roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return role;
        }
    }

    @Cacheable(key = "#id")
    public Role findRoleById(Integer id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Cacheable(key = "#name")
    public Role findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    public List<Role> findRoleByNames(String[] names) { // fixme
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

    public List<Role> findAll() { // FIXME: 28.10.2022
        return roleRepository.findAll();
    }

    @Cacheable(key = "#role.id")
    public Role update(Role role) {
        if (!role.getEnabled() && (role.getName().equals(DEFAULT_ADMIN) || role.getName().equals(DEFAULT_USER))) {
            log.warn("Can't disable ADMIN or USER built in role.");
            role.setEnabled(true);
        }
        try {
            return roleRepository.save(role);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return role;
        }
    }

    @CacheEvict(key = "#id")
    public void delete(Integer id) {
        Optional<Role> optionalRole = roleRepository.findById(id);
        optionalRole.ifPresent(r -> {
            if (r.getName().equals(DEFAULT_ADMIN) ||
                    r.getName().equals(DEFAULT_USER)) {
                log.warn("Cant delete predefined USER or ADMIN roles.");
            } else {
                roleRepository.deleteById(id);
            }
        });
    }

    @CachePut(key = "#result.id")
    public Role getAdmin() {
        return findRoleByName(DEFAULT_ADMIN);
    }

    @CachePut(key = "#result.id")
    public Role getUser() {
        return findRoleByName(DEFAULT_USER);
    }

}
