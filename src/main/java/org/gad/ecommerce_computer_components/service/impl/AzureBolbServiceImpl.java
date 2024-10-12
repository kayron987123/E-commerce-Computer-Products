package org.gad.ecommerce_computer_components.service.impl;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.gad.ecommerce_computer_components.service.interfaces.AzureBlobService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class AzureBolbServiceImpl implements AzureBlobService {

    private static final List<String> VALID_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Value("${AZURE_STORAGE_CONTAINER_NAME}")
    private String containerName;

    @Value("${AZURE_STORAGE_CONNECTION_STRING}")
    private String connectionString;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        if (!isImageFile(file.getOriginalFilename())) {
            throw new IllegalArgumentException("El archivo no es una imagen válida");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido");
        }

        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        BlobClient blobClient = containerClient.getBlobClient(fileName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);
        return blobClient.getBlobUrl();
    }

    private boolean isImageFile(String fileName) {
        String extension = getFileExtension(fileName);
        return VALID_EXTENSIONS.contains(extension.toLowerCase());
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = originalFileName.substring(0, originalFileName.lastIndexOf("."));
        return baseName + "_" + System.currentTimeMillis() + "." + extension;
    }
}
