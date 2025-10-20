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
	PRIMARY KEY (name, inn),
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
	PRIMARY KEY (name, id),
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
	
-- Триггер для проверки доступности номера при бронировании
CREATE OR REPLACE FUNCTION check_room_available()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM rooms 
        WHERE hotel_code = NEW.hotel_code 
          AND room_number = NEW.room_number 
          AND available = TRUE
    ) THEN
        RAISE EXCEPTION 'Номер % в отеле % занят!', NEW.room_number, NEW.hotel_code;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_check_room_available
    BEFORE INSERT ON reservations
    FOR EACH ROW
    EXECUTE FUNCTION check_room_available();

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

-- Представление для анализа стоимости номеров по отелям
CREATE VIEW price_per_hotel AS
SELECT 
	hotel_code,
	count(room_number) AS amount_of_rooms,
	SUM(price) AS total_price
FROM rooms
GROUP BY 1;

-- Представление для идентификации крупных отелей
CREATE VIEW big_hotels AS
SELECT 
	r.hotel_code,
	h.name AS hotel_name,
	count( * ) AS amount_of_rooms
FROM rooms r JOIN hotels h ON r.hotel_code = h.hotel_code
GROUP BY 1, 2
HAVING count( * ) >= 30;

-- Представление для выявления популярных номеров
CREATE VIEW popular_rooms AS
SELECT
	r.hotel_code,
	h.name AS hotel_name,
	r.room_number,
	count( * ) AS amount_of_reservations
FROM reservations r JOIN hotels h ON r.hotel_code = h.hotel_code
GROUP BY 1, 2, 3
HAVING count(*) >= 5;

-- Представление для анализа структуры персонала по отелям
CREATE VIEW staff_info AS
WITH nt AS
(SELECT 
 	s.hotel_code AS hotel_code,
 	j.name AS job_name
 FROM staff s JOIN job_positions j ON s.job_code = j.job_code)
SELECT
	n.hotel_code,
	h.name AS hotel_name,
	n.job_name,
	count( * ) AS job_amount
FROM nt n JOIN hotels h ON n.hotel_code = h.hotel_code
GROUP BY 1, 2, 3;