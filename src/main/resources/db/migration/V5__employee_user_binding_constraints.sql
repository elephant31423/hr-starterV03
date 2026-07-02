SET @add_employee_id_to_users = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE users ADD COLUMN employee_id BIGINT NULL',
    'SELECT 1'
  )
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND column_name = 'employee_id'
);

PREPARE stmt FROM @add_employee_id_to_users;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_employee_user_unique = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE employee ADD UNIQUE KEY uk_employee_user_id (user_id)',
    'SELECT 1'
  )
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'employee'
    AND index_name = 'uk_employee_user_id'
);

PREPARE stmt FROM @add_employee_user_unique;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_users_employee_unique = (
  SELECT IF(
    COUNT(*) = 0,
    'ALTER TABLE users ADD UNIQUE KEY uk_users_employee_id (employee_id)',
    'SELECT 1'
  )
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'users'
    AND index_name = 'uk_users_employee_id'
);

PREPARE stmt FROM @add_users_employee_unique;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
