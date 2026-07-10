CREATE TABLE IF NOT EXISTS audit_logs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  username VARCHAR(100) NULL,
  action VARCHAR(50) NOT NULL,
  entity_type VARCHAR(100) NULL,
  entity_id BIGINT NULL,
  old_value LONGTEXT NULL,
  new_value LONGTEXT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'SUCCESS',
  error_message TEXT NULL,
  ip_address VARCHAR(100) NULL,
  user_agent VARCHAR(500) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_audit_logs_created_at (created_at),
  INDEX idx_audit_logs_username (username),
  INDEX idx_audit_logs_action (action),
  INDEX idx_audit_logs_entity (entity_type, entity_id)
);

CREATE TABLE IF NOT EXISTS holidays (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  `date` DATE NOT NULL,
  `name` VARCHAR(100) NOT NULL,
  is_national TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_holidays_date (date)
);

CREATE TABLE IF NOT EXISTS work_schedules (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  work_date DATE NOT NULL,
  shift_id BIGINT NULL,
  start_time VARCHAR(20) NULL,
  end_time VARCHAR(20) NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  is_on_call TINYINT(1) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  notes VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_work_schedules_employee_date (employee_id, work_date),
  INDEX idx_work_schedules_date (work_date),
  INDEX idx_work_schedules_shift (shift_id)
);

SET @add_departments_location = (
  SELECT IF(COUNT(*) = 0, 'ALTER TABLE departments ADD COLUMN location VARCHAR(100) NULL', 'SELECT 1')
  FROM information_schema.columns
  WHERE table_schema = DATABASE() AND table_name = 'departments' AND column_name = 'location'
);
PREPARE stmt FROM @add_departments_location;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
