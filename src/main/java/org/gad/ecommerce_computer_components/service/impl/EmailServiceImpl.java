package org.gad.ecommerce_computer_components.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.gad.ecommerce_computer_components.service.interfaces.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailServiceImpl implements EmailService {

    private final static String SUBJECT_TOKEN = "CREACION DE CUENTA EN EL E-COMMERCE";
    private final static String SUBJECT_DELETE = "ELIMINACION DE CUENTA EN EL E-COMMERCE";
    private final static String SUBJECT_UPDATE_PASSWORD = "ACTUALIZACION DE CONTRASEÑA EN EL E-COMMERCE";

    @Value("USER_EMAIL")
    private String user;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine mailTemplateEngine;

    @Override
    public void sendEmailWithTokenConfirmation(String toUser, String token, String emailType) {
        Context context = new Context();
        context.setVariable("token", token);

        String emailContent = "";

        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(user);
            helper.setTo(toUser);

            switch (emailType) {
                case "TOKEN":
                    emailContent = mailTemplateEngine.process("email-create-user", context);
                    helper.setSubject(SUBJECT_TOKEN);
                    helper.setText(emailContent,true);
                    break;
                case "UPDATE":
                    emailContent = mailTemplateEngine.process("email-update-user", context);
                    helper.setSubject(SUBJECT_UPDATE_PASSWORD);
                    helper.setText(emailContent, true);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid email type");
            }
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo", e);
        }
    }


    @Override
    public void sendEmailToDeleteUser(String toUser) {
        Context context = new Context();

        String emailContent = mailTemplateEngine.process("email-delete-user", context);

        MimeMessage message = emailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(user);
            helper.setTo(toUser);
            helper.setSubject(SUBJECT_DELETE);
            helper.setText(emailContent, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo de eliminación", e);
        }
    }
}
