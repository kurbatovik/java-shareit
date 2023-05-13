package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.Variables;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadStateException;
import ru.practicum.shareit.exception.NotAvailableException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public Booking createBooking(Booking booking, Long userId, long itemId) {
        User user = returnUserOrThrowException(userId);
        Item item = returnItemOrThrowException(itemId);
        if (user.getId() == item.getOwner().getId()) {
            throw new NotFoundException("You owner");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        booking = bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking updateBookingStatus(Long bookingId, Boolean approved, Long userId) {
        Booking booking = returnBookingOrThrowException(bookingId);
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            throw new NotFoundException(Variables.USER_WITH_ID_NOT_HAVE_AVAILABLE, userId);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new NotAvailableException("Booking already considered");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return booking;
    }

    @Override
    public Booking getBookingById(Long bookingId, Long userId) {
        Booking booking = returnBookingOrThrowException(bookingId);
        Item item = booking.getItem();
        User booker = booking.getBooker();
        if (userId != item.getOwner().getId() && userId != booker.getId()) {
            throw new NotFoundException(Variables.USER_WITH_ID_NOT_HAVE_AVAILABLE, userId);
        }
        return booking;
    }

    @Override
    public List<Booking> getBookerBookings(Long userId, BookingState state, Pageable pageable) {
        User booker = returnUserOrThrowException(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerOrderByStartDesc(booker, pageable).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerAndStartBeforeAndEndAfter(booker, now, pageable).toList();
                break;
            case PAST:
                bookings = bookingRepository.findByBookerAndEndDateBeforeOrderByStartDesc(booker, now, pageable).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerAndStartDateAfterOrderByStartDesc(booker, now, pageable).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(booker, BookingStatus.WAITING, pageable).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(booker, BookingStatus.REJECTED, pageable).toList();
                break;
            default:
                throw new BadStateException("Unknown state");
        }
        return bookings;
    }

    @Override
    public List<Booking> getOwnerBookings(Long userId, BookingState state, Pageable pageable) {
        User owner = returnUserOrThrowException(userId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByOwnerOrderByStartDesc(owner, pageable).toList();
                break;
            case CURRENT:
                bookings = bookingRepository.findByOwnerAndStartBeforeAndEndAfterOrder(owner, now, pageable).toList();
                break;
            case PAST:
                bookings = bookingRepository.findByOwnerAndEndDateBeforeOrderByStartDesc(owner, now, pageable).toList();
                break;
            case FUTURE:
                bookings = bookingRepository.findByOwnerAndStartDateAfterOrderByStartDesc(owner, now, pageable).toList();
                break;
            case WAITING:
                bookings = bookingRepository.findByOwnerAndStatus(owner, BookingStatus.WAITING, pageable).toList();
                break;
            case REJECTED:
                bookings = bookingRepository.findByOwnerAndStatus(owner, BookingStatus.REJECTED, pageable).toList();
                break;
            default:
                    throw new BadStateException("Unknown state");
        }

        return bookings;
    }


    private Item returnItemOrThrowException(long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID: {0} not found", itemId));
        if (!item.getAvailable()) {
            throw new NotAvailableException("Item with ID: {0} not available", item.getId());
        }
        return item;
    }

    private Booking returnBookingOrThrowException(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with ID: {0} not found", bookingId));
    }

    private User returnUserOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(Variables.USER_WITH_ID_NOT_FOUND, userId));
    }
}
