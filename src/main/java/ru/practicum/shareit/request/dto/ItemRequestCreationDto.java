package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)

public class ItemRequestCreationDto {
    @NotEmpty
    private String description;

   // private Timestamp created = Timestamp.valueOf(LocalDateTime.now());
}
