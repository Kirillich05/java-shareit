package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findBookingsByBookerIsOrderByStartDesc(User booker, Pageable pageable);

    Page<Booking> findAllByBookerAndEndBeforeOrderByStartDesc(User booker,
                                              LocalDateTime localDateTime,
                                              Pageable pageable);

    Page<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                  LocalDateTime start,
                                                                  LocalDateTime end,
                                                                  Pageable pageable);

    Page<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                        LocalDateTime localDateTime,
                                                                        Pageable pageable);

    @Query("SELECT booking FROM Booking booking " +
            "WHERE booking.booker.id = ?1 AND booking.status = ?2 " +
            "ORDER BY booking.status desc ")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status, Pageable page);

    Page<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner, Pageable pageable);

    Page<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner,
                                                                      LocalDateTime localDateTime,
                                                                      Pageable pageable);

    Page<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end,
                                                                                     Pageable pageable);

    Page<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner,
                                                                       LocalDateTime localDateTime,
                                                                       Pageable pageable);

    @Query("SELECT booking FROM Booking booking " +
            "WHERE booking.item.owner.id = ?1 AND booking.status = ?2 " +
            "ORDER BY booking.status desc ")
    List<Booking> findByItemOwnerAndStatus(long ownerId, BookingStatus status, Pageable page);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(long userId, long itemId,
                                                                          BookingStatus status, LocalDateTime end);

    List<Booking> findAllByItemIdAndStatusIsAndStartAfterOrderByStartAsc(long itemId, BookingStatus status,
                                                                         LocalDateTime start);

    List<Booking> findAllByItemIdAndStatusIsAndStartBeforeOrderByStartDesc(long itemId, BookingStatus status,
                                                                           LocalDateTime start);
}
