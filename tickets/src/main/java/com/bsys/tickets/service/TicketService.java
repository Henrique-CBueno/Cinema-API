package com.bsys.tickets.service;

import com.bsys.tickets.domain.Ticket;
import com.bsys.tickets.domain.dto.ReservationPaidConsumerDTO;
import com.bsys.tickets.repository.TicketRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketTokenService ticketTokenService;
    private final S3Service s3Service;
    private final TicketRepository ticketRepository;

    public void generateTicket(ReservationPaidConsumerDTO reservation) {
        log.info("Generating ticket for reservation: {}", reservation.reservationId());

        Ticket ticket = mapToTicket(reservation);
        String token = ticketTokenService.generateToken(reservation);
        
        try {
            byte[] qrCodeImage = generateQRCode(token);
            byte[] pdfTicket = generatePdfTicket(ticket, qrCodeImage);
            
            // Upload to S3
            String fileName = "ticket-" + reservation.reservationId() + ".pdf";
            String s3Url = s3Service.uploadFile(pdfTicket, fileName);
            ticket.setS3Url(s3Url);

            // Save to MongoDB
            ticketRepository.save(ticket);
            
            log.info("Ticket generated and saved successfully for reservation: {}. S3 URL: {}", reservation.reservationId(), s3Url);
            
        } catch (Exception e) {
            log.error("Error generating ticket for reservation: {}", reservation.reservationId(), e);
            throw new RuntimeException("Error generating ticket", e);
        }
    }

    private Ticket mapToTicket(ReservationPaidConsumerDTO dto) {
        return Ticket.builder()
                .id(UUID.randomUUID().toString())
                .reservationId(dto.reservationId())
                .customerName(dto.customer().name())
                .movieTitle(dto.movieName())
                .seat(dto.seat())
                .cinemaName(dto.cinemaName())
                .roomName(dto.roomName())
                .showTime(dto.formatedDateHour())
                .price(dto.value())
                .generatedAt(LocalDateTime.now())
                .build();
    }

    private byte[] generateQRCode(String text) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
        
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    private byte[] generatePdfTicket(Ticket ticket, byte[] qrCodeImage) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                
                // Add Text
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 750);
                contentStream.showText("Cinema Ticket");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 720);
                
                contentStream.showText("Movie: " + ticket.getMovieTitle());
                contentStream.newLine();
                contentStream.showText("Cinema: " + ticket.getCinemaName());
                contentStream.newLine();
                contentStream.showText("Room: " + ticket.getRoomName());
                contentStream.newLine();
                contentStream.showText("Seat: " + ticket.getSeat());
                contentStream.newLine();
                contentStream.showText("Time: " + ticket.getShowTime());
                contentStream.newLine();
                contentStream.showText("Customer: " + ticket.getCustomerName());
                contentStream.newLine();
                contentStream.showText("Price: " + ticket.getPrice());
                contentStream.endText();

                // Add QR Code
                PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, qrCodeImage, "QR Code");
                contentStream.drawImage(pdImage, 400, 650, 150, 150);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
