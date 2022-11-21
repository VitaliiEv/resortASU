package vitaliiev.resortASU.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import vitaliiev.resortASU.ResortApplicationTests;
import vitaliiev.resortASU.model.auth.Role;
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
    private Role role1, role2, role3;

    @BeforeEach
    void before() {
        roles = new ArrayList<>();
        role1 = new Role("TEST_USER1");
        role2 = new Role("TEST_USER2");
        role3 = new Role("TEST_USER3");
        roles.add(role1);
        roles.add(role2);
        roles.add(role3);
    }

    @AfterEach
    void after() {
        roleRepository.deleteAll(roles);
        roles.forEach(r -> r = null);
        roles = null;
    }

    @Test
    void create() {
//        log.warn("creating role {}, and putting it into cache", role1.getName());
//        role1 = service.create(role1);
//        log.warn("getting role {}, from cache by id", role1.getName());
//        service.findRoleById(role1.getId());
//        log.warn("getting role {}, from repository", role1.getName());
//        roleRepository.findByName(role1.getName());
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