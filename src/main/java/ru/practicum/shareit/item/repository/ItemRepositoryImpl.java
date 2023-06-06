package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 0;

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item save(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
        } else {
            item.setId(getId());
            items.put(item.getId(), item);
        }
        return item;
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(items.get(id));
    }

    private long getId() {
        lastId++;
        return lastId;
    }
}
