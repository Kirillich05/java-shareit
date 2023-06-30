package ru.practicum.shareit.booking.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse addBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                        @Valid @RequestBody BookingDtoRequest bookingDtoRequest) {
        var booking = bookingService.addBooking(bookingDtoRequest, userId);
        log.info("Posting booking");
        return BookingMapper.toBookingDtoResponse(booking);
    }


    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long bookingId) {
        log.info("Getting booking " + bookingId);
        return bookingService.getBookingById(userId, bookingId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse approveBookingStatus(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long bookingId,
                                                   @RequestParam boolean approved) {
        log.info("PatchMapping booking " + bookingId + " approved = " + approved);
        return bookingService.approveBookingStatus(userId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestParam(defaultValue = "ALL", required = false)
                                                         String state) {
        log.info("Getting all booking by user " + userId);
        return bookingService.getAllBookingsByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsByOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                          @RequestParam(defaultValue = "ALL", required = false)
                                                          String state) {
        log.info("Getting all booking by owner " + userId);
        return bookingService.getAllBookingsByOwner(userId, state);
    }
}
