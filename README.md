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
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/torm?autoReconnect=true&useSSL=false
db.username=root
db.password=123456789
```
Lưu ý:
* Thay đổi tên database, tài khoản và mật khẩu tương ứng. 
* Sử dụng driver: ```com.mysql.jdbc.Driver``` với MySql < 8.x

#### 4. Mapping các đối tượng với các bảng:
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

##### Cách map:
* Dùng ```@Entity``` với thuộc tính ```tableName``` để chỉ định tên bảng trong database.
* Đặt ```@Column``` với thuộc tính ```name``` để chỉ định tên cột trong database.
* Đối khoá chính: đặt ```@Id``` với thuộc tính ```autoIncrement``` để chỉ định đó là khoá chính và có tự tăng hay không.
* Đặt ```@IdField``` lên trên class và ```name``` phải trùng với tên của thuộc tính khoá chính trong đối tượng;
* Tạo getter và setter tương ứng

### Example code:
#### Cấu trúc chung khi thực hiện truy vấn:
```java
Session session = this.getSession();

try {
  // do something
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

#### Cách sử dụng Criteria Query
1. Lấy danh sách các User
```java
List<UserEntity> userEntityList = new ArrayList<>();
Session session = this.getSession();

try {
  userEntityList = session.createCriteria(UserEntity.class).list();
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

2. Lấy 1 User bởi Id
```java
UserEntity userEntity = null;
Session session = this.getSession();

try {
  userEntity = (UserEntity) session.get(UserEntity.class, id);
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

3. Đếm số lượng User có trong database
```java
Session session = this.getSession();
Criteria cr = session.createCriteria(UserEntity.class);
Long rowCount = 0L;

try {
  cr.addSelection(Projections.rowCount());
  rowCount = (Long) cr.uniqueResult();
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

4. Lấy danh sách User theo điều kiện
Ví dụ câu sql như sau:
```mysql
SELECT * FROM user WHERE (username LIKE '%thanh' OR id BETWEEN 1 AND 3) AND full_name IS NOT NULL
```

Thì trong java code:
```java
List<UserEntity> userEntityList = new ArrayList<>();
Session session = this.getSession();
Criteria cr = session.createCriteria(UserEntity.class);

List<Criterion> criterionList = new ArrayList<>();

GroupExpression groupExpression = Logical.andGroup();
groupExpression.add(Logical.and("username").like("thanh", MatchMode.START));
groupExpression.add(Logical.or("id").between(2, 3));
criterionList.add(groupExpression);
criterionList.add(Logical.and("fullName").isNotNull());

try {
  userEntityList = criteria.list();
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

5. Chi lay 1 so thuoc tinh can thiet
* Doi voi Aggregate Function:
Vi du cau sql nhu sau: 
```mysql
SELECT min(id), max(id) FROM user
```
Thi ta chi can them nhu sau truoc khi thuc hien ```java criteria.list()```:
```java
criteria.addSelection(Projections.min("id"));
criteria.addSelection(Projections.max("id"));
```

Chú ý: Khi can lay nhung thuoc tinh la Aggregate Function thi ta khong the mapping thanh doi tuong, danh sach tra ve co dang ```java List<Object[]>```

* Doi voi cac thuoc tinh
Vi du cau sql nhu sau: 
```mysql
SELECT full_name, role_id FROM user
```
Thi ta chi can them nhu sau truoc khi thuc hien ```java criteria.list()```:
```java
criteria.addSelection("fullName");
criteria.addSelection("roleId");
```
Chu y: Ket qua tra ve van la danh sach cac doi tuong.

6. Su dung group by va alias
Vi du cau sql nhu sau: 
```mysql
SELECT max(id) AS user_id FROM user GROUP BY role_id
```

Thi ta se lam nhu sau:
```java
criteria.addSelection(Projections.max("id").as("user_id"));
criteria.addGroupBy("roleId");
```

7. set order va limit 
Vi du cau sql nhu sau: 
```mysql
SELECT * FROM user ORDER BY id DESC LIMIT 2 OFFSET 0
```

Trong java code:
```java
criteria.addOrder(Order.desc("id"));
criteria.setMaxResults(2);
criteria.setFirstResult(0);
```