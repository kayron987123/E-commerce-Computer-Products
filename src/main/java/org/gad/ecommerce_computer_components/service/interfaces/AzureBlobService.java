package org.gad.ecommerce_computer_components.service.interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AzureBlobService {
    String uploadFile(MultipartFile file) throws IOException;
}
