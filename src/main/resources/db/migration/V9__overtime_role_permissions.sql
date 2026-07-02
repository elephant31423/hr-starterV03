INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN (
  'overtime:apply',
  'overtime:view',
  'overtime:approve:department',
  'overtime:approve:hr',
  'overtime:approve:final'
)
WHERE r.role_key = 'ADMIN';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN ('overtime:apply')
WHERE r.role_key = 'EMPLOYEE';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN ('overtime:view', 'overtime:approve:department')
WHERE r.role_key = 'DEPARTMENT_MANAGER';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN ('overtime:view', 'overtime:approve:hr')
WHERE r.role_key = 'HR';

INSERT IGNORE INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
JOIN permissions p ON p.permission_code IN ('overtime:view', 'overtime:approve:final')
WHERE r.role_key = 'EXECUTIVE';
