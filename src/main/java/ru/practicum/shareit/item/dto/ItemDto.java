package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {
    private long id;
    @NotBlank(message = "Name can't blank", groups = {Create.class})
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;
    @NotBlank(message = "Description can't blank", groups = {Create.class})
    @Size(max = 512, groups = {Create.class, Update.class})
    private String description;
    @NotNull(message = "Available should be specified", groups = {Create.class})
    private Boolean available;
    @Positive(message = "Request ID can't be less than 1", groups = {Create.class})
    private Long requestId;
}
