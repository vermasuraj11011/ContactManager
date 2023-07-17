package com.contactManager.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
public class PdfService {

    private Logger LOGGER = LoggerFactory.getLogger(PdfService.class);

    public ByteArrayInputStream createPdf() {
        LOGGER.info("starting creating pdf..");
        String title = "Welcome to pdf creation";
        String content = "Hi my name is suraj verma and i am i crating a pdf";
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE, 20);
        Paragraph titlePara = new Paragraph(title, titleFont);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        document.add(titlePara);

        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 12);
        Paragraph contentPara = new Paragraph(content, contentFont);
        contentPara.add(new Chunk("I am adding new text"));
        contentPara.setAlignment(Element.ALIGN_CENTER);
        document.add(contentPara);

        document.close();
        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }
}
