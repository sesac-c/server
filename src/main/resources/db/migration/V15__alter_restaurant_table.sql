-- 1. address_id 컬럼 삭제 (존재하는 경우에만)
    SET @exist := (SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
    AND table_name = 'restaurant'
    AND column_name = 'address_id');

SET @sql = IF(@exist > 0,
    'ALTER TABLE restaurant DROP COLUMN address_id',
    'SELECT "Column address_id does not exist in restaurant table, skipping drop" AS message');

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
-- 2. longitude 컬럼 타입 변경 (BigDecimal에서 VARCHAR로)
ALTER TABLE restaurant MODIFY COLUMN longitude VARCHAR(255) NOT NULL;

-- 3. latitude 컬럼 타입 변경 (BigDecimal에서 VARCHAR로)
ALTER TABLE restaurant MODIFY COLUMN latitude VARCHAR(255) NOT NULL;