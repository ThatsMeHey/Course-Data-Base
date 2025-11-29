-- Таблица отелей - хранит основную информацию о гостиницах
CREATE TABLE hotels
(
	hotel_code TEXT PRIMARY KEY,
	name TEXT NOT NULL UNIQUE,
	inn text NOT NULL UNIQUE,
	director text NOT NULL,
	owner text NOT NULL,
	address text NOT NULL UNIQUE
);

-- Таблица должностей - справочник возможных должностей сотрудников
CREATE TABLE job_positions
(
	job_code TEXT PRIMARY KEY,
	name TEXT NOT NULL UNIQUE
);

-- Таблица сотрудников - информация о работниках отелей
CREATE TABLE staff
(
	name TEXT NOT NULL,
	inn TEXT NOT NULL UNIQUE,
	hotel_code TEXT NOT NULL,
	job_code TEXT NOT NULL,
	PRIMARY KEY (inn),
	FOREIGN KEY (hotel_code) REFERENCES hotels(hotel_code) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (job_code) REFERENCES job_positions(job_code) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица номеров - информация о комнатах в отелях
CREATE TABLE rooms
(
	hotel_code TEXT,
	room_number SMALLINT CHECK (room_number > 0) NOT NULL,
	description text NOT NULL,
	price NUMERIC (10, 2) CHECK (price > 0) NOT NULL,
	available BOOLEAN DEFAULT TRUE NOT NULL,
	PRIMARY KEY (hotel_code, room_number),
	FOREIGN KEY (hotel_code) REFERENCES hotels(hotel_code) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица постояльцев - информация о текущих гостях отелей
CREATE TABLE visitors
(
	name TEXT NOT NULL,
	id SERIAL,
	hotel_code TEXT NOT NULL,
	room_number SMALLINT NOT NULL,
	arrival_date TIMESTAMP NOT NULL,
	departure_date TIMESTAMP CHECK (departure_date > arrival_date) NOT NULL,
	description TEXT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (hotel_code, room_number) REFERENCES rooms(hotel_code, room_number) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Таблица бронирований - информация о забронированных номерах
CREATE TABLE reservations
(
	name TEXT NOT NULL,
	id SERIAL,
	hotel_code TEXT NOT NULL,
	room_number SMALLINT NOT NULL,
	arrival_date TIMESTAMP NOT NULL,
	departure_date TIMESTAMP CHECK (departure_date > arrival_date) NOT NULL,
	description TEXT NOT NULL,
	PRIMARY KEY (id),
	FOREIGN KEY (hotel_code, room_number) REFERENCES rooms(hotel_code, room_number) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Индексы для оптимизации поиска сотрудников
CREATE INDEX idx_staff_search ON staff(hotel_code, job_code);

-- Индекс для поиска номеров по цене
CREATE INDEX idx_rooms_search_by_price ON rooms(price);

-- Индексы для оптимизации работы с постояльцами
CREATE INDEX idx_visitors_hotel_room ON visitors(hotel_code, room_number);
CREATE INDEX idx_visitors_search_by_arrival_date ON visitors(arrival_date);
CREATE INDEX idx_visitors_search_by_departure_date ON visitors(departure_date);

-- Индексы для оптимизации работы с бронированиями
CREATE INDEX idx_reservations_search_by_hotel_room ON reservations(hotel_code, room_number);
CREATE INDEX idx_reservations_search_by_arrival_date ON reservations(arrival_date);
CREATE INDEX idx_reservations_search_by_departure_date ON reservations(departure_date);

-- Триггер для автоматического занятия номера при заселении гостя
CREATE OR REPLACE FUNCTION occupy_room_on_checkin()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE rooms 
    SET available = FALSE
    WHERE hotel_code = NEW.hotel_code AND room_number = NEW.room_number;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_occupy_room
    AFTER INSERT ON visitors
    FOR EACH ROW
    EXECUTE FUNCTION occupy_room_on_checkin();
	
-- Триггер для автоматического освобождения номера при выселении гостя
CREATE OR REPLACE FUNCTION free_room_on_checkout()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE rooms 
    SET available = TRUE
    WHERE hotel_code = OLD.hotel_code AND room_number = OLD.room_number;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_free_room
    AFTER DELETE ON visitors
    FOR EACH ROW
    EXECUTE FUNCTION free_room_on_checkout();
	
-- Представление для анализа продолжительности проживания гостей
CREATE VIEW visitors_time AS
SELECT 
	id, 
	name, 
	hotel_code, 
	room_number, 
	arrival_date,
	departure_date,
	departure_date - arrival_date AS time_of_residence
FROM visitors;








CREATE VIEW hotel_statistics AS
SELECT 
    h.hotel_code,
    h.name AS hotel_name,
    COUNT(DISTINCT s.inn) AS staff_count,
    COUNT(DISTINCT r.room_number) AS total_rooms,
    COUNT(DISTINCT CASE WHEN r.available = TRUE THEN r.room_number END) AS available_rooms,
    COUNT(DISTINCT v.id) AS current_visitors
FROM hotels h
LEFT JOIN staff s ON h.hotel_code = s.hotel_code
LEFT JOIN rooms r ON h.hotel_code = r.hotel_code
LEFT JOIN visitors v ON h.hotel_code = v.hotel_code
GROUP BY h.hotel_code, h.name, h.address;


CREATE VIEW room_occupancy_analysis AS
SELECT 
    h.hotel_code,
    h.name AS hotel_name,
    COUNT(r.room_number) AS total_rooms,
    COUNT(CASE WHEN r.available = TRUE THEN r.room_number END) AS available_rooms,
    COUNT(CASE WHEN r.available = FALSE THEN r.room_number END) AS occupied_rooms,
    COUNT(v.id) AS current_visitors_count,
    COUNT(res.id) AS active_reservations_count,
    ROUND(
        COUNT(CASE WHEN r.available = FALSE THEN r.room_number END) * 100.0 / 
        NULLIF(COUNT(r.room_number), 0), 
        2
    ) AS occupancy_rate_percent
FROM hotels h
LEFT JOIN rooms r ON h.hotel_code = r.hotel_code
LEFT JOIN visitors v ON h.hotel_code = v.hotel_code AND r.room_number = v.room_number
LEFT JOIN reservations res ON h.hotel_code = res.hotel_code AND r.room_number = res.room_number
GROUP BY h.hotel_code, h.name;


CREATE OR REPLACE VIEW hotel_financial_analysis AS
SELECT 
  h.hotel_code,
  h.name AS hotel_name,
  r.total_rooms,
  COALESCE(r.total_potential_revenue, 0) AS total_potential_revenue,
  ROUND(COALESCE(r.average_room_price, 0)::numeric, 2) AS average_room_price,
  r.min_room_price,
  r.max_room_price,
  COALESCE(r.current_daily_revenue, 0) AS current_daily_revenue,
  COALESCE(s.total_staff, 0) AS total_staff,
  COALESCE(p.unique_positions_count, 0) AS unique_positions_count
FROM hotels h
INNER JOIN (
  SELECT hotel_code,
         COUNT(*) AS total_rooms,
         SUM(price) AS total_potential_revenue,
         AVG(price) AS average_room_price,
         MIN(price) AS min_room_price,
         MAX(price) AS max_room_price,
         SUM(CASE WHEN available = FALSE THEN price ELSE 0 END) AS current_daily_revenue
  FROM rooms
  GROUP BY hotel_code
  HAVING COUNT(*) > 0
) r ON r.hotel_code = h.hotel_code
LEFT JOIN (
  SELECT hotel_code,
         COUNT(DISTINCT inn) AS total_staff
  FROM staff
  GROUP BY hotel_code
) s ON s.hotel_code = h.hotel_code
LEFT JOIN (
  SELECT hotel_code,
         COUNT(DISTINCT job_code) AS unique_positions_count
  FROM staff
  GROUP BY hotel_code
) p ON p.hotel_code = h.hotel_code;











CREATE VIEW staff_view AS
SELECT 
    s.name,
    s.inn,
    s.hotel_code,
    h.name AS hotel_name,
    s.job_code,
    j.name AS job_name
FROM staff s
    JOIN hotels h ON s.hotel_code = h.hotel_code
    JOIN job_positions j ON s.job_code = j.job_code;

CREATE VIEW rooms_view AS
SELECT 
    r.hotel_code,
    h.name AS hotel_name,
    r.room_number,
    r.description,
    r.price,
    r.available
FROM rooms r
    JOIN hotels h ON r.hotel_code = h.hotel_code;

CREATE VIEW visitors_view AS
SELECT 
    v.name,
    v.id,
    v.hotel_code,
    h.name AS hotel_name,
    v.room_number,
    v.arrival_date,
    v.departure_date,
    v.description
FROM visitors v
    JOIN hotels h ON v.hotel_code = h.hotel_code;

CREATE VIEW reservations_view AS
SELECT 
    r.name,
    r.id,
    r.hotel_code,
    h.name AS hotel_name,
    r.room_number,
    r.arrival_date,
    r.departure_date,
    r.description
FROM reservations r
    JOIN hotels h ON r.hotel_code = h.hotel_code;



CREATE OR REPLACE FUNCTION auto_checkout_and_checkin()
RETURNS void AS $$
DECLARE
    rec RECORD;
BEGIN
    -- 1. Удаляем всех посетителей, у которых дата выезда прошла
    DELETE FROM visitors
    WHERE departure_date < NOW();
    
    -- 2. Заселяем гостей из бронирований, если текущая дата в диапазоне И комната свободна
    FOR rec IN
        SELECT r.name, r.hotel_code, r.room_number, r.arrival_date, r.departure_date, r.description
        FROM reservations r
        WHERE NOW() >= r.arrival_date
          AND NOW() < r.departure_date
          AND NOT EXISTS (
              SELECT 1 FROM visitors v
              WHERE v.hotel_code = r.hotel_code
                AND v.room_number = r.room_number
          )
          AND EXISTS (
              SELECT 1 FROM rooms rm
              WHERE rm.hotel_code = r.hotel_code
                AND rm.room_number = r.room_number
                AND rm.available = TRUE
          )
    LOOP
        INSERT INTO visitors (name, hotel_code, room_number, arrival_date, departure_date, description)
        VALUES (
            rec.name,
            rec.hotel_code,
            rec.room_number,
            rec.arrival_date,
            rec.departure_date,
            rec.description
        );
        
        -- Удаляем бронь после успешного заселения
        DELETE FROM reservations
        WHERE hotel_code = rec.hotel_code
          AND room_number = rec.room_number
          AND arrival_date = rec.arrival_date
          AND name = rec.name;
    END LOOP;
    
    -- 3. Удаление брони, если текущая дата в диапазоне, но комната занята
    DELETE FROM reservations r
    WHERE NOW() >= r.arrival_date
      AND NOW() < r.departure_date
      AND EXISTS (
          SELECT 1 FROM visitors v
          WHERE v.hotel_code = r.hotel_code
            AND v.room_number = r.room_number
      );
    
    -- 4. Удаление брони, если текущая дата больше даты выселения
    DELETE FROM reservations
    WHERE NOW() >= departure_date;
    
END;
$$ LANGUAGE plpgsql;