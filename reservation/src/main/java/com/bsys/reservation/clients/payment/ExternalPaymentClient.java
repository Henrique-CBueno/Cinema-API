package com.bsys.reservation.clients.payment;

import com.bsys.reservation.clients.payment.dto.BillingListResponse;
import com.bsys.reservation.config.ExternalPaymentClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "external-payment-client",
		url = "${integration.payment.url}",
		configuration = ExternalPaymentClientConfiguration.class)
public interface ExternalPaymentClient {

	@GetMapping("/billing/list")
	BillingListResponse listBillings();
}
