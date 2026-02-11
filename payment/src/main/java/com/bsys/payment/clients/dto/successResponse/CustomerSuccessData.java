package com.bsys.payment.clients.dto.successResponse;

public record CustomerSuccessData(CustomerResponseData data,
                                  String error) {

    public record CustomerResponseData(String id,
                                       UserMetadata metadata) {}

    public record UserMetadata(String name,
                               String cellphone,
                               String email,
                               String taxId) {}
}
