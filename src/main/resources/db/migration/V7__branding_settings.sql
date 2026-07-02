INSERT INTO system_settings (setting_group, setting_key, setting_value, value_type, description)
VALUES
  ('BASIC', 'company.logoUrl', '', 'STRING', 'Company logo URL'),
  ('BASIC', 'company.faviconUrl', '', 'STRING', 'Browser favicon URL')
ON DUPLICATE KEY UPDATE setting_key = setting_key;
