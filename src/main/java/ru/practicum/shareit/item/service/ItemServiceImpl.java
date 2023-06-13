package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepo;
    private final UserService userService;

    @Override
    public List<Item> getItemsByOwner(long ownerId) {
        return itemRepo.findAll()
                .stream()
                .filter(item -> item.getOwner() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(long id) {
        return findOrThrow(id);
    }

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        var owner = userService.getUserById(userId);
        var item = ItemMapper.toItem(itemDto, owner.getId());
        if (isBadItem(itemDto)) {
            throw new BadRequestException("Empty fields in item");
        }

        return itemRepo.save(item);
    }

    @Override
    public Item updateItem(long itemId, ItemDto itemDto, long userId) {
        var item = findOrThrow(itemId);

        if (item.getOwner() != userId) {
            throw new NotFoundException("This user is not owner of the item");
        }

        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());

        return itemRepo.save(item);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        String textToLower = text.toLowerCase();
        return itemRepo.findAll()
                .stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textToLower) ||
                        item.getDescription().toLowerCase().contains(textToLower))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
}
