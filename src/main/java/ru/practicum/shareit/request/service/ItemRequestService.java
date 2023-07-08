package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    List<ItemRequestDtoResponse> getAllItemRequestByUser(long userId);

    ItemRequest addItemRequest(long userId, ItemRequestDtoRequire itemRequestDtoRequire);

    ItemRequestDtoResponse getItemRequestById(long userId, long requestId);

    List<ItemRequestDtoResponse> getAllItemRequest(long userId, int from, int size);
}
