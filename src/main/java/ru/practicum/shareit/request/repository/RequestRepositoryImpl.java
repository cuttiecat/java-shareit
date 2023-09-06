package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepositoryImpl extends JpaRepository<Request, Long> {
    @Query("SELECT r " +
            "FROM Request AS r " +
            "WHERE r.requestor.id = :requestorId " +
            "ORDER BY r.created DESC")
    List<Request> findRequestsByRequestorId(@Param("requestorId") Long requestorId);

    @Query("SELECT r " +
            "FROM Request AS r " +
            "WHERE r.requestor.id <> :requestorId")
    Page<Request> findAllForUser(@Param("requestorId") Long requestorId, Pageable pageable);
}