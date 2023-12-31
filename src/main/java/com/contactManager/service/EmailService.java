package com.contactManager.service;

import com.contactManager.config.Constant;
import com.contactManager.config.Util;
import com.contactManager.payload.EmailMessage;
import com.contactManager.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class EmailService {

    @Value("${email.password}")
    String password;

    @Value("${email.user_name}")
    String userName;

    @Autowired
    private Util util;

    private final Logger LOGGER = LoggerFactory.getLogger(EmailMessage.class.getName());

    public Boolean sendMail(
            User user,
            EmailMessage message,
            List<MultipartFile> attachments,
            List<String> attachmentPath,
            String templatePath
    ) {
        LOGGER.info("preparing to send email.....");

//        get the system properties
        Properties properties = System.getProperties();

//        set properties
        properties.put("mail.smtp.host", Constant.SMTP_HOST_NAME);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

//        Step:1 get the session object
        Session session = javax.mail.Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        session.setDebug(true);

//        step:2 compose the message [text, multi media]
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(userName);
            mimeMessage.addRecipients(Message.RecipientType.TO, message.getTo());
            mimeMessage.setSubject(message.getSubject());

            MimeMultipart mimeMultipart = new MimeMultipart();

            MimeBodyPart bodyPart = new MimeBodyPart();
            String htmlContent = null;
            if (templatePath != null) {
                htmlContent = this.util.readFileAsString(templatePath).replace("***username***", user.getName());
            } else {
                htmlContent = "<p>" + message.getMessage() + "</p>";
            }
            bodyPart.setContent(htmlContent, "text/html");
            mimeMultipart.addBodyPart(bodyPart);

//            adding multiple attachment
            List<MimeBodyPart> mimeBodyParts = addAttachments(attachments, attachmentPath);
            if (!mimeBodyParts.isEmpty()) {
                mimeBodyParts.forEach(bodyPart1 -> {
                    try {
                        mimeMultipart.addBodyPart(bodyPart1);
                    } catch (MessagingException e) {
                        LOGGER.error("email sent failed for user " + user.getEmail() + " with message : " + e.getMessage());
                    }
                });
            }

            mimeMessage.setContent(mimeMultipart);

//            sending mail
            Transport.send(mimeMessage);

            LOGGER.info("Email sent to " + user.getName() + " successfully");

        } catch (Exception e) {
            LOGGER.error("email sent failed for user " + user.getEmail() + " with message : " + e.getMessage());
            return false;
        }
        return true;
    }

    private List<MimeBodyPart> addAttachments(
            List<MultipartFile> attachments,
            List<String> attachmentPath
    ) {
        List<MimeBodyPart> bodyParts = new ArrayList<>();

        if (attachments != null && !attachments.isEmpty()) {
            attachments.forEach(file -> {
                MimeBodyPart attachment = new MimeBodyPart();
                try {
                    ByteArrayDataSource attachmentDataSource =
                            new ByteArrayDataSource(file.getInputStream(), file.getContentType());

                    attachment.setDataHandler(new DataHandler(attachmentDataSource));

                    attachment.setFileName(file.getOriginalFilename());
                    bodyParts.add(attachment);
                } catch (IOException | MessagingException e) {
                    LOGGER.error("Failed to create file " + e.getMessage());
                    throw new RuntimeException(e);
                }
            });
        }

        if (attachmentPath != null && !attachmentPath.isEmpty()) {
            attachmentPath.forEach(path -> {
                MimeBodyPart attachment = new MimeBodyPart();
                File file = new File(path);
                try {
                    attachment.attachFile(file);
                } catch (IOException | MessagingException e) {
                    LOGGER.error("Failed to create file " + e.getMessage());
                    throw new RuntimeException(e);
                }
                bodyParts.add(attachment);
            });
        }
        return bodyParts;
    }
}
