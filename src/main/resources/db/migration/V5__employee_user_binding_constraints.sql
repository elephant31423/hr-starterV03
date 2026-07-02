ALTER TABLE employee
  ADD UNIQUE KEY uk_employee_user_id (user_id);

ALTER TABLE users
  ADD UNIQUE KEY uk_users_employee_id (employee_id);
