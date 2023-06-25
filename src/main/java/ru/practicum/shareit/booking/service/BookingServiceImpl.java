package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserService userService;

    @Transactional
    @Override
    public Booking addBooking(BookingDtoRequest bookingDtoRequest, long bookerId) {
        if (!isValidBooking(bookingDtoRequest)) {
            throw new BadRequestException("Booking data is not valid");
        }

        var item = itemRepo.findById(bookingDtoRequest.getItemId()).orElseThrow(
                    () -> new NotFoundException("Item by id  was not found"));
        User booker = userService.getUserById(bookerId);

        if (item.getOwner().getId() == bookerId) throw new NotFoundException("Item " + item.getId() +
                " can not be booked by owner");
        if (!item.getAvailable()) throw new BadRequestException("Item " + item.getId() +
                " is not available for booking");
        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BadRequestException("Booking time error");
        }

        bookingDtoRequest.setStatus(BookingStatus.WAITING);
        var booking = BookingMapper.toBooking(bookingDtoRequest, booker, item);
        return bookingRepo.save(booking);
    }

    @Override
    public BookingDtoResponse getBookingById(long userId, long bookingId) {
        Booking booking = findOrThrow(bookingId);
        Item item = booking.getItem();
        if (item.getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else {
            throw new NotFoundException("User " + userId + " does not have permissions");
        }
    }

    @Transactional
    @Override
    public BookingDtoResponse approveBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = findOrThrow(bookingId);
        Item item = booking.getItem();

        if (userId != item.getOwner().getId()) throw new NotFoundException("User " + userId +
                " can not change booking status");
        if (booking.getStatus() == BookingStatus.APPROVED) throw new BadRequestException("Status was approved");

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toBookingDtoResponse(bookingRepo.save(booking));
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByUser(long userId, String state) {
        User user = userService.getUserById(userId);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepo.findBookingsByBookerIsOrderByStartDesc(user));
                break;
            case "PAST":
                bookings.addAll(bookingRepo.findBookingsByBookerIsAndEndBeforeOrderByStartDesc(user,
                        LocalDateTime.now()));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepo.findBookingsByBookerIsAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepo.findBookingsByBookerIsAndStartIsAfterOrderByStartDesc(user,
                        LocalDateTime.now()));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepo.findByBookerAndStatus(user.getId(), BookingStatus.REJECTED));
                break;
            case "WAITING":
                bookings.addAll(bookingRepo.findByBookerAndStatus(user.getId(), BookingStatus.WAITING));
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByOwner(long userId, String state) {
        User user = userService.getUserById(userId);
        List<Booking> bookings = new ArrayList<>();

        switch (state) {
            case "ALL":
                bookings.addAll(bookingRepo.findBookingsByItemOwnerIsOrderByStartDesc(user));
                break;
            case "PAST":
                bookings.addAll(bookingRepo.findBookingsByItemOwnerAndEndBeforeOrderByStartDesc(user,
                        LocalDateTime.now()));
                break;
            case "CURRENT":
                bookings.addAll(bookingRepo.findBookingsByItemOwnerIsAndStartBeforeAndEndAfterOrderByStartDesc(user,
                        LocalDateTime.now(), LocalDateTime.now()));
                break;
            case "FUTURE":
                bookings.addAll(bookingRepo.findBookingsByItemOwnerAndStartAfterOrderByStartDesc(user,
                        LocalDateTime.now()));
                break;
            case "REJECTED":
                bookings.addAll(bookingRepo.findByItemOwnerAndStatus(user.getId(), BookingStatus.REJECTED));
                break;
            case "WAITING":
                bookings.addAll(bookingRepo.findByItemOwnerAndStatus(user.getId(), BookingStatus.WAITING));
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }


    private Booking findOrThrow(long id) {
        return bookingRepo
                .findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Booking by id " + id + " was not found")
                );
    }

    private boolean isValidBooking(BookingDtoRequest bookingDtoRequest) {
        return (bookingDtoRequest.getStart() != null && bookingDtoRequest.getEnd() != null) &&
                (!bookingDtoRequest.getStart().equals(bookingDtoRequest.getEnd())) &&
                (!bookingDtoRequest.getStart().isBefore(LocalDateTime.now())) &&
                (!bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) &&
                (!bookingDtoRequest.getEnd().isBefore(LocalDateTime.now()));
    }
}
