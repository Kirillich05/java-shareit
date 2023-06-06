package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    List<Item> getItemsByOwner(long ownerId);

    Item getItemById(long id);

    Item addItem(ItemDto itemDto, long userId);

    Item updateItem(long id, ItemDto itemDto, long userId);

    List<ItemDto> search(String text);
}
