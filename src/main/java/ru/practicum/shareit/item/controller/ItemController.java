package ru.practicum.shareit.item.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Getting items list");
        return service.getItemsByOwner(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@PathVariable("id") long id) {
        log.info("Getting item by id " + id);
        return ItemMapper.toItemDto(service.getItemById(id));
    }

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @Valid @RequestBody ItemDto itemDto) {
        log.info("Posting item");
        var item = service.addItem(itemDto, userId);

        return ItemMapper.toItemDto(item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("id") long itemId,
            @Valid @RequestBody ItemDto itemDto
    ) {
        log.info("Updating item by id " + itemId + " by user id " + userId);
        var item = service.updateItem(itemId, itemDto, userId);
        return ItemMapper.toItemDto(item);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Searching " + text);
        return service.search(text);
    }
}
