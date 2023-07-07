package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemRequestDtoResponse> getAllItemRequestByUser(long userId) {
        userService.getUserById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findItemRequestsByRequestorIdOrderByCreatedAsc(userId);

        List<ItemRequestDtoResponse> itemRequestDtoResponseList = itemRequestList.stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());
        itemRequestDtoResponseList.forEach(this::setItems);
        return itemRequestDtoResponseList;
    }

    @Transactional
    @Override
    public ItemRequest addItemRequest(long userId, ItemRequestDtoRequire itemRequestDtoRequire) {
        if (itemRequestDtoRequire.getDescription() == null) {
            throw new BadRequestException("Item request description is not  be empty");
        }

        var requestor = userService.getUserById(userId);
        var itemRequest = ItemRequestMapper.toItemRequest(itemRequestDtoRequire, requestor);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestDtoResponse getItemRequestById(long userId, long requestId) {
        userService.getUserById(userId);
        var itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request by id " + requestId + " was not found"));
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.toItemRequestDtoResponse(itemRequest);
        setItems(itemRequestDtoResponse);
        return itemRequestDtoResponse;
    }

    @Override
    public List<ItemRequestDtoResponse> getAllItemRequest(long userId, int from, int size) {
        if (from < 0 || size <= 0) {
            throw new BadRequestException("From and size must not be null");
        }

        userService.getUserById(userId);
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemRequestDtoResponse> itemRequestDtoResponseList = itemRequestRepository
                .findAllByRequestorIdIsNot(userId, page)
                .stream()
                .map(ItemRequestMapper::toItemRequestDtoResponse)
                .collect(Collectors.toList());
        itemRequestDtoResponseList.forEach(this::setItems);
        return itemRequestDtoResponseList;
    }

    private void setItems(ItemRequestDtoResponse itemRequestDtoResponseList) {
        itemRequestDtoResponseList.setItems(itemRepository.findAllByRequestId(itemRequestDtoResponseList.getId())
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList()));
    }
}
