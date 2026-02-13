package com.bsys.tickets.domain.dto;

public record CustomerDTO(String name,
                          String taxId,
                          String email,
                          String phone) {
}
