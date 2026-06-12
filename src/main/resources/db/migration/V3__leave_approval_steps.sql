CREATE TABLE IF NOT EXISTS leave_approval_steps (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  leave_request_id BIGINT NOT NULL,
  step_order INT NOT NULL,
  step_code VARCHAR(50) NOT NULL,
  approver_role VARCHAR(50) NOT NULL,
  approver_user_id BIGINT NULL,
  status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
  comment VARCHAR(500) NULL,
  approved_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_leave_approval_request_order (leave_request_id, step_order),
  INDEX idx_leave_approval_status (status, approver_role)
);

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'leave:approve:department', '部門請假審核', '審核同部門員工請假申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'leave:approve:department');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'leave:approve:hr', '人資請假審核', '人資審核員工請假申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'leave:approve:hr');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'leave:approve:final', '高階請假核准', '高階主管最終核准特殊請假申請', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'leave:approve:final');
