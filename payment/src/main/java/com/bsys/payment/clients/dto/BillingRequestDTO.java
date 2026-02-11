package com.bsys.payment.clients.dto;

import com.bsys.payment.clients.dto.enums.PaymentsMethods;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record BillingRequestDTO(@NotBlank
                                String frequency, // Ex: "ONE_TIME"

                                @NotEmpty
                                List<PaymentsMethods> methods, // Ex: ["PIX"]

                                @NotEmpty @Valid
                                List<Product> products,

                                @NotNull @Min(60)
                                Integer expiresIn, // Tempo em SEGUNDOS (Ex: 3600 para 1 hora)

                                @NotBlank @Pattern(regexp = "https?://.*")
                                String returnUrl,

                                @NotBlank @Pattern(regexp = "https?://.*")
                                String completionUrl,

                                String customerId,

                                UUID externalId) {

    public record Product(
            String externalId,

            @NotBlank
            String name,

            String description,

            @Min(1)
            int quantity,

            @Min(100)
            int price // Em centavos (Ex: 2000 = R$ 20,00)
    ) {}
}
