package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getItemsByOwner(long ownerId);

    ItemBookingDto getItemById(long userId, long itemId);

    Item addItem(ItemDto itemDto, long userId);

    Item updateItem(long id, ItemDto itemDto, long userId);

    List<ItemDto> search(String text);

    List<ItemBookingDto> getAllItems(long userId);

    Comment addComment(long userId, long itemId, CommentDto commentDto);
}
