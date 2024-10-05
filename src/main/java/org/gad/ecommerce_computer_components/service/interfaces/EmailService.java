package org.gad.ecommerce_computer_components.service.interfaces;

public interface EmailService {
    void sendEmailTemporaryKey(String toUser, String token);
    void sendEmailTemporaryKeyUpdate(String toUser, String token);
    void sendEmailToDeleteUser(String toUser);
}
