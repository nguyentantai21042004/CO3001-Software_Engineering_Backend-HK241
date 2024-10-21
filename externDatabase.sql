CREATE TABLE tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) NOT NULL,  -- Access token hoặc refresh token
    token_type VARCHAR(50) NOT NULL,  -- Loại token (access hoặc refresh)
    student_id INT NOT NULL,  -- ID của sinh viên sở hữu token
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Thời điểm token được cấp
    expires_at TIMESTAMP,  -- Thời điểm token hết hạn
    revoked BOOLEAN DEFAULT FALSE,  -- Trạng thái thu hồi token
    device_info VARCHAR(255),  -- Thông tin thiết bị đăng nhập
    ip_address VARCHAR(45),  -- Địa chỉ IP đăng nhập
    is_mobile TINYINT(1) DEFAULT 0,  -- Trạng thái đăng nhập trên thiết bị di động
    refresh_token VARCHAR(255) NOT NULL,  -- Refresh token
    refresh_expiration_date TIMESTAMP,  -- Thời gian hết hạn refresh token
    FOREIGN KEY (student_id) REFERENCES students(id)  -- Liên kết với bảng students
);


ALTER TABLE students
    MODIFY student_join_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    MODIFY student_last_login DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;


CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

ALTER TABLE students ADD COLUMN role_id INT;

ALTER TABLE students MODIFY COLUMN role_id BIGINT;


ALTER TABLE students ADD CONSTRAINT fk_role_id FOREIGN KEY (role_id) REFERENCES roles(id);

ALTER TABLE students
RENAME COLUMN student_id TO id;

ALTER TABLE students
RENAME COLUMN student_name TO full_name;

ALTER TABLE students
RENAME COLUMN student_email TO email;

ALTER TABLE students
RENAME COLUMN student_join_date TO join_date;

ALTER TABLE students
RENAME COLUMN student_last_login TO last_login;

ALTER TABLE students
ADD COLUMN password VARCHAR(255);

ALTER TABLE tokens
ADD COLUMN role varchar(50);
