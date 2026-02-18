package com.bsys.reservation.publisher.dto;

public record ReservationNotificationDTO(
    String reservationId,
    CustomerDTO customer,
    String movieTitle,
    String sessionTime,
    String status, // e.g., PENDING
    String message
) {}
