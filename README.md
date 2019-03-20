## Torm là một thư viện ORM hỗ trợ thao tác dữ liệu thông qua đối tượng.

## Được xây dựng dựa trên Hibernate 4.x - Hoạt động với MySQL.

### Thư viện được phát triển thêm từ private project quản lý bất động sản.

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
##### 1. Lấy danh sách các User
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

##### 2. Lấy 1 User bởi Id
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

##### 3. Đếm số lượng User có trong database
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

##### 4. Lấy danh sách User theo điều kiện
Ví dụ câu sql như sau:
```mysql
SELECT * FROM user WHERE (username LIKE '%thanh' OR id BETWEEN 1 AND 3) AND full_name IS NOT NULL
```

Java code tương ứng:
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

// thêm danh sách điều kiện vào câu query
for (Criterion criterion : criterionList) {
  criteria.addCriterion(criterion);
}

try {
  userEntityList = criteria.list();
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

##### 5. Lấy 1 User theo điều kiện
Ví dụ câu sql như sau:
```mysql
SELECT * FROM user WHERE username = 'anhtuan' AND password = '1234'
```

Java code tương ứng:
```java
UserEntity userEntity = null;
Session session = this.getSession();
Criteria cr = session.createCriteria(UserEntity.class);
criteria.addCriterion(Logical.and("username").eq("anhtuan"));
criteria.addCriterion(Logical.and("password").eq("1234"));

try {
  userEntity = (UserEntity) criteria.uniqueResult();
} catch (Exception e) {
    e.printStackTrace();
} finally {
    session.close();
}
```

###### Tham khảo thêm các điều kiện trong Class ```Restrictions```

##### 6. Chỉ lấy 1 số thuộc tính cần thiết:
* Đối với Aggregate Function:
Ví dụ câu sql như sau: 
```mysql
SELECT min(id), max(id) FROM user
```
Thì ta cần thêm như sau trước khi thực hiện ```criteria.list()```:
```java
criteria.addSelection(Projections.min("id"));
criteria.addSelection(Projections.max("id"));
```

Chú ý: Khi cần lấy những thuộc tính là Aggregate Function thì kết quả trả về có dạng ```List<Object[]>```

* Đối với các thuộc tính của đối tượng
Ví dụ câu sql:
```mysql
SELECT full_name, role_id FROM user
```
Java code tương ứng:
```java
criteria.addSelection("fullName");
criteria.addSelection("roleId");
```
Chú ý: Kết quả trả về là danh sách các đối tượng, những thuộc tính không lấy sẽ là ```null```.

##### 7. Sử dụng Group by và Alias
Đối với câu sql như sau:
```mysql
SELECT max(id) AS user_id FROM user GROUP BY role_id
```

Java code tương ứng:
```java
criteria.addSelection(Projections.max("id").as("user_id"));
criteria.addGroupBy("roleId");
```

##### 8. Sử dụng Order By và Limit, Offset
Ví dụ câu sql:
```mysql
SELECT * FROM user ORDER BY id DESC LIMIT 2 OFFSET 0
```

Java code tương ứng:
```java
criteria.addOrder(Order.desc("id"));
criteria.setMaxResults(2);
criteria.setFirstResult(0);
```

#### Cách sử dụng SQL Query
##### 1. Ví dụ 1 câu sql đơn giản như sau:
```mysql
SELECT * FROM user WHERE username = 'anhtuan' AND password = '1234'
```

Java code tương ứng:
```java
Session session = SessionFactory.openSession();
try {
  SQLQuery sqlQuery = session.createSQLQuery("SELECT * FROM user WHERE username = {username} AND password = {password}");
  sqlQuery.setEntity(UserEntity.class);
  sqlQuery.setParam("username", "anhtuan");
  sqlQuery.setParam("password", "1234");
  List<UserEntity> list = sqlQuery.list();
} catch (SQLException e) {
  e.printStackTrace();
} finally {
  session.close();
}
```
Chú ý: Do dữ liệu lấy về là những thuộc tính của User nên ta thêm ```sqlQuery.setEntity(UserEntity.class);``` để kết quả trả về là 1 danh sách các User

##### 2. Ví dụ câu sql kết bảng như sau:
```mysql
SELECT u.full_name, r.name FROM user u JOIN role r ON u.role_id = r.id WHERE u.id BETWEEN 1 AND 2
```

Java code tương ứng:
```java
Session session = SessionFactory.openSession();
try {
  SQLQuery sqlQuery = session.createSQLQuery("SELECT u.full_name, r.name FROM user u JOIN role r ON u.role_id = r.id WHERE u.id BETWEEN {lowId} AND {highId}");
  sqlQuery.setParam("lowId", 1);
  sqlQuery.setParam("highId", 2);
  List<Objec[]> list = sqlQuery.list();
} catch (SQLException e) {
  e.printStackTrace();
} finally {
  session.close();
}
```
Chú ý: đối với trường hợp này, do lấy dữ liệu từ 2 bảng nên không thể trả về 1 danh sách User, thay vào đó sẽ trả về danh sách các mảng đối tượng.

#### Thêm, sửa, xoá đối tượng
#### Cấu trúc chung:
```java
Session session = this.getSession();
Transaction transaction = session.beginTransaction();

try {
  // do something
  transaction.commit();
} catch (SQLException e) {
  transaction.rollback();
  e.printStackTrace();
} finally {
  session.close();
}
```

##### 1. Thêm mới 1 User
```java
UserEntity userEntity = new UserEntity();
userEntity.setUsername("haimy");
userEntity.setFullName("Tran Hai My");
userEntity.setRoleId("USER");

Session session = this.getSession();
Transaction transaction = session.beginTransaction();

try {
  session.save(userEntity);
  transaction.commit();
} catch (SQLException e) {
  transaction.rollback();
  e.printStackTrace();
} finally {
  session.close();
}
```

##### 2. Cập nhật thông tin 1 User
```java
UserEntity userEntity = new UserEntity();
userEntity.setId(4);
userEntity.setUsername("haimy");
userEntity.setFullName("Tran Hai Cau");
userEntity.setPassword("1->9");
userEntity.setRoleId("ADMIN");

Session session = this.getSession();
Transaction transaction = session.beginTransaction();

try {
  session.update(userEntity);
  transaction.commit();
} catch (SQLException e) {
  transaction.rollback();
  e.printStackTrace();
} finally {
  session.close();
}
```

##### 3. Xoá 1 User bởi Id
```java
UserEntity userEntity = new UserEntity();
userEntity.setId(4);

Session session = this.getSession();
Transaction transaction = session.beginTransaction();

try {
  session.delete(userEntity);
  transaction.commit();
} catch (SQLException e) {
  transaction.rollback();
  e.printStackTrace();
} finally {
  session.close();
}
```
