package com.contactManager.config;

import com.contactManager.entities.EmailMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Component
public class Util {
    private final Logger LOGGER = LoggerFactory.getLogger(EmailMessage.class.getName());

    public String readFileAsString(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
            LOGGER.info("Successfully read the file input");
        } catch (Exception e) {
            LOGGER.error("Failed to read the file " + e.getMessage());
        }
        return contentBuilder.toString();
    }
}
