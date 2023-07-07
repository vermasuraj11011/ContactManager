package com.contactManager.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileService {
    String uploadImage(MultipartFile file) throws Exception;

    Boolean deleteImage(String fileName) throws Exception;

    InputStream getResources(String path, String fileName) throws FileNotFoundException;
}
