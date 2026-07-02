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
  ('BASIC', 'company.name', 'Enterprise HR System', 'STRING', '公司或系統顯示名稱'),
  ('BASIC', 'work.standardStartTime', '09:00', 'TIME', '預設上班時間'),
  ('BASIC', 'work.standardEndTime', '18:00', 'TIME', '預設下班時間'),
  ('BASIC', 'work.dailyHours', '8', 'DECIMAL', '每日標準工時'),
  ('BASIC', 'leave.minimumUnitHours', '1', 'DECIMAL', '最小請假單位，小時'),
  ('BASIC', 'leave.allowHalfDay', 'true', 'BOOLEAN', '是否允許半日請假'),
  ('BASIC', 'leave.allowOverdraft', 'false', 'BOOLEAN', '是否允許超額請假'),
  ('SECURITY', 'password.minLength', '8', 'INTEGER', '密碼最小長度'),
  ('SECURITY', 'password.requireLetter', 'true', 'BOOLEAN', '密碼是否必須包含英文字母'),
  ('SECURITY', 'password.requireNumber', 'true', 'BOOLEAN', '密碼是否必須包含數字'),
  ('SECURITY', 'login.maxFailedAttempts', '5', 'INTEGER', '登入失敗鎖定次數'),
  ('SECURITY', 'login.lockMinutes', '15', 'INTEGER', '登入鎖定分鐘數'),
  ('SECURITY', 'session.tokenExpireMinutes', '480', 'INTEGER', 'Token 有效分鐘數')
ON DUPLICATE KEY UPDATE setting_key = setting_key;

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'setting:view', '系統設定查看', '查看系統設定', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'setting:view');

INSERT INTO permissions (permission_code, permission_name, description, parent_id)
SELECT 'setting:update', '系統設定維護', '修改系統設定', NULL
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission_code = 'setting:update');
