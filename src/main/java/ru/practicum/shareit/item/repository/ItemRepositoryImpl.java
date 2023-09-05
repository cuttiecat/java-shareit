package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepositoryImpl extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND i.available = true")
    Page<Item> findAllByText(@Param("text") String text, Pageable pageable);

    @Query("SELECT i " +
            "FROM Item AS i " +
            "WHERE i.request.id IN :requestsId")
    List<Item> findAllByRequestsId(@Param("requestsId") List<Long> requestsId);
}