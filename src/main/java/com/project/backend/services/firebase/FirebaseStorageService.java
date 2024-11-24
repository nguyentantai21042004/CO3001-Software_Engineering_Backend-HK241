package com.project.backend.services.firebase;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            // Tạo tên file duy nhất
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

            // Lấy bucket từ Firebase Storage
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));

            return blob.getMediaLink();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi upload file lên Firebase Storage", e);
        }
    }

    public boolean deleteFile(String filePath) {
        try {
            // Lấy bucket từ Firebase Storage
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.get(filePath);
            if (blob == null) {
                return false;
            }
            return blob.delete();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa file từ Firebase Storage", e);
        }
    }
}
