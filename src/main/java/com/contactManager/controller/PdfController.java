package com.contactManager.controller;

import com.contactManager.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.ByteArrayInputStream;

@Controller
@RequestMapping("/pdf/")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/createPdf")
    public ResponseEntity<InputStreamResource> createPdf() {
        ByteArrayInputStream pdf = pdfService.createPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                "Content-Disposition", "inline;file=test.pdf"
        );
        return
                ResponseEntity
                        .ok()
                        .headers(headers)
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(new InputStreamResource(pdf));

    }
}
