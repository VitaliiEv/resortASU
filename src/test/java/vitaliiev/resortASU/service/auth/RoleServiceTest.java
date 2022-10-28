package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vitaliiev.resortASU.ResortApplicationTests;
import vitaliiev.resortASU.entity.auth.Role;
import vitaliiev.resortASU.repository.auth.RoleRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
class RoleServiceTest extends ResortApplicationTests {

    @Autowired
    private RoleService service;

    @Autowired
    private RoleRepository roleRepository;

    private List<Role> roles = new ArrayList<>();

    @BeforeEach
    void before() {

        roles.add(new Role("TEST_USER1"));
        roles.add(new Role("TEST_USER2"));
        roles.add(new Role("TEST_USER3"));
    }

    @AfterEach
    void after() {
        roleRepository.deleteAll(roles);
        roles.clear();
    }

    @Test
    void create() {
        for (Role r : roles) {
            log.warn("creating role {}, and putting it into cache", r.getName());
            r = service.create(r);
            log.warn("getting role {}, from cache", r.getName());
            service.findRoleByName(r.getName());
            log.warn("getting role {}, from repository", r.getName());
            roleRepository.findByName(r.getName());
        }
    }

    @Test
    void findRoleById() {
    }

    @Test
    void findRoleByName() {
    }

    @Test
    void findRoleByNames() {
    }

    @Test
    void findAll() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void getAdmin() {
    }

    @Test
    void getUser() {
    }
}