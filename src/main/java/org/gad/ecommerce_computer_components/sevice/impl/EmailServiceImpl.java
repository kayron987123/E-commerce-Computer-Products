package org.gad.ecommerce_computer_components.sevice.impl;

import org.gad.ecommerce_computer_components.sevice.interfaces.EmailService;
import org.gad.ecommerce_computer_components.sevice.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EmailServiceImpl implements EmailService {

    private final static String SUBJECT_TOKEN = "CREACION DE CUENTA EN EL E-COMMERCE";
    private final static String MESSAGE_TOKEN = "SU CUENTA HA SIDO CREADA CON EXITO, SU CONTRASEÑA TEMPORAL ES: ";

    @Value("${USER_EMAIL}")
    private String user;

    @Autowired
    private JavaMailSender emailSender;

    @Override
    public void sendEmailTemporaryKey(String toUser, String token) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(user);
        mailMessage.setTo(toUser);
        mailMessage.setSubject(SUBJECT_TOKEN);
        mailMessage.setText(MESSAGE_TOKEN.concat(token));

        emailSender.send(mailMessage);
    }
}
