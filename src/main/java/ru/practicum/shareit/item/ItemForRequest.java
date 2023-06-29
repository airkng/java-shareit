package ru.practicum.shareit.item;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemForRequest {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;
}
