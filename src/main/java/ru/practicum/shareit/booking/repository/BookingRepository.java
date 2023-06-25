package ru.practicum.shareit.booking.repository;

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

    List<Booking> findBookingsByBookerIsOrderByStartDesc(User booker);

    List<Booking> findBookingsByBookerIsAndEndBeforeOrderByStartDesc(User booker,
                                                                    LocalDateTime localDateTime);

    List<Booking> findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(User booker,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end);

    List<Booking> findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(User booker,
                                                                         LocalDateTime localDateTime);

    @Query("SELECT booking FROM Booking booking " +
            "WHERE booking.booker.id = ?1 AND booking.status = ?2 " +
            "ORDER BY booking.status desc ")
    List<Booking> findByBookerAndStatus(long userId, BookingStatus status);

    List<Booking> findBookingsByItemOwnerIsOrderByStartDesc(User owner);

    List<Booking> findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(User owner,
                                                                      LocalDateTime localDateTime);

    List<Booking> findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(User owner,
                                                                                      LocalDateTime start,
                                                                                      LocalDateTime end);

    List<Booking> findBookingsByItemOwnerAndStartAfterOrderByStartDesc(User owner,
                                                                       LocalDateTime localDateTime);

    @Query("SELECT booking FROM Booking booking " +
            "WHERE booking.item.owner.id = ?1 AND booking.status = ?2 " +
            "ORDER BY booking.status desc ")
    List<Booking> findByItemOwnerAndStatus(long ownerId, BookingStatus status);

    List<Booking> findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(long userId, long itemId,
                                                                          BookingStatus status, LocalDateTime end);

    List<Booking> findAllByItemIdAndStatusIsAndStartAfterOrderByStartAsc(long itemId, BookingStatus status,
                                                                         LocalDateTime start);

    List<Booking> findAllByItemIdAndStatusIsAndStartBeforeOrderByStartDesc(long itemId, BookingStatus status,
                                                                           LocalDateTime start);
}
