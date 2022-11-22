package vitaliiev.resortASU.service.reserve;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vitaliiev.resortASU.ResortASUGeneralException;
import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.model.reserve.ReserveSuit;
import vitaliiev.resortASU.model.suit.Suit;
import vitaliiev.resortASU.model.suit.SuitType;
import vitaliiev.resortASU.service.search.SuitSearchResult;
import vitaliiev.resortASU.service.suit.SuitService;
import vitaliiev.resortASU.service.suit.SuitTypeService;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReserveValidationService {

    private final SuitService suitService;
    private final SuitTypeService suitTypeService;

    @Autowired
    public ReserveValidationService(SuitService suitService, SuitTypeService suitTypeService) {
        this.suitService = suitService;
        this.suitTypeService = suitTypeService;
    }

    public Reserve validate(Reserve reserve) {
        Set<ReserveSuit> reserveSuits = reserve.getReserveSuit();
        for (ReserveSuit reserveSuit : reserveSuits) {
            boolean suitIsAvailable = suitIsAvailable(reserve.getCheckin(), reserve.getCheckout(), reserveSuit.getSuit());
            if (!suitIsAvailable) {
                reserve.setValid(false);
                return reserve;
            }
        }
        reserve.setValid(true);
        return reserve;
    }

    public Reserve reValidate(Reserve reserve) {
        Set<Suit> suits = new HashSet<>();
        Set<ReserveSuit> reserveSuits = reserve.getReserveSuit();
        Map<SuitType, Long> suitTypes = reserveSuits.stream()
                .map(reserveSuit -> reserveSuit.getSuit().getSuittype())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        for (Map.Entry<SuitType, Long> entry : suitTypes.entrySet()) {
            suits.addAll(this.getMatchingSuits(reserve, entry.getKey(), entry.getValue()));
        }
        if (reserveSuits.size() != suits.size()) {
            reserve.setValid(false);
        }
        for (ReserveSuit reserveSuit : reserveSuits) {
            Suit suit = suits.stream()
                    .filter(s-> s.getSuittype().equals(reserveSuit.getSuit().getSuittype()))
                    .findAny()
            .orElseThrow();
            reserveSuit.setSuit(suit);
            suits.remove(suit);
        }
        return reserve;
    }

    public Set<Suit> getMatchingSuits(Reserve reserve, SuitSearchResult suitSearchResult) {
        SuitType suitType = this.suitTypeService.findById(suitSearchResult.getSuitTypeId());
        return this.getMatchingSuits(reserve,suitType, suitSearchResult.getQuantity());
    }

    public Set<Suit> getMatchingSuits(Reserve reserve, SuitType suitType, long quantity) {
        Set<Suit> suits = new HashSet<>(this.suitService.findAllPresent());
        suits = suits.stream()
                .filter(s -> s.getSuittype().equals(suitType))
                .filter(s -> suitIsAvailable(reserve.getCheckin(), reserve.getCheckout(), s))
                .limit(quantity)
                .collect(Collectors.toSet());
        if (suits.size() < quantity) {
            throw new ResortASUGeneralException("Not enough suits found");
        }
        return suits;
    }

    public static boolean suitIsAvailable(Date checkIn, Date checkOut, Suit suit) {
        if (suit.getDeleted()) {
            return false;
        }
        Set<ReserveSuit> reserveSuits = suit.getReserveSuit();
        for (ReserveSuit reserveSuit : reserveSuits) {
            Reserve reserve = reserveSuit.getReserve();
            if (reserveAccepted(reserve) && !periodsDontOverlap(checkIn, checkOut, reserve)) {
                return false;
            }
        }
        return true;
    }

    public static boolean periodsDontOverlap(Date checkIn, Date checkOut, Reserve existingReserve) {
        return checkIn.after(existingReserve.getCheckout()) || checkIn.equals(existingReserve.getCheckout()) ||
                checkOut.before(existingReserve.getCheckin()) || checkOut.equals(existingReserve.getCheckin());
    }

    public static boolean reserveAccepted(Reserve reserve) {
        List<String> allowedStatuses = List.of("Accepted", "Finished");
        return allowedStatuses.stream()
                .anyMatch(status -> status.equalsIgnoreCase(reserve.getReserveStatus().getStatus()));
    }

}
