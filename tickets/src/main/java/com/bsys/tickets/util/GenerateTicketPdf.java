package com.bsys.tickets.util;

import com.bsys.tickets.domain.dto.ReservationPaidConsumerDTO;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Component
public class GenerateTicketPdf {

    public byte[] generateTicketPdf(ReservationPaidConsumerDTO ticket, byte[] qrImageBytes) throws Exception {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A6);
            document.addPage(page);

            PDPageContentStream content = new PDPageContentStream(document, page);

            float yPosition = 380;

            // Título
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(50, yPosition);
            content.showText("EVENTO XYZ");
            content.endText();

            yPosition -= 30;

            // Nome
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(50, yPosition);
            content.showText("Nome: " + ticket.customer().name());
            content.endText();

            yPosition -= 15;

            // Ticket ID
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(50, yPosition);
            content.showText("Ticket ID: " + ticket.reservationId());
            content.endText();

            yPosition -= 15;

            // Data
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 10);
            content.newLineAtOffset(50, yPosition);
            content.showText("Data: " + ticket.formatedDateHour());
            content.endText();

            // QR Code
            PDImageXObject qrImage = PDImageXObject.createFromByteArray(
                    document,
                    qrImageBytes,
                    "qr"
            );

            content.drawImage(qrImage, 60, 80, 150, 150);

            content.close();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            document.save(baos);

            return baos.toByteArray();
        }
    }
}