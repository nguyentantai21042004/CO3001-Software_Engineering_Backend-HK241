package com.project.backend.services.firebase;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final Storage storage;

    public FirebaseStorageService() {
        this.storage = StorageClient.getInstance().bucket().getStorage();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        return blob.getMediaLink();
    }

    public boolean deleteFile(String filePath) {
        String bucketName = "testbe-28a98.appspot.com";
        Bucket bucket = storage.get(bucketName);
        return bucket.get(filePath).delete();
    }
}
