## Torm là một thư viện ORM hỗ trợ thao tác dữ liệu thông qua đối tượng.

## Được xây dựng dựa trên Hibernate 4.x - Hoạt động với MySQL.

### Các chức năng:
* Hỗ trợ câu lệnh Criteria dùng để build câu truy vấn trên đối tượng.
* Hỗ trợ SQLQuery đối với những câu truy vấn phức tạp.
* Có thể đặt tên cho các tham số trong câu truy vấn.
* Thao tác thêm, sửa, xoá 1 đối tượng.
* Tải lại dữ liệu của đối tượng sau khi save hoặc update để lấy dữ liệu của các trường do trigger sinh ra.

### Một số hạn chế ở version 1.0.1:
* Criteria chỉ truy vấn được trên 1 đối tượng.
* Chưa có mệnh đề Having trong Criteria.
* Chưa biễu diễn được các quan hệ (1-1, 1-n, n-n) trên các thực thể.

### Một số lưu ý:
* Chạy trên Java 8
* Hoạt động tốt với MySql

### Các bước cấu hình:
1. Tạo file torm.properties trong thư mục resource của project.
2. Thêm các thuộc tính để kết nối với database, ví dụ:
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/torm?autoReconnect=true&useSSL=false
db.username=root
db.password=123456789
```
3. Mapping các đối tượng với các bảng.
4. Hoàn tất.

### Demo code:
#### 1. Tạo database đơn giản với cấu trúc như sau:
```mysql
CREATE TABLE `role` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_USER_ROLE` (`role_id`),
  CONSTRAINT `FK_USER_ROLE` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
);
```

#### 2. Thêm dữ liệu vào các bảng:
```mysql
INSERT INTO `role` VALUES ('ADMIN', 'Admin');
INSERT INTO `role` VALUES ('USER', 'User');

INSERT INTO `user` VALUES (1, 'thanhtai', 'Vo Thanh Tai', '12345', 'ADMIN');
INSERT INTO `user` VALUES (2, 'thanhcong', NULL, NULL, 'USER');
INSERT INTO `user` VALUES (3, 'anhtuan', 'Bui Anh Tuan', '1234', 'USER');
```

#### 3. Tạo file torm.properties


#### 3. Mapping các đối tượng với các bảng:
##### RoleEntity
```java
@Entity(tableName = "role")
@IdField(name = "id")
public class RoleEntity {
    @Id(autoIncrement = false)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    // getter and setter
}
```

##### UserEntity
```java
@Entity(tableName = "user")
@IdField(name = "id")
public class UserEntity {
    @Id(autoIncrement = true)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "password")
    private String password;

    @Column(name = "role_id")
    private String roleId;

    // getter and setter
}
```
