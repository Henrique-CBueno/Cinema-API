package com.bsys.reservation.webhook;

import com.bsys.reservation.infra.constants.ExceptionConstants;
import com.bsys.reservation.infra.exceptions.WebhookSecretNotValid;
import com.bsys.reservation.webhook.dto.req.PaidDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class ReservationPaidWebhook {

    @Value("${webhook.secret}")
    private String appWebhookSecret;

    @Value("${webhook.publicKey}")
    private String publicKey;

    private final ObjectMapper objectMapper;
    private final WebhookService webhookService;

    @PostMapping("webhook/paid")
    public ResponseEntity<Void> webhookPagamento(@RequestBody String rawBody,
                                                 @RequestParam String webhookSecret,
                                                 @RequestHeader("X-Webhook-Signature") String signatureHeader) throws JsonProcessingException {

        if (!isValidSignature(rawBody, signatureHeader) ||
                !Objects.equals(webhookSecret, appWebhookSecret))
                    throw new WebhookSecretNotValid(ExceptionConstants.WEBHOOK_SECRET_NOT_VALID);

        PaidDTO payload = objectMapper.readValue(rawBody, PaidDTO.class);

        webhookService.handleRawPayment(payload.data().billing().products().get(0).externalId());

        return ResponseEntity.ok().build();
    }


    private boolean isValidSignature(String payload, String providedSignature) {
        try {

            byte[] keyBytes = publicKey.getBytes(StandardCharsets.UTF_8);

            SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

            String calculatedSignature = Base64.getEncoder().encodeToString(hmacBytes);
            return calculatedSignature.equals(providedSignature);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new WebhookSecretNotValid(ExceptionConstants.WEBHOOK_SECRET_NOT_VALID);
        }
        }
}
