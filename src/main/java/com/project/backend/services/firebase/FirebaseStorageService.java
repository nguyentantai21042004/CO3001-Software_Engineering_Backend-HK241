package com.project.backend.services.firebase;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    private final Storage storage;

    public FirebaseStorageService() throws IOException {
        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream("src/main/resources/serviceAccountKey.json"));
        this.storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();
    }

    public String uploadFile(MultipartFile file) throws IOException {
        // Tạo một tên file duy nhất
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // Lấy bucket từ Firebase Storage
        Bucket bucket = StorageClient.getInstance().bucket();

        // Upload file lên Firebase Storage
        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.OWNER));

        // Trả về URL của file đã upload
        return blob.getMediaLink();
    }

    public boolean deleteFile(String filePath) {
        String bucketName = "testbe-28a98.appspot.com";
        Bucket bucket = storage.get(bucketName);

        // Delete the file
        return bucket.get(filePath).delete();
    }
}