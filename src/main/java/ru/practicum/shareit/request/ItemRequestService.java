package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(Long userId, ItemRequestCreationDto itemRequestCreationDto);

    List<ItemRequestDto> getAllMyRequest(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestDto getById(Long userId, Long requestId);
}
