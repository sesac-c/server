SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE()
      AND TABLE_NAME = 'campus'
      AND CONSTRAINT_NAME = 'unique_name'
);

SET @sql = IF(@constraint_exists = 0,
    'ALTER TABLE campus ADD CONSTRAINT unique_name UNIQUE (name)',
    'SELECT "Constraint already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;