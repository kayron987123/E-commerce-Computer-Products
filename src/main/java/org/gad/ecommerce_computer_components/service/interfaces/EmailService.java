package org.gad.ecommerce_computer_components.sevice.interfaces;

import java.io.File;

public interface EmailService {
    void sendEmailTemporaryKey(String toUser, String token);
    void sendEmailTemporaryKeyUpdate(String toUser, String token);
    void sendEmailToDeleteUser(String toUser);
}
