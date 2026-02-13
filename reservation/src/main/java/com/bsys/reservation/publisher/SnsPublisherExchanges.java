package com.bsys.reservation.publisher;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SnsPublisherExchanges {

    public static String RESERVATION_PAID_EXCHANGE;
    public static String NOTIFICATION_TOPIC_EXCHANGE;

    @Value("${sns.exchanges.reservation_paid}")
    public void setReservationPaidExchange(String value) {
        RESERVATION_PAID_EXCHANGE = value;
    }

    @Value("${sns.exchanges.notification-topic}")
    public void setNotificationTopicExchange(String value) {
        NOTIFICATION_TOPIC_EXCHANGE = value;
    }
}
