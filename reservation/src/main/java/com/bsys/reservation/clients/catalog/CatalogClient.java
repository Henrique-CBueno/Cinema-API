package com.bsys.reservation.clients.catalog;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "catalog-client", url = "${clients.catalog.url}")
public interface CatalogClient {
}
