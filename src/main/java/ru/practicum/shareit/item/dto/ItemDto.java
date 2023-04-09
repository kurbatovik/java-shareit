package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@ToString
@Builder
public class ItemDto {
    private long id;
    @NotBlank(message = "Name can't blank", groups = {Create.class})
    private String name;
    @NotBlank(message = "Description can't blank", groups = {Create.class})
    private String description;
    @NotNull(message = "Available should be specified", groups = {Create.class})
    private Boolean available;
}
