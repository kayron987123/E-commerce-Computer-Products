package org.gad.ecommerce_computer_components.service.interfaces;

public interface EmailService {
    void sendEmailWithTokenConfirmation(String toUser, String token, String emailType);
    void sendEmailToDeleteUser(String toUser);
}
