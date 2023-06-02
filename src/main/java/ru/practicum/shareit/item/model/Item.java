package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class Item {

    private Integer id;

    private User owner;

    private String name;

    private String description;

    private Boolean available;

}
