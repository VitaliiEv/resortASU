package vitaliiev.resortASU.service.reserve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.ResortASUGeneralException;
import vitaliiev.resortASU.dto.ReserveRequest;
import vitaliiev.resortASU.model.customer.Customer;
import vitaliiev.resortASU.model.reserve.PaymentStatus;
import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.model.reserve.ReserveStatus;
import vitaliiev.resortASU.model.reserve.ReserveSuit;
import vitaliiev.resortASU.model.suit.Suit;
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.repository.reserve.ReserveRepository;
import vitaliiev.resortASU.service.customer.CustomerService;
import vitaliiev.resortASU.service.search.SuitSearchResult;
import vitaliiev.resortASU.service.suit.SuitService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ReserveService {


    protected static final String ENTITY_NAME = Reserve.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIgnoreNullValues()
            .withMatcher("checkin", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("checkout", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymentstatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymenttype", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("reserveStatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "created", "lastchanged", "customers", "reserveReserve");

    private final ReserveRepository repository;

    private final ReserveStatusService reserveStatusService;

    private final ReserveSuitService reserveSuitService;

    private final ReserveValidationService reserveValidationService;
    private final SuitService suitService;
    private final SuitTypeService suitTypeService;

    private final PaymentStatusService paymentStatusService;

    private final CustomerService customerService;


    @Autowired
    public ReserveService(ReserveRepository repository, ReserveStatusService reserveStatusService,
                          ReserveSuitService reserveSuitService, ReserveValidationService reserveValidationService,
                          SuitService suitService, SuitTypeService suitTypeService,
                          PaymentStatusService paymentStatusService, CustomerService customerService) {
        this.repository = repository;
        this.reserveStatusService = reserveStatusService;
        this.reserveSuitService = reserveSuitService;
        this.reserveValidationService = reserveValidationService;
        this.suitService = suitService;
        this.suitTypeService = suitTypeService;
        this.paymentStatusService = paymentStatusService;
        this.customerService = customerService;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve create(Reserve entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve create(ReserveRequest request) throws DataIntegrityViolationException {
        Reserve reserve = new Reserve();
        ReserveStatus reserveStatus = this.reserveStatusService.findAll().stream()
                .filter(e -> e.getStatus().equalsIgnoreCase("Created"))
                .findAny()
                .orElseThrow();
        PaymentStatus paymentStatus = this.paymentStatusService.findAll().stream()
                .filter(e -> e.getPaymentstatus().equalsIgnoreCase("not payed"))
                .findAny()
                .orElseThrow();
        Timestamp now = Timestamp.from(Instant.now());
        reserve.setReserveStatus(reserveStatus);
        reserve.setPaymentstatus(paymentStatus);
        reserve.setCheckin(request.getCheckIn());
        reserve.setCheckout(request.getCheckOut());
        reserve.setPaymenttype(request.getPaymentType());
        reserve.setLastchanged(now);
        reserve.setCreated(now);
        reserve = repository.save(reserve); // must be done here, because later we will need resort Id

        Set<Customer> customers = new HashSet<>();
        customers.addAll(request.getAdults());
        customers.addAll(request.getAdults());
        reserve.setCustomers(new HashSet<>());
        for (Customer c : customers) {
            List<Customer> similar = this.customerService.find(c);
            if (similar.isEmpty()) {
                c.setReserves(new HashSet<>());
                c.addReserve(reserve);
                this.customerService.create(c);
            } else if (similar.size() == 1) {
                c.addReserve(reserve);
                this.customerService.update(c);
            } else {
                throw new ResortASUGeneralException("Several matching customers found.");
            }
        }
//        reserve.setCustomers(customers);
        Set<ReserveSuit> reserveSuits = new HashSet<>();
        for (SuitSearchResult suitSearchResult : request.getSuitTypes()) {
            Set<ReserveSuit> temp = mapSearchResultToReserveSuits(reserve, suitSearchResult);
            reserveSuits.addAll(temp);
        }
        reserveSuits.forEach(rs -> {
            rs = this.reserveSuitService.create(rs);
            Suit s = rs.getSuit();
            s.getReserveSuit().add(rs);
            this.suitService.update(s);
        });
        reserve.setReserveSuit(reserveSuits);
        reserve.setValid(true);
        reserve = this.update(reserve);
        return reserve;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve accept(Long id) {
        Reserve reserve = repository.findById(id).orElseThrow();
        List<ReserveStatus> reserveStatuses = this.reserveStatusService.findAll();
        ReserveStatus created = reserveStatuses.stream()
                .filter(status -> status.getStatus().equalsIgnoreCase("Created"))
                .findAny()
                .orElseThrow();
        if (reserve.getReserveStatus().equals(created)) {
            ReserveStatus newStatus = reserveStatuses.stream()
                    .filter(status -> status.getStatus().equalsIgnoreCase("Accepted"))
                    .findAny()
                    .orElseThrow();
            reserve = this.reserveValidationService.validate(reserve);
            if (!reserve.isValid()) {
                reserve = this.reserveValidationService.reValidate(reserve);
            }
            if (reserve.isValid()) {
                reserve.setReserveStatus(newStatus);
                this.update(reserve);
            }
//            for (ReserveSuit reserveSuit : reserve.getReserveSuit()) {
//                if (this.reserveValidationService.suitIsAvailable(reserve.getCheckin(), reserve.getCheckout(),
//                        reserveSuit.getSuit())) {
//                    return reserve; //fixme only firs suit checked
//                    // todo validate here
//                }
//            }
//            reserve.setReserveStatus(newStatus);
//            this.update(reserve);
        }
        return reserve;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve decline(Long id) {
        Reserve reserve = repository.findById(id).orElseThrow();
        List<ReserveStatus> reserveStatuses = this.reserveStatusService.findAll();
        ReserveStatus created = reserveStatuses.stream()
                .filter(status -> status.getStatus().equals("Created"))
                .findAny()
                .orElseThrow();
        if (reserve.getReserveStatus().equals(created)) {
            ReserveStatus newStatus = reserveStatuses.stream()
                    .filter(status -> status.getStatus().equals("Declined"))
                    .findAny()
                    .orElseThrow();
            reserve.setReserveStatus(newStatus);
            this.update(reserve);
        }
        return reserve;
    }

    @Cacheable(cacheNames = CACHE_NAME, key = "#id")
    public Reserve findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Reserve> find(Reserve entity) {
        Example<Reserve> example = Example.of(entity, SEARCH_CONDITIONS_MATCH_ALL);
        return repository.findAll(example, Sort.by("id"));
    }

    @Cacheable(cacheNames = CACHE_LIST_NAME)
    public List<Reserve> findAll() {
        List<Reserve> reserves = repository.findAll(Sort.by("id"));
        this.validateNew(reserves);
        return repository.findAll(Sort.by("id"));
    }


    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve update(Reserve entity) {
        entity.setLastchanged(Timestamp.from(Instant.now()));
        try {
            return repository.save(entity);
        } catch (DataIntegrityViolationException e) {
            log.warn(e.getMessage());
            return entity;
        }
    }

    public ReserveRequest updateReserveRequest(ReserveRequest reserveRequest) {
        reserveRequest.getSuitTypes().forEach(this::updateSuitSearchRequest);
        reserveRequest.initCustomerLists();
        return reserveRequest;
    }

    public void updateSuitSearchRequest(SuitSearchResult suitSearchResult) {
        SuitType suitType = this.suitTypeService.findById(suitSearchResult.getSuitTypeId());
        suitSearchResult.update(suitType);
    }


    private Set<ReserveSuit> mapSearchResultToReserveSuits(Reserve reserve, SuitSearchResult suitSearchResult) {
        Set<ReserveSuit> reserveSuits = new HashSet<>(suitSearchResult.getQuantity());

        Set<Suit> suits = this.reserveValidationService.getMatchingSuits(reserve, suitSearchResult);
        for (Suit suit : suits) {
            ReserveSuit reserveSuit = new ReserveSuit();
            reserveSuit.setReserve(reserve);
            reserveSuit.setSuit(suit);
            reserveSuit.setPrice(suit.getSuittype().getCurrentprice()); // price should never be user input
            reserveSuits.add(reserveSuit);
        }
        return reserveSuits;
    }

    private void validateNew(List<Reserve> reserves) {
        for (Reserve reserve : reserves) {
            if (reserve.getReserveStatus().getStatus().equalsIgnoreCase("Created")) {
                this.reserveValidationService.validate(reserve);
            }
        }
    }
}
