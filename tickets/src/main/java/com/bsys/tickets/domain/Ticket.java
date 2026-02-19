package com.bsys.tickets.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tickets")
public class Ticket {

    @Id
    private String id;
    private String reservationId;
    private String customerName;
    private String movieTitle;
    private String seat;
    private String cinemaName;
    private String roomName;
    private String showTime;
    private BigDecimal price;
    private String qrCode; 
    private LocalDateTime generatedAt;
    private String s3Url;
}
