INSERT INTO clinic (clinic_code, name, address, phone, email, inn)
SELECT 'CL-001',
       'Городская поликлиника №1',
       'ул. Ленина, 10',
       '+78001002030',
       'info@clinic1.ru',
       '123456789012' WHERE NOT EXISTS (SELECT 1 FROM clinic WHERE inn = '123456789012');

INSERT INTO clinic (clinic_code, name, address, phone, email, inn)
SELECT 'CL-002',
       'Медицинский центр Здоровье',
       'пр. Мира, 25',
       '+78002003040',
       'info@clinic2.ru',
       '210987654321' WHERE NOT EXISTS (SELECT 1 FROM clinic WHERE inn = '210987654321');

INSERT INTO employee (full_name, phone, email, hire_date, role, specialization, license_number, clinic_id)
SELECT 'Петров Петр Петрович',
       '+79001112233',
       'petrov@clinic1.ru',
       '2024-01-15',
       'DOCTOR',
       'Терапевт',
       'LIC-001',
       1 WHERE NOT EXISTS (SELECT 1 FROM employee WHERE email = 'petrov@clinic1.ru');

INSERT INTO employee (full_name, phone, email, hire_date, role, specialization, license_number, clinic_id)
SELECT 'Иванова Анна Сергеевна',
       '+79002223344',
       'ivanova@clinic1.ru',
       '2024-02-01',
       'DOCTOR',
       'Кардиолог',
       'LIC-002',
       1 WHERE NOT EXISTS (SELECT 1 FROM employee WHERE email = 'ivanova@clinic1.ru');

INSERT INTO employee (full_name, phone, email, hire_date, role, clinic_id)
SELECT 'Смирнова Ольга Игоревна',
       '+79003334455',
       'smirnova@clinic1.ru',
       '2024-01-10',
       'ADMIN',
       1 WHERE NOT EXISTS (SELECT 1 FROM employee WHERE email = 'smirnova@clinic1.ru');

INSERT INTO patient (full_name, phone, email, birth_date, medical_card_number, notes, clinic_id)
SELECT 'Сидоров Алексей Валерьевич',
       '+79004445566',
       'sidorov@mail.ru',
       '1985-05-20',
       'MC-001',
       'Аллергия на пенициллин',
       1 WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'sidorov@mail.ru');

INSERT INTO patient (full_name, phone, email, birth_date, medical_card_number, clinic_id)
SELECT 'Козлова Мария Дмитриевна',
       '+79005556677',
       'kozlova@mail.ru',
       '1990-08-15',
       'MC-002',
       1 WHERE NOT EXISTS (SELECT 1 FROM patient WHERE email = 'kozlova@mail.ru');

INSERT INTO medical_service (title, description, cost, duration_minutes, clinic_id)
SELECT 'Прием терапевта',
       'Первичный прием врача-терапевта',
       1500,
       30,
       1 WHERE NOT EXISTS (SELECT 1 FROM medical_service WHERE title = 'Прием терапевта' AND clinic_id = 1);

INSERT INTO medical_service (title, description, cost, duration_minutes, clinic_id)
SELECT 'Прием кардиолога',
       'Консультация врача-кардиолога',
       2000,
       45,
       1 WHERE NOT EXISTS (SELECT 1 FROM medical_service WHERE title = 'Прием кардиолога' AND clinic_id = 1);

INSERT INTO medical_service (title, description, cost, duration_minutes, clinic_id)
SELECT 'Общий анализ крови',
       'Забор крови и лабораторное исследование',
       500,
       15,
       1 WHERE NOT EXISTS (SELECT 1 FROM medical_service WHERE title = 'Общий анализ крови' AND clinic_id = 1);