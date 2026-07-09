SET @demo_password = '$2a$10$z5srfFIiSlLOFHgRYFct3u0Bc7z9nqUKburNvFdSnsZqKIC1ySfz2';

SET @add_users_version = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE users ADD COLUMN version BIGINT NOT NULL DEFAULT 0', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'version'
);
PREPARE stmt FROM @add_users_version;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_users_updated_at = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE users ADD COLUMN updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'users' AND column_name = 'updated_at'
);
PREPARE stmt FROM @add_users_updated_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_roles_status = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE roles ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT ''ACTIVE''', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'roles' AND column_name = 'status'
);
PREPARE stmt FROM @add_roles_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_roles_description = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE roles ADD COLUMN description VARCHAR(255) NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'roles' AND column_name = 'description'
);
PREPARE stmt FROM @add_roles_description;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_roles_created_by = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE roles ADD COLUMN created_by BIGINT NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'roles' AND column_name = 'created_by'
);
PREPARE stmt FROM @add_roles_created_by;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_roles_created_at = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE roles ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'roles' AND column_name = 'created_at'
);
PREPARE stmt FROM @add_roles_created_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_roles_updated_at = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE roles ADD COLUMN updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'roles' AND column_name = 'updated_at'
);
PREPARE stmt FROM @add_roles_updated_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_email = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE employee ADD COLUMN email VARCHAR(120) NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'employee' AND column_name = 'email'
);
PREPARE stmt FROM @add_employee_email;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_phone = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE employee ADD COLUMN phone VARCHAR(50) NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'employee' AND column_name = 'phone'
);
PREPARE stmt FROM @add_employee_phone;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_address = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE employee ADD COLUMN address VARCHAR(255) NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'employee' AND column_name = 'address'
);
PREPARE stmt FROM @add_employee_address;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_birthday = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE employee ADD COLUMN birthday DATE NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'employee' AND column_name = 'birthday'
);
PREPARE stmt FROM @add_employee_birthday;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_resign_date = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE employee ADD COLUMN resign_date DATETIME NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'employee' AND column_name = 'resign_date'
);
PREPARE stmt FROM @add_employee_resign_date;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_updated_at = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE employee ADD COLUMN updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'employee' AND column_name = 'updated_at'
);
PREPARE stmt FROM @add_employee_updated_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

CREATE TABLE IF NOT EXISTS departments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  code VARCHAR(50) UNIQUE,
  parent_id BIGINT NULL,
  manager_id BIGINT NULL,
  description VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO departments (name, code, parent_id, description)
VALUES
  ('人資部', 'HR', NULL, 'Human Resources'),
  ('營運部', 'OPS', NULL, 'Operations')
ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);

INSERT INTO roles (role_key, role_name, status, description)
VALUES
  ('ADMIN', '超級管理員', 'ACTIVE', '系統最高權限'),
  ('HR', '人資', 'ACTIVE', '人資管理與審核'),
  ('EMPLOYEE', '員工', 'ACTIVE', '一般員工')
ON DUPLICATE KEY UPDATE
  role_name = VALUES(role_name),
  status = VALUES(status),
  description = VALUES(description);

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
VALUES
  ('employee:view', '員工查看', '查看員工資料', NULL),
  ('employee:create', '員工新增', '新增員工資料', NULL),
  ('employee:update', '員工修改', '修改員工資料', NULL),
  ('employee:delete', '員工刪除', '刪除員工資料', NULL),
  ('user:view', '帳號查看', '查看使用者帳號', NULL),
  ('user:create', '帳號新增', '新增使用者帳號', NULL),
  ('user:update', '帳號修改', '修改使用者帳號', NULL),
  ('user:delete', '帳號刪除', '刪除使用者帳號', NULL),
  ('role:view', '角色查看', '查看角色', NULL),
  ('role:create', '角色新增', '新增角色', NULL),
  ('role:update', '角色修改', '修改角色', NULL),
  ('role:delete', '角色刪除', '刪除角色', NULL),
  ('permission:view', '權限查看', '查看權限', NULL),
  ('department:view', '部門查看', '查看部門', NULL),
  ('department:create', '部門新增', '新增部門', NULL),
  ('department:update', '部門修改', '修改部門', NULL),
  ('department:delete', '部門刪除', '刪除部門', NULL),
  ('calendar:view:all', '所有月曆查看', '查看所有員工月曆', NULL),
  ('shift:edit', '排班編輯', '編輯本人或授權排班', NULL),
  ('shift:edit:others', '他人排班編輯', '編輯其他員工排班', NULL),
  ('leave:apply', '請假申請', '提交請假申請', NULL),
  ('annualLeave:edit', '特休編輯', '編輯特休額度', NULL),
  ('audit:view', '審計紀錄查看', '查看審計紀錄', NULL)
ON DUPLICATE KEY UPDATE
  permission_name = VALUES(permission_name),
  description = VALUES(description);

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p
WHERE r.role_key = 'ADMIN';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN (
  'employee:view',
  'employee:create',
  'employee:update',
  'user:view',
  'calendar:view:all',
  'shift:edit',
  'shift:edit:others',
  'leave:apply',
  'leave:approve:department',
  'leave:approve:hr',
  'annualLeave:edit',
  'overtime:view',
  'overtime:apply',
  'overtime:approve:hr',
  'department:view',
  'setting:view',
  'setting:update',
  'audit:view'
)
WHERE r.role_key = 'HR';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN (
  'employee:view',
  'shift:edit',
  'leave:apply',
  'overtime:apply'
)
WHERE r.role_key = 'EMPLOYEE';

INSERT INTO employee (emp_no, name, department_id, title, hire_date, status, email, phone, address)
VALUES
  ('A0001', '系統管理員', (SELECT id FROM departments WHERE code = 'HR'), 'System Administrator', '2026-01-01', 1, 'admin@example.com', '0900000001', 'Demo'),
  ('H0001', '人資使用者', (SELECT id FROM departments WHERE code = 'HR'), 'HR Specialist', '2026-01-01', 1, 'hr@example.com', '0900000002', 'Demo'),
  ('E0001', '一般員工', (SELECT id FROM departments WHERE code = 'OPS'), 'Staff', '2026-01-01', 1, 'employee@example.com', '0900000003', 'Demo')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  department_id = VALUES(department_id),
  title = VALUES(title),
  status = VALUES(status),
  email = VALUES(email),
  phone = VALUES(phone);

INSERT INTO users (username, password, full_name, enabled, employee_id)
VALUES
  ('admin', @demo_password, '系統管理員', 1, (SELECT id FROM employee WHERE emp_no = 'A0001')),
  ('hr_user', @demo_password, '人資使用者', 1, (SELECT id FROM employee WHERE emp_no = 'H0001')),
  ('employee', @demo_password, '一般員工', 1, (SELECT id FROM employee WHERE emp_no = 'E0001'))
ON DUPLICATE KEY UPDATE
  password = VALUES(password),
  full_name = VALUES(full_name),
  enabled = VALUES(enabled),
  employee_id = VALUES(employee_id);

UPDATE employee e
JOIN users u ON u.employee_id = e.id
SET e.user_id = u.id
WHERE e.emp_no IN ('A0001', 'H0001', 'E0001');

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_key = 'ADMIN'
WHERE u.username = 'admin';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_key = 'HR'
WHERE u.username = 'hr_user';

INSERT IGNORE INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.role_key = 'EMPLOYEE'
WHERE u.username = 'employee';
