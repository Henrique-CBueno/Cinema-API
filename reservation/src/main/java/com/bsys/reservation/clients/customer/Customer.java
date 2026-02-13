package com.bsys.reservation.clients.customer;

public record Customer (String id,
                        String name,
                        String phone,
                        String email,
                        String taxId) {
}
