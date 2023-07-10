package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequire;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @GetMapping
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting all item requests by user {}", userId);
        return itemRequestClient.getAllItemRequestByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @Valid @RequestBody ItemRequestDtoRequire itemRequestDtoRequire) {
        log.info("Posting item request");
        return itemRequestClient.addItemRequest(userId, itemRequestDtoRequire);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @PathVariable long requestId) {
        log.info("Getting item request {}", requestId);
        return itemRequestClient.getItemRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Getting all item requests");
        return itemRequestClient.getAllItemRequest(userId, from, size);
    }
}
