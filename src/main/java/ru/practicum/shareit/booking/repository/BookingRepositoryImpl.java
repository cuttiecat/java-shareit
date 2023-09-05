package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepositoryImpl extends JpaRepository<Booking, Long> {

    @Query(
            "FROM Booking AS b " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllByBookerId(@Param("bookerId") Long bookerId, Pageable pageable); // Booker ALL

    @Query(
            "FROM Booking AS b " +
            "WHERE b.start < :currentTime AND b.end > :currentTime AND b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllCurrentByBookerId(@Param("bookerId") Long bookerId,
                                           @Param("currentTime") LocalDateTime currentTime,
                                           Pageable pageable); // Booker CURRENT

    @Query(
            "FROM Booking AS b " +
            "WHERE b.end < :currentTime AND b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllPastByBookerId(@Param("bookerId") Long bookerId,
                                        @Param("currentTime") LocalDateTime currentTime,
                                        Pageable pageable); // Booker PAST

    @Query(
            "FROM Booking AS b " +
            "WHERE b.start > :currentTime AND b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllFutureByBookerId(@Param("bookerId") Long bookerId,
                                          @Param("currentTime") LocalDateTime currentTime,
                                          Pageable pageable); // Booker FUTURE

    @Query(
            "FROM Booking AS b " +
            "WHERE b.status = 'WAITING' AND b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllWaitingByBookerId(@Param("bookerId") Long bookerId,
                                           Pageable pageable); // Booker WAITING

    @Query(
            "FROM Booking AS b " +
            "WHERE b.status = 'REJECTED' AND b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllRejectedByBookerId(@Param("bookerId") Long bookerId,
                                            Pageable pageable); // Booker REJECTED

    @Query(
            "FROM Booking AS b " +
            "WHERE b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllByOwnerId(@Param("ownerId") Long ownerId,
                                   Pageable pageable); // Owner ALL

    @Query(
            "FROM Booking AS b " +
            "WHERE b.start < :currentTime AND b.end > :currentTime AND b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllCurrentByOwnerId(@Param("ownerId") Long ownerId,
                                          @Param("currentTime") LocalDateTime currentTime,
                                          Pageable pageable); // Owner CURRENT

    @Query(
            "FROM Booking AS b " +
            "WHERE b.end < :currentTime AND b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllPastByOwnerId(@Param("ownerId") Long ownerId,
                                       @Param("currentTime") LocalDateTime currentTime,
                                       Pageable pageable); // Owner PAST

    @Query(
            "FROM Booking AS b " +
            "WHERE b.start > :currentTime AND b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllFutureByOwnerId(@Param("ownerId") Long ownerId,
                                         @Param("currentTime") LocalDateTime currentTime,
                                         Pageable pageable); // Owner FUTURE

    @Query(
            "FROM Booking AS b " +
            "WHERE b.status = 'WAITING' AND b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllWaitingByOwnerId(@Param("ownerId") Long ownerId,
                                          Pageable pageable); // Owner WAITING

    @Query(
            "FROM Booking AS b " +
            "WHERE b.status = 'REJECTED' AND b.item.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    Page<Booking> findAllRejectedByOwnerId(@Param("ownerId") Long ownerId,
                                           Pageable pageable); // Owner REJECTED

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemDto(b1.id, b1.item.id, b1.booker.id) " +
            "FROM Booking AS b1 " +
            "WHERE b1.start = (" +
            "   SELECT MIN(b2.start) " +
            "   FROM Booking AS b2 " +
            "   WHERE b2.item.id = b1.item.id AND b2.start < :currentTime) " +
            "AND b1.item.owner.id = :ownerId " +
            "ORDER BY b1.end DESC")
    List<BookingItemDto> findNearPreviousBookings(@Param("currentTime") LocalDateTime currentTime,
                                                  @Param("ownerId") Long ownerId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemDto(b1.id, b1.item.id, b1.booker.id) " +
            "FROM Booking AS b1 " +
            "WHERE b1.start = (" +
            "   SELECT MIN(b2.start) " +
            "   FROM Booking AS b2 " +
            "   WHERE b2.item.id = b1.item.id AND b2.start > :currentTime) " +
            "AND b1.item.owner.id = :ownerId " +
            "AND b1.status = 'APPROVED' " +
            "ORDER BY b1.start ASC")
    List<BookingItemDto> findNearNextBookings(@Param("currentTime") LocalDateTime currentTime,
                                              @Param("ownerId") Long ownerId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemDto(b.id, b.item.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.start < :currentTime " +
            "AND b.item.id = :itemId " +
            "AND b.item.owner.id = :ownerId " +
            "ORDER BY b.end DESC")
    List<BookingItemDto> findNearPreviousBooking(@Param("currentTime") LocalDateTime currentTime,
                                                 @Param("ownerId") Long ownerId,
                                                 @Param("itemId") Long itemId);

    @Query("SELECT new ru.practicum.shareit.booking.dto.BookingItemDto(b.id, b.item.id, b.booker.id) " +
            "FROM Booking AS b " +
            "WHERE b.start > :currentTime " +
            "AND b.status = 'APPROVED' " +
            "AND b.item.id = :itemId " +
            "AND b.item.owner.id = :ownerId " +
            "ORDER BY b.start ASC")
    List<BookingItemDto> findNearNextBooking(@Param("currentTime") LocalDateTime currentTime,
                                             @Param("ownerId") Long ownerId,
                                             @Param("itemId") Long itemId);
}
