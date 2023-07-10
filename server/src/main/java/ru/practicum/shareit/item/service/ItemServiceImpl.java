package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepo;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Override
    public List<ItemBookingDto> getAllItems(long userId, int from, int size) {
        userService.getUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepo.findAllByOwnerId(userId, page)
                .stream()
                .sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::toItemBookingDto)
                .map(this::setItemBookingWithComments)
                .collect(Collectors.toList());
    }

    @Override
    public ItemBookingDto getItemById(long userId, long itemId) {
        var item = findOrThrow(itemId);
        ItemBookingDto itemBookingDto = ItemMapper.toItemBookingDto(item);
        itemBookingDto.setComments(commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (item.getOwner().getId() == userId) {
            setItemBookingWithComments(itemBookingDto);
        }
        return itemBookingDto;
    }

    @Transactional
    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        if (isBadItem(itemDto)) {
            throw new BadRequestException("Empty fields in item");
        }

        var owner = userService.getUserById(userId);
        var item = ItemMapper.toItem(itemDto, owner);

        if (itemDto.getRequestId() != null) {
            var itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Not found item request id " + itemDto.getRequestId()));
            item.setRequest(itemRequest);
        }

        return itemRepo.save(item);
    }

    @Transactional
    @Override
    public Item updateItem(long itemId, ItemDto itemDto, long userId) {
        var item = findOrThrow(itemId);

        if (item.getOwner().getId() != userId) {
            throw new NotFoundException("This user is not owner of the item");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return itemRepo.save(item);
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);

        return itemRepo.search(text, page)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Comment addComment(long userId, long itemId, CommentDto commentDto) {
        if (commentDto.getText() == null || commentDto.getText().isBlank()) {
            throw new BadRequestException("There is no text for comment");
        }
        var user = userService.getUserById(userId);
        Item item = findOrThrow(itemId);
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId,
                        BookingStatus.APPROVED,LocalDateTime.now())
                        .isEmpty()) {
                                throw new BadRequestException("Not allowed for user " + user.getId());
        }
        commentDto.setCreated(LocalDateTime.now());
        var comment = CommentMapper.toComment(item, commentDto, user);
        return commentRepository.save(comment);
    }


    private Item findOrThrow(long id) {
        return itemRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Item by id " + id + " was not found")
                );
    }

    private boolean isBadItem(ItemDto itemDto) {
        return itemDto.getName() == null ||
                itemDto.getDescription() == null ||
                itemDto.getAvailable() == null ||
                itemDto.getName().isBlank();
    }

    private ItemBookingDto setItemBookingWithComments(ItemBookingDto itemBookingDto) {
        var nextBooking = bookingRepository.findAllByItemIdAndStatusIsAndStartAfterOrderByStartAsc(
                itemBookingDto.getId(), BookingStatus.APPROVED, LocalDateTime.now()).isEmpty() ? null :
                BookingMapper.toBookingItemDto(bookingRepository.findAllByItemIdAndStatusIsAndStartAfterOrderByStartAsc(
                        itemBookingDto.getId(), BookingStatus.APPROVED, LocalDateTime.now()).get(0));
        itemBookingDto.setNextBooking(nextBooking);

        var lastBooking = bookingRepository.findAllByItemIdAndStatusIsAndStartBeforeOrderByStartDesc(
                itemBookingDto.getId(), BookingStatus.APPROVED, LocalDateTime.now()).isEmpty() ? null :
                BookingMapper.toBookingItemDto(bookingRepository.findAllByItemIdAndStatusIsAndStartBeforeOrderByStartDesc(
                        itemBookingDto.getId(), BookingStatus.APPROVED, LocalDateTime.now()).get(0));
        itemBookingDto.setLastBooking(lastBooking);

        itemBookingDto.setComments(commentRepository.findAllByItemId(itemBookingDto.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));

        return itemBookingDto;
    }
}
