package vitaliiev.resortASU.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vitaliiev.resortASU.ResortASUGeneralException;
import vitaliiev.resortASU.dto.ReserveRequest;
import vitaliiev.resortASU.dto.SuitSearchRequest;
import vitaliiev.resortASU.dto.SuitSearchResultSet;
import vitaliiev.resortASU.service.UploadService;
import vitaliiev.resortASU.service.search.SearchService;
import vitaliiev.resortASU.service.suit.SuitClassService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Controller
public class SearchController {

    private static final String SECTION_NAME = "search";

    private static final String MAIN_VIEW = SECTION_NAME;
    private static final String REQUEST_MAPPING = "/" + MAIN_VIEW;
    private final SearchService service;

    private final SuitClassService suitClassService;

    private final UploadService uploadService;

    @Autowired
    public SearchController(SearchService service, SuitClassService suitTypeClassService, UploadService uploadService) {
        this.service = service;
        this.suitClassService = suitTypeClassService;
        this.uploadService = uploadService;
    }

    @GetMapping(REQUEST_MAPPING)
    public String view() {
        return MAIN_VIEW;
    }

    @PostMapping(REQUEST_MAPPING + "/find")
    public String find(@ModelAttribute(name = "searchRequest") @Valid SuitSearchRequest suitSearchRequest,
                       BindingResult bindingResult,
                       RedirectAttributes redirectAttributes,
                       Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("validationErrors", bindingResult.getAllErrors());
            return MAIN_VIEW;
        }
        try {
            List<SuitSearchResultSet> results = this.service.find(suitSearchRequest);
//            ReserveRequest reserveRequest = new ReserveRequest(suitSearchRequest);
            ReserveRequest reserveRequest = new ReserveRequest();
            redirectAttributes.addFlashAttribute("reserve", reserveRequest);
            redirectAttributes.addFlashAttribute("completedSearchRequest", suitSearchRequest);
            redirectAttributes.addFlashAttribute("searchResults", results);

        } catch (ResortASUGeneralException e) {
            log.warn(e.getMessage());
            redirectAttributes.addFlashAttribute("additionalErrors", e.getMessage()); //fixme
        }
        return "redirect:" + REQUEST_MAPPING;
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        SuitSearchRequest suitSearchRequest = new SuitSearchRequest();
        model.addAttribute("searchRequest", suitSearchRequest);
        model.addAttribute("suitClasses", this.suitClassService.findAll());
        model.addAttribute("pathToStorage", this.uploadService.getStorage().toString());
    }

}
