package com.bsys.reservation.publisher.dto;

public record CustomerDTO(String name,
                          String taxId,
                          String email,
                          String phone) {
}
