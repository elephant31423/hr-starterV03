CREATE TABLE IF NOT EXISTS shifts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NULL,
  shift_date DATE NULL,
  shift_code BIGINT NULL,
  shift_name VARCHAR(100) NOT NULL DEFAULT '',
  start_time VARCHAR(20) NULL,
  end_time VARCHAR(20) NULL,
  is_night VARCHAR(10) NULL,
  color VARCHAR(30) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_shifts_employee_date (employee_id, shift_date)
);

CREATE TABLE IF NOT EXISTS employee_shifts (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  employee_id BIGINT NOT NULL,
  shift_id BIGINT NULL,
  shift_date DATE NOT NULL,
  shift_type VARCHAR(30) NOT NULL DEFAULT 'OFF',
  on_duty TINYINT(1) NOT NULL DEFAULT 0,
  remark VARCHAR(255) NULL,
  created_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_by VARCHAR(100) NULL,
  updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  UNIQUE KEY uk_employee_shifts_employee_date (employee_id, shift_date),
  INDEX idx_employee_shifts_date (shift_date),
  INDEX idx_employee_shifts_type (shift_type),
  INDEX idx_employee_shifts_shift (shift_id)
);

INSERT INTO shifts (shift_code, shift_name, start_time, end_time, is_night, color)
SELECT 1, '早班', '09:00', '18:00', 'N', '#22c55e'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = '早班');

INSERT INTO shifts (shift_code, shift_name, start_time, end_time, is_night, color)
SELECT 2, '中班', '14:00', '23:00', 'N', '#3b82f6'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = '中班');

INSERT INTO shifts (shift_code, shift_name, start_time, end_time, is_night, color)
SELECT 3, '夜班', '23:00', '08:00', 'Y', '#8b5cf6'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = '夜班');

INSERT INTO shifts (shift_code, shift_name, start_time, end_time, is_night, color)
SELECT 4, '休假', NULL, NULL, 'N', '#64748b'
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE shift_name = '休假');
