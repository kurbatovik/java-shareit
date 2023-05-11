package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerOrderByIdAsc(User user, Pageable pageable);

    @Query("SELECT item FROM Item item " +
            "WHERE (LOWER(item.name) LIKE LOWER(CONCAT('%',?1,'%')) " +
            "OR LOWER(item.description) LIKE LOWER(CONCAT('%',?1,'%'))) AND item.available=TRUE")
    Page<Item> findLikingByNameOrDescription(String text, Pageable pageable);

    @Query("SELECT item, item.owner.id FROM Item item WHERE item.requestId = ?1")
    List<Item> findAllByRequestOrderByIdAsc(long requestId);
}
