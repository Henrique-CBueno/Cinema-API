package com.bsys.reservation.webhook.dto.req;

import java.util.List;
import java.util.Map;

public record PaidDTO(String id,
                      WebhookData data,
                      boolean devMode,
                      String event
) {}

