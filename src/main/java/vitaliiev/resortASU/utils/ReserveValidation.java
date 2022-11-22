package vitaliiev.resortASU.utils;

import vitaliiev.resortASU.model.reserve.Reserve;
import vitaliiev.resortASU.model.reserve.ReserveSuit;
import vitaliiev.resortASU.model.suit.Suit;

import java.sql.Date;
import java.util.List;
import java.util.Set;

public class ReserveValidation {
    public ReserveValidation() {
        throw new IllegalStateException("Utility class");
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
