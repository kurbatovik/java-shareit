package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking booking, Long userId, long itemId);

    Booking updateBookingStatus(Long bookingId, Boolean approved, Long userId);

    Booking getBookingById(Long bookingId, Long userId);

    List<Booking> getBookerBookings(Long userId, BookingState state, Pageable pageable);

    List<Booking> getOwnerBookings(Long userId, BookingState state, Pageable pageable);
}
