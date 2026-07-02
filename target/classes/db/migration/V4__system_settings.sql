CREATE TABLE IF NOT EXISTS system_settings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  setting_group VARCHAR(50) NOT NULL,
  setting_key VARCHAR(100) NOT NULL,
  setting_value VARCHAR(500) NOT NULL,
  value_type VARCHAR(30) NOT NULL DEFAULT 'STRING',
  description VARCHAR(255),
  updated_by VARCHAR(100),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_system_settings_key (setting_key),
  INDEX idx_system_settings_group (setting_group)
);

INSERT INTO system_settings (setting_group, setting_key, setting_value, value_type, description)
VALUES
  ('BASIC', 'company.name', 'Enterprise HR System', 'STRING', 'Company or system display name'),
  ('BASIC', 'work.standardStartTime', '09:00', 'TIME', 'Standard work start time'),
  ('BASIC', 'work.standardEndTime', '18:00', 'TIME', 'Standard work end time'),
  ('BASIC', 'work.dailyHours', '8', 'DECIMAL', 'Daily work hours'),
  ('BASIC', 'leave.minimumUnitHours', '1', 'DECIMAL', 'Minimum leave unit in hours'),
  ('BASIC', 'leave.allowHalfDay', 'true', 'BOOLEAN', 'Allow half-day leave'),
  ('BASIC', 'leave.allowOverdraft', 'false', 'BOOLEAN', 'Allow leave overdraft'),
  ('SECURITY', 'password.minLength', '8', 'INTEGER', 'Minimum password length'),
  ('SECURITY', 'password.requireLetter', 'true', 'BOOLEAN', 'Password requires letters'),
  ('SECURITY', 'password.requireNumber', 'true', 'BOOLEAN', 'Password requires numbers'),
  ('SECURITY', 'login.maxFailedAttempts', '5', 'INTEGER', 'Maximum failed login attempts'),
  ('SECURITY', 'login.lockMinutes', '15', 'INTEGER', 'Account lock minutes'),
  ('SECURITY', 'session.tokenExpireMinutes', '480', 'INTEGER', 'Token expiration minutes')
ON DUPLICATE KEY UPDATE setting_key = setting_key;

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'setting:view', '系統設定查看', '查看系統設定', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'setting:view');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'setting:update', '系統設定修改', '修改系統設定', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'setting:update');
