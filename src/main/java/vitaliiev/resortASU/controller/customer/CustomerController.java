package vitaliiev.resortASU.controller.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.model.customer.Customer;
import vitaliiev.resortASU.service.customer.CustomerService;
import vitaliiev.resortASU.service.customer.GenderService;
import vitaliiev.resortASU.service.reserve.ReserveSuitService;

import java.util.List;

@Slf4j
@Controller
public class CustomerController {
    private static final String FRAGMENT_NAME = "customer";
    private static final String SECTION_NAME = "customer";
    private static final String REQUEST_MAPPING = "/" + SECTION_NAME + "/" + FRAGMENT_NAME;
    private final CustomerService service;

    private final GenderService genderService;

    private final ReserveSuitService reserveSuitService;


    @Autowired
    public CustomerController(CustomerService service, GenderService genderService,
                              ReserveSuitService reserveSuitService) {
        this.service = service;
        this.genderService = genderService;
        this.reserveSuitService = reserveSuitService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Customer> entities = service.findAllPresent();
            model.addAttribute("entities", entities);
        }
        Customer newEntity = new Customer();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") Customer entity,
                       RedirectAttributes redirectAttributes) {
        List<Customer> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "newEntity") Customer entity, RedirectAttributes redirectAttributes) {
        try {
            service.create(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            redirectAttributes.addFlashAttribute("creationError", e.getMessage());
        }
        return "redirect:" + REQUEST_MAPPING;
    }

    @GetMapping(REQUEST_MAPPING + "/{id}")
    public String read(@PathVariable Long id, Model model) {
        Customer entity = service.findById(id);
        if (entity != null) {
            model.addAttribute("fragment", FRAGMENT_NAME);
            model.addAttribute("entity", entity);
        }
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/update")
    public String update(@ModelAttribute(name = "entity") Customer entity) {
        service.update(entity);
        return "redirect:/customer/" + FRAGMENT_NAME + "/" + entity.getId();
    }

    @PostMapping(REQUEST_MAPPING + "/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        service.delete(id);
        return "redirect:" + REQUEST_MAPPING;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("genders", this.genderService.findAll());
    }
}
