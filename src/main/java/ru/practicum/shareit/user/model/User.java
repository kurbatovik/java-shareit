package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */

@Builder
@Getter
@Setter
@ToString
public class User {

    private long id;

    private String email;

    private String name;
}
