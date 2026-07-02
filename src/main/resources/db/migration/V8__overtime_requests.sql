CREATE TABLE IF NOT EXISTS overtime_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  overtime_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  hours DECIMAL(8,2) NOT NULL,
  reason VARCHAR(500) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING_DEPARTMENT',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_overtime_requests_employee_date (employee_id, overtime_date),
  INDEX idx_overtime_requests_status (status)
);

CREATE TABLE IF NOT EXISTS overtime_approval_steps (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  overtime_request_id BIGINT NOT NULL,
  step_order INT NOT NULL,
  step_code VARCHAR(50) NOT NULL,
  approver_role VARCHAR(50) NOT NULL,
  approver_user_id BIGINT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
  comment VARCHAR(500) NULL,
  approved_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_overtime_approval_request_order (overtime_request_id, step_order),
  INDEX idx_overtime_approval_status (status, approver_role)
);

CREATE TABLE IF NOT EXISTS overtime_records (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  overtime_request_id BIGINT NOT NULL,
  employee_id BIGINT NOT NULL,
  overtime_date DATE NOT NULL,
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  hours DECIMAL(8,2) NOT NULL,
  reason VARCHAR(500) NULL,
  approved_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_overtime_records_employee_date (employee_id, overtime_date),
  UNIQUE KEY uk_overtime_record_request (overtime_request_id)
);

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'overtime:apply', '加班申請', '提交個人加班申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'overtime:apply');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'overtime:view', '加班紀錄查看', '查看加班申請與加班紀錄', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'overtime:view');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'overtime:approve:department', '部門加班審核', '審核同部門員工加班申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'overtime:approve:department');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'overtime:approve:hr', '人資加班審核', '人資審核員工加班申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'overtime:approve:hr');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'overtime:approve:final', '高階加班核准', '高階主管最終核准加班申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'overtime:approve:final');
