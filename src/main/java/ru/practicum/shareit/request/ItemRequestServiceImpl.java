package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepositoryDb;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserRepositoryDb;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;

    private final ItemRepositoryDb itemRepository;

    private final UserRepositoryDb userRepository;

    private final ItemRequestMapper mapper;

    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto add(final Long userId, final ItemRequestCreationDto itemRequestCreationDto) {
        ItemRequest item = mapper.toItemRequest(itemRequestCreationDto);
        item.setRequestor(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User Not found!")));
        return mapper.toItemRequestDto(itemRequestRepository.save(item));
    }

    @Override
    public List<ItemRequestDto> getAllMyRequest(final Long userId, final Integer from, final Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not found!");
        }

        Pageable pageable = PageRequest.of(from == 0 ? 0 : from / size, size).withSort(Sort.by("created").descending());
        Page<ItemRequestDto> requests = itemRequestRepository
                .findAllByRequestorId(userId, pageable)
                .map(this::composeItemsWithRequest);
        return requests.stream().collect(Collectors.toList());
    }

    private ItemRequestDto composeItemsWithRequest(final ItemRequest itemRequest) {
        var itemRequestDto  = mapper.toItemRequestDto(itemRequest);
        var items = itemRepository.findAllByRequestRequestId(itemRequest.getRequestId())
                .stream()
                .map(itemMapper::toItemForRequest)
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            itemRequestDto.setItems(List.of());
        }
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getAll(final Long userId, final Integer from, final Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User Not found!");
        }
        Pageable pageable = PageRequest.of(from == 0 ? 0 : from / size, size).withSort(Sort.by("created").descending());
        var requests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable)
                .map(this::composeItemsWithRequest)
                .stream()
                .collect(Collectors.toList());
        return requests;

    }

    @Override
    public ItemRequestDto getById(final Long userId, final Long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
        var itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException(""));
        return composeItemsWithRequest(itemRequest);
    }
}
