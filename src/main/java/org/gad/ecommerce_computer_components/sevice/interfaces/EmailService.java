package org.gad.ecommerce_computer_components.sevice.interfaces;

import java.io.File;

public interface EmailService {
    void sendEmailWithTokenConfirmation(String toUser, String token, String emailType);
    void sendEmailToDeleteUser(String toUser);
}
