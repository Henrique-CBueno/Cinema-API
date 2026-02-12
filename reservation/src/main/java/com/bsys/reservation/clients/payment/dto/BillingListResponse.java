package com.bsys.reservation.clients.payment.dto;

import java.util.List;

public record BillingListResponse(List<BillingSuccessResponse.Data> data,
                                  String error) {
}
