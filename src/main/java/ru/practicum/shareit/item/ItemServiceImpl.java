package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.UserAccessException;
import ru.practicum.shareit.item.comments.Comment;
import ru.practicum.shareit.item.comments.CommentDto;
import ru.practicum.shareit.item.comments.CommentMapper;
import ru.practicum.shareit.item.comments.CommentRepository;
import ru.practicum.shareit.item.dto.ItemCreationDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepositoryDb;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper mapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final UserService userService;
    private final ItemRepositoryDb itemRepository;
    private final UserRepositoryDb userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto get(final Long itemId, final Long userId) {
        ItemDto itemDto = mapper.toItemDto(itemRepository.findById(itemId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Item with user id = %d and item id = %d not found", userId, itemId));
        }));
        itemDto = setBookings(itemDto, userId);
        itemDto.setComments(getComments(itemId));
        return itemDto;
    }

    private List<CommentDto> getComments(final Long itemId) {
        return commentRepository.findByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private ItemDto setBookings(final ItemDto itemDto, final Long userId) {
        if (itemDto.getOwner().getId().equals(userId)) {
            var bookings = bookingRepository.findByItemId(itemDto.getId(), Sort.by(Sort.Direction.DESC, "start"));

            itemDto.setLastBooking(bookings.stream()
                    .filter((booking) -> booking.getStart().isBefore(LocalDateTime.now()))
                    .max(Comparator.comparing(Booking::getEnd))
                    .map(bookingMapper::toItemBookingDto)
                    .orElse(null)
            );

            itemDto.setNextBooking(bookings.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED) && booking.getStart().isAfter(LocalDateTime.now()))
                    .map(bookingMapper::toItemBookingDto)
                    //либо max
                    .min(Comparator.comparing(BookingItemDto::getStart))
                    .orElse(null));
            return itemDto;
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getAll(final Long userId) {
        var items = itemRepository.findAllByOwnerId(userId).stream()
                .sorted(Comparator.comparingLong(Item::getId))
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
        items.forEach((itemDto) -> {
            itemDto.setComments(getComments(itemDto.getId()));
            setBookings(itemDto, userId);
        });
        return items;
    }

    @Override
    public List<ItemDto> search(final String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.searchByText(text).stream()
                .map(mapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(final Long userId, final Long itemId, final CommentDto commentDto) {
        //refactor mapper
        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(userRepository.findById(userId).orElseThrow());
        comment.setItem((itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"))));
        if (!bookingRepository.existsByBookerIdAndEndBeforeAndStatus(userId, LocalDateTime.now(), BookingStatus.APPROVED)) {
            throw new NotAvailableException("You cant comment before use!");
        }

        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public ItemDto create(final ItemCreationDto itemCreationDto) {
        Item item = mapper.toItem(itemCreationDto);
        User owner = userRepository.findById(item.getOwner().getId()).orElseThrow(() -> {
            throw new NotFoundException(String.format("Item with user id = %d ", item.getOwner().getId()));
        });
        item.setOwner(owner);
        return mapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(final ItemCreationDto itemCreationDto, final Long itemId) {
        userService.get(itemCreationDto.getUserId());
        Item itemInfo = mapper.toItem(itemCreationDto);
        Optional<Item> optionalOldItem = itemRepository.findById(itemId);
        if (optionalOldItem.isPresent()) {
            Item oldItem = optionalOldItem.get();
            if (!oldItem.getOwner().getId().equals(itemInfo.getOwner().getId())) {
                throw new UserAccessException(
                        String.format("User with user id = %d has not access to update item with item id = %d",
                                itemInfo.getOwner().getId(), itemId));
            }
            if (itemInfo.getAvailable() != null) {
                oldItem.setAvailable(itemInfo.getAvailable());
            }
            if (itemInfo.getName() != null) {
                oldItem.setName(itemInfo.getName());
            }
            if (itemInfo.getDescription() != null) {
                oldItem.setDescription(itemInfo.getDescription());
            }
            return mapper.toItemDto(itemRepository.save(oldItem));
        } else {
            throw new NotFoundException(String.format("Item with item id = %s not found", itemId));
        }
    }

}
