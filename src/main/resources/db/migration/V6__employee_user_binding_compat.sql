CREATE TABLE IF NOT EXISTS user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY(user_id, role_id)
);

SET @copy_user_role_to_user_roles = (
  SELECT IF(
    COUNT(*) = 1,
    'INSERT IGNORE INTO user_roles (user_id, role_id) SELECT user_id, role_id FROM user_role',
    'SELECT 1'
  )
  FROM information_schema.tables
  WHERE table_schema = DATABASE()
    AND table_name = 'user_role'
);

PREPARE stmt FROM @copy_user_role_to_user_roles;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
