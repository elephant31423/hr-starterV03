CREATE TABLE IF NOT EXISTS leave_type (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  paid TINYINT(1) NOT NULL DEFAULT 1,
  need_approve TINYINT(1) NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS leave_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  leave_type_id BIGINT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  days DECIMAL(10,2) NOT NULL DEFAULT 0,
  leave_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  reason VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING_DEPARTMENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_leave_requests_employee (employee_id),
  INDEX idx_leave_requests_status (status),
  INDEX idx_leave_requests_date_range (start_date, end_date),
  INDEX idx_leave_requests_type (leave_type_id)
);

CREATE TABLE IF NOT EXISTS leave_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  leave_type VARCHAR(50) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  leave_date DATE NOT NULL,
  leave_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  reason VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'APPROVED',
  created_by BIGINT NULL,
  approved_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_leave_records_employee_date (employee_id, leave_date),
  INDEX idx_leave_records_type_status (leave_type, status)
);

CREATE TABLE IF NOT EXISTS employee_annual_leaves (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  year INT NOT NULL,
  total_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  used_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  remain_hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_employee_annual_leaves_employee_year (employee_id, year),
  INDEX idx_employee_annual_leaves_year (year)
);

CREATE TABLE IF NOT EXISTS annual_leave_rule (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  rule_name VARCHAR(100) NOT NULL,
  min_years INT NOT NULL,
  max_years INT NULL,
  hours DECIMAL(10,2) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP
);

INSERT INTO leave_type (code, name, paid, need_approve)
VALUES
  ('ANNUAL', '特休', 1, 1),
  ('SICK', '病假', 0, 1),
  ('PERSONAL', '事假', 0, 1),
  ('BIRTHDAY', '生日假', 1, 1)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  paid = VALUES(paid),
  need_approve = VALUES(need_approve);

INSERT INTO annual_leave_rule (rule_name, min_years, max_years, hours)
SELECT '滿 0 年', 0, 1, 0
WHERE NOT EXISTS (SELECT 1 FROM annual_leave_rule WHERE min_years = 0 AND max_years = 1);

INSERT INTO annual_leave_rule (rule_name, min_years, max_years, hours)
SELECT '滿 1 年', 1, 2, 56
WHERE NOT EXISTS (SELECT 1 FROM annual_leave_rule WHERE min_years = 1 AND max_years = 2);

INSERT INTO annual_leave_rule (rule_name, min_years, max_years, hours)
SELECT '滿 2 年', 2, 3, 80
WHERE NOT EXISTS (SELECT 1 FROM annual_leave_rule WHERE min_years = 2 AND max_years = 3);

INSERT INTO annual_leave_rule (rule_name, min_years, max_years, hours)
SELECT '滿 3 年以上', 3, NULL, 112
WHERE NOT EXISTS (SELECT 1 FROM annual_leave_rule WHERE min_years = 3 AND max_years IS NULL);

INSERT INTO employee_annual_leaves (employee_id, year, total_hours, used_hours, remain_hours)
SELECT e.id, YEAR(CURDATE()), 56, 0, 56
FROM employee e
WHERE NOT EXISTS (
  SELECT 1
  FROM employee_annual_leaves eal
  WHERE eal.employee_id = e.id
    AND eal.year = YEAR(CURDATE())
);
