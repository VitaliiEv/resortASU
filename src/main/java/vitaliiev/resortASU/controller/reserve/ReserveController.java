package vitaliiev.resortASU.controller.reserve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.dto.ReserveRequest;
import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.service.UploadService;
import vitaliiev.resortASU.service.reserve.PaymentStatusService;
import vitaliiev.resortASU.service.reserve.PaymentTypeService;
import vitaliiev.resortASU.service.reserve.ReserveService;
import vitaliiev.resortASU.service.reserve.ReserveStatusService;

import java.util.List;

@Slf4j
@Controller
public class ReserveController {

    private static final String FRAGMENT_NAME = "reserve";
    private static final String SECTION_NAME = "reserve";
    private static final String REQUEST_MAPPING = "/" + SECTION_NAME + "/" + FRAGMENT_NAME;
    private final ReserveService service;

    private final ReserveStatusService reserveStatusService;
    private final PaymentStatusService paymentStatusService;
    private final PaymentTypeService paymentTypeService;

    private final UploadService uploadService;

    @Autowired
    public ReserveController(ReserveService service, ReserveStatusService reserveStatusService,
                             PaymentStatusService paymentStatusService, PaymentTypeService paymentTypeService,
                             UploadService uploadService) {
        this.service = service;
        this.reserveStatusService = reserveStatusService;
        this.paymentStatusService = paymentStatusService;
        this.paymentTypeService = paymentTypeService;
        this.uploadService = uploadService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String list(Model model) {
        if (!model.containsAttribute("entities")) {
            List<Reserve> entities = service.findAll();
            model.addAttribute("entities", entities);
        }
        Reserve newEntity = new Reserve();
        model.addAttribute("fragment", FRAGMENT_NAME);
        model.addAttribute("newEntity", newEntity);
        return SECTION_NAME + "/" + FRAGMENT_NAME;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "newEntity") Reserve entity,
                       RedirectAttributes redirectAttributes) {
        List<Reserve> entities = service.find(entity);
        redirectAttributes.addFlashAttribute("entities", entities);
        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/create")
    public String create(@ModelAttribute(name = "reserve") ReserveRequest request, Model model) {
//        List<Customer> adults = new ArrayList<>(request.getAdultBeds());
//        List<Customer> children = new ArrayList<>(request.getChildBeds());
        request = this.service.updateReserveRequest(request);
//        model.addAttribute("adults", adults);
//        model.addAttribute("children", children);
        model.addAttribute("reserve", request);
        return REQUEST_MAPPING + "/confirm";
    }

    @GetMapping(REQUEST_MAPPING + "/confirm")
    public String addCustomers(Model model) {

        return "redirect:" + REQUEST_MAPPING;
    }

    @PostMapping(REQUEST_MAPPING + "/confirm")
    public String confirm(@ModelAttribute(name = "reserve") ReserveRequest request, Model model) {
        this.service.create(request);
        return REQUEST_MAPPING;
    }


//    @GetMapping(REQUEST_MAPPING + "/{id}")
//    public String read(@PathVariable Long id, Model model) {
//        Reserve entity = service.findById(id);
//        if (entity != null) {
//            model.addAttribute("fragment", FRAGMENT_NAME);
//            model.addAttribute("entity", entity);
//        }
//        return SECTION_NAME + "/" + FRAGMENT_NAME;
//    }

    //    @PostMapping(REQUEST_MAPPING + "/update")
//    public String update(@ModelAttribute(name = "entity") Reserve entity) {
//        service.update(entity);
//        return "redirect:/Reserve/" + FRAGMENT_NAME + "/" + entity.getId();
//    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("reserveStatuses", this.reserveStatusService.findAll());
        model.addAttribute("paymentTypes", this.paymentTypeService.findAll());
        model.addAttribute("paymentStatuses", this.paymentStatusService.findAll());
        model.addAttribute("pathToStorage", this.uploadService.getStorage().toString());
    }
}
