package ru.practicum.shareit.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestService service;

    @GetMapping
    public List<ItemRequestDtoResponse> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all item requests");
        return service.getAllItemRequestByUser(userId);
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @Valid @RequestBody ItemRequestDtoRequire itemRequestDtoRequire) {
        log.info("Posting new item requests");
        var itemRequest = service.addItemRequest(userId, itemRequestDtoRequire);
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoResponse getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        log.info("Getting item requests by id " + requestId);
        return service.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoResponse> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "0") int from,
                                                          @RequestParam(defaultValue = "10") int size) {
        return service.getAllItemRequest(userId, from, size);
    }
}
