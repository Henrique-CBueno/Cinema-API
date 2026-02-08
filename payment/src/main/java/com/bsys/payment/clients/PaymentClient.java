package com.bsys.payment.clients;

import com.bsys.payment.config.PaymentClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "Payment-client",
        url = "${integration.payment.url}",
        configuration = PaymentClientConfiguration.class)
public class PaymentClient {
}
