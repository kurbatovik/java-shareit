package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    private long id;
    @NotBlank(message = "Name can't blank", groups = {Create.class})
    private String name;
    @NotBlank(message = "Description can't blank", groups = {Create.class})
    private String description;
    @NotNull(message = "Available should be specified", groups = {Create.class})
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
}
