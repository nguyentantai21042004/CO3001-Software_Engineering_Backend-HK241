package com.project.backend.services.firebase;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
            // Đảm bảo Content-Type chính xác
            if (blob.getContentType() == null || blob.getContentType().isEmpty()) {
                blob.toBuilder().setContentType(file.getContentType()).build().update();
            }
            blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
            // Cập nhật metadata để file hiển thị inline (trong trình duyệt) thay vì tải xuống
            blob.toBuilder().setContentDisposition("inline").build().update();

            // Trả về URL có thể xem trực tiếp trong trình duyệt
            String fileUrl = String.format("https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    URLEncoder.encode(blob.getName(), StandardCharsets.UTF_8));

            return fileUrl;
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
