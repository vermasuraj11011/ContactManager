package com.contactManager.service;


import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadImage(String path, MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        String uniqueFileName = randomId.concat(fileName.substring(fileName.lastIndexOf(".")));
        File savedFile = new ClassPathResource(path).getFile();
        Path paths = Paths.get(savedFile.getAbsolutePath() + File.separator + uniqueFileName);
        Files.copy(file.getInputStream(), paths);
        return uniqueFileName;
    }

    @Override
    public InputStream getResources(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        InputStream is = new FileInputStream(fullPath);
        return is;
    }
}
