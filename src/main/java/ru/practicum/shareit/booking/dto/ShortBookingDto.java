package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ShortBookingDto {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;
}
