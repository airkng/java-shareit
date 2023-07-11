package ru.practicum.shareit.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepositoryDb extends JpaRepository<Item, Long> {
    Page<Item> findAllByOwnerId(Long userId, Pageable page);

    @Query("select i from Item i where (lower(i.name) like lower(concat('%', ?1, '%')) "
            + "or lower(i.description) like lower(concat('%', ?1, '%'))) and i.available=true")
    Page<Item> searchByText(String text, Pageable pages);

    List<Item> findAllByRequestRequestId(Long requestId);
}
