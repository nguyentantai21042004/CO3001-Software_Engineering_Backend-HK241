package com.project.backend.services.firebase;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final StorageClient storageClient;

    public FirebaseStorageService() {
        this.storageClient = StorageClient.getInstance();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            Bucket bucket = storageClient.bucket();
            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            return blob.getMediaLink();
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean deleteFile(String filePath) {
        try {
            String bucketName = "testbe-28a98.appspot.com";
            Bucket bucket = storageClient.bucket(bucketName);
            Blob blob = bucket.get(filePath);
            if (blob == null) {
                return false;
            }
            return blob.delete();
        } catch (Exception e) {
            return false;
        }
    }

}
