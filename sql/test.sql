 use hrdb;
 set @userId='2';
 	SELECT r.id,
               r.role_key  AS roleKey,
               r.role_name AS roleName
        FROM roles r
        JOIN user_roles ur ON r.id = ur.role_id
        WHERE ur.user_id =  @userId;
        
        select*from users;
        select*from employee;
        select*from user_role;
        select*from roles;
 
 CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(100),
  enabled TINYINT DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE roles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  role_key VARCHAR(100) UNIQUE NOT NULL,
  role_name VARCHAR(100) NOT NULL
);

CREATE TABLE user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id)
);

CREATE TABLE employee (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  emp_no VARCHAR(50) UNIQUE,
  user_id BIGINT,
  name VARCHAR(100),
  department_id BIGINT,
  title VARCHAR(100),
  hire_date DATE,
  status TINYINT DEFAULT 1,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


INSERT INTO roles (role_key, role_name) VALUES
('ADMIN', '系統管理員'),
('HR', '人資'),
('MANAGER', '主管'),
('EMPLOYEE', '一般員工');
INSERT INTO users (username, password, full_name, enabled) VALUES
('admin', '123456', 'Admin User', 1),
('hr_tina', '123456', 'Tina HR', 1),
('hr_mike', '123456', 'Mike HR', 1),
('manager_john', '123456', 'John Manager', 1),
('manager_lisa', '123456', 'Lisa Manager', 1),
('emp_tom', '123456', 'Tom Employee', 1),
('emp_kelly', '123456', 'Kelly Employee', 1),
('emp_steve', '123456', 'Steve Employee', 1),
('emp_amy', '123456', 'Amy Employee', 1),
('emp_bruce', '123456', 'Bruce Employee', 1);
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1),  -- admin → ADMIN
(2, 2),  -- hr_tina → HR
(3, 2),  -- hr_mike → HR
(4, 3),  -- manager_john → MANAGER
(5, 3),  -- manager_lisa → MANAGER
(6, 4),  -- emp_tom → EMPLOYEE
(7, 4),  -- emp_kelly → EMPLOYEE
(8, 4),  -- emp_steve → EMPLOYEE
(9, 4),  -- emp_amy → EMPLOYEE
(10, 4); -- emp_bruce → EMPLOYEE
INSERT INTO employee (emp_no, user_id, name, department_id, title, hire_date, status) VALUES
('EMP001', 1, 'Admin User', 1, 'System Admin', '2020-01-01', 1),
('EMP002', 2, 'Tina HR', 2, 'HR Specialist', '2021-03-15', 1),
('EMP003', 3, 'Mike HR', 2, 'HR Specialist', '2022-07-01', 1),
('EMP004', 4, 'John Manager', 3, 'Department Manager', '2019-08-20', 1),
('EMP005', 5, 'Lisa Manager', 3, 'Department Manager', '2020-11-30', 1),
('EMP006', 6, 'Tom Employee', 4, 'Staff', '2023-01-10', 1),
('EMP007', 7, 'Kelly Employee', 4, 'Staff', '2023-02-05', 1),
('EMP008', 8, 'Steve Employee', 4, 'Staff', '2023-04-12', 1),
('EMP009', 9, 'Amy Employee', 4, 'Staff', '2023-05-10', 1),
('EMP010', 10, 'Bruce Employee', 4, 'Staff', '2023-06-01', 1);


