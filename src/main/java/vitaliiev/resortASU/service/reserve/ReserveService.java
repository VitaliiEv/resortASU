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
import vitaliiev.resortASU.model.reserve.PaymentStatus;
import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.model.reserve.ReserveStatus;
import vitaliiev.resortASU.model.reserve.ReserveSuit;
import vitaliiev.resortASU.model.suit.Suit;
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.repository.reserve.ReserveRepository;
import vitaliiev.resortASU.service.search.SuitSearchResult;
import vitaliiev.resortASU.service.suit.SuitService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReserveService {


    protected static final String ENTITY_NAME = Reserve.ENTITY_NAME;
    protected static final String CACHE_NAME = ENTITY_NAME;
    protected static final String CACHE_LIST_NAME = ENTITY_NAME + "List";

    private static final ExampleMatcher SEARCH_CONDITIONS_MATCH_ALL = ExampleMatcher
            .matching()
            .withIncludeNullValues()
            .withMatcher("number", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("checkin", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("checkout", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymentstatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("paymenttype", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withMatcher("reserveStatus", ExampleMatcher.GenericPropertyMatchers.exact().ignoreCase())
            .withIgnorePaths("id", "created", "lastchanged", "customers", "reserveReserve");

    private final ReserveRepository repository;

    private final ReserveStatusService reserveStatusService;

    private final ReserveSuitService reserveSuitService;

    private final SuitService suitService;
    private final SuitTypeService suitTypeService;

    private final PaymentStatusService paymentStatusService;

    @Autowired
    public ReserveService(ReserveRepository repository, ReserveStatusService reserveStatusService,
                          ReserveSuitService reserveSuitService, SuitService suitService,
                          SuitTypeService suitTypeService, PaymentStatusService paymentStatusService) {
        this.repository = repository;
        this.reserveStatusService = reserveStatusService;
        this.reserveSuitService = reserveSuitService;
        this.suitService = suitService;
        this.suitTypeService = suitTypeService;
        this.paymentStatusService = paymentStatusService;
    }

    @Caching(
            put = {@CachePut(cacheNames = CACHE_NAME, key = "#result?.id")},
            evict = {@CacheEvict(cacheNames = CACHE_LIST_NAME, allEntries = true)}
    )
    public Reserve create(Reserve entity) throws DataIntegrityViolationException {
        return repository.save(entity);
    }

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
        Set<ReserveSuit> reserveSuits = new HashSet<>();
        for (SuitSearchResult suitSearchResult : request.getSuitTypes()) {
            Set<ReserveSuit> temp = mapSuitTypeToReserveSuits(reserve, suitSearchResult);
            reserveSuits.addAll(temp);
        }

        reserveSuits.forEach(rs -> {
            rs = this.reserveSuitService.create(rs);
            Suit s = rs.getSuit();
            s.getReserveSuit().add(rs);
            this.suitService.update(s);
        });
        reserve.setReserveSuit(reserveSuits);
        reserve = this.update(reserve);
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
        reserveRequest.getSuitTypes().forEach(st -> {
            SuitType suitType = this.suitTypeService.findById(st.getSuitTypeId());
            st.setPhoto(suitType.getMainphoto());
            st.setBeds(suitType.getBeds().getBeds());
            st.setPrice(suitType.getCurrentprice());
            st.setSuitClass(suitType.getSuitClass().getSuitclass());
        });

        return reserveRequest;
    }

    private Set<ReserveSuit> mapSuitTypeToReserveSuits(Reserve reserve, SuitSearchResult suitSearchResult) {
        Set<ReserveSuit> reserveSuits = new HashSet<>(suitSearchResult.getQuantity());

        Set<Suit> suits = getMatchingSuits(reserve, suitSearchResult);
        for (Suit suit : suits) {
            ReserveSuit reserveSuit = new ReserveSuit();
            reserveSuit.setReserve(reserve);
            reserveSuit.setSuit(suit);
            reserveSuit.setPrice(suit.getSuittype().getCurrentprice()); // price should never be user input
            reserveSuits.add(reserveSuit);
        }
        return reserveSuits;
    }

    private Set<Suit> getMatchingSuits(Reserve reserve, SuitSearchResult suitSearchResult) {
        int exactSuits = suitSearchResult.getQuantity();
        Set<Suit> suits = new HashSet<>(this.suitService.findAllPresent());
        suits = suits.stream()
                .filter(s -> s.getSuittype().equals(this.suitTypeService.findById(suitSearchResult.getSuitTypeId())))
                .filter(isAvailable(reserve.getCheckin(), reserve.getCheckout()))
                .limit(exactSuits)
                .collect(Collectors.toSet());
        if (suits.size() < exactSuits) {
            throw new ResortASUGeneralException("Not enough suits found");
        }
//        Set<Suit> suits = suitTypeService.findById(suitSearchResult.getSuitTypeId()).getSuits();
        return suits;
    }

    public Predicate<Suit> isAvailable(Date checkIn, Date checkOut) { // fixme duplicate suit type search
        return suit -> suit.getReserveSuit().stream()
                .map(ReserveSuit::getReserve)
                .filter(reserveStatusCheck())
                .allMatch(periodsDontOverlap(checkIn, checkOut));
    }

    public Predicate<Reserve> periodsDontOverlap(Date checkIn, Date checkOut) {
        return reserve -> checkIn.after(reserve.getCheckout()) || checkIn.equals(reserve.getCheckout()) ||
                checkOut.before(reserve.getCheckin()) || checkOut.equals(reserve.getCheckin());
    }

    public Predicate<Reserve> reserveStatusCheck() {
        List<String> allowedStatuses = List.of("Accepted", "Finished");
        return reserve -> allowedStatuses.stream()
                .anyMatch(status -> status.equalsIgnoreCase(reserve.getReserveStatus().getStatus()));
    }

}
