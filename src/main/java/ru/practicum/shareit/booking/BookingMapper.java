package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.function.Function;

@Component
public class BookingMapper {

    private final Function<Booking, BookingDto> toDto = booking ->
            BookingDto.builder().id(booking.getId()).start(booking.getStart()).end(booking.getEnd())
                    .status(booking.getStatus()).build();

    private final Function<BookingDto, Booking> fromDto = bookingDto ->
            Booking.builder().id(bookingDto.getId()).start(bookingDto.getStart())
                    .end(bookingDto.getEnd()).build();

    public Function<BookingDto, Booking> getFromDto() {
        return fromDto;
    }

    public Function<Booking, BookingDto> getToDto() {
        return toDto;
    }

    public Function<Booking, BookingDto> getToDtoWithUserAndItem(UserDto user, ItemDto item) {
        return toDto
                .andThen(bookingDto -> {
                    bookingDto.setItem(item);
                    bookingDto.setBooker(user);
                    return bookingDto;
                });
    }

    public Function<Booking, BookingDto> getDtoWithOneUserAndItems(UserDto bookerDto) {
        return booking -> {
            Item item = booking.getItem();
            ItemDto itemDto = ItemDto.builder().id(item.getId()).name(item.getName())
                    .description(item.getDescription()).build();
            return getToDtoWithUserAndItem(bookerDto, itemDto)
                    .apply(booking);
        };
    }

    public ShortBookingDto getShortDto(Booking lastBooking) {
        return ShortBookingDto.builder().id(lastBooking.getId()).bookerId(lastBooking.getBooker().getId())
                .start(lastBooking.getStart()).end(lastBooking.getEnd()).build();
    }
}
