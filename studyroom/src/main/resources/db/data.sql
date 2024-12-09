-- INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
-- VALUES
--     (000000000, 'admin', 'admin', 'admin', 'admin@pusan.ac.kr', 'PROF', NOW(), NOW());
-- INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
-- VALUES
--     (111111111, 'std', 'std', 'std', 'std@pusan.ac.kr', 'STD', NOW(), NOW());
-- INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
-- VALUES
--     (222222222, 'ta', 'ta', 'ta', 'ta@pusan.ac.kr', 'TA', NOW(), NOW());
-- INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
-- VALUES
--     (333333333, 'lead', 'lead', 'lead', 'lead@pusan.ac.kr', 'LEAD', NOW(), NOW());
--
-- LectureList 테이블 초기 데이터 삽입

INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (0, 'prof', 'Prof', '1234', 'admin@pusan.ac.kr', 'PROF', NOW(), NOW());

INSERT INTO lecture_lists (lecture_id, name, std_id_id, date_created, last_updated)
VALUES
    (1, 'DataBase', 111111111, NOW(), NOW()),
    (2, 'DataStructures', 111111111, NOW(), NOW());

INSERT INTO Users (std_id, name, nickname, password, email, grade)
VALUES (1, '조교', 'ta', '1234', 'john@example.com', NULL);

SELECT * FROM Users WHERE std_id = 1;

INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (3, 'prof', 'Prof', '1234', 'prof@pusan.ac.kr', 'PROF', NOW(), NOW());

INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (4, 'Ta', 'Ta', '1234', 'Ta@pusan.ac.kr', 'TA', NOW(), NOW());

INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (111111111, '학생1', 'std1', '1234', '1234@pusan.ac.kr',null, NOW(), NOW());

INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (222222222, '리더', 'lead1', '1234', '1234@pusan.ac.kr',null, NOW(), NOW());

UPDATE Users
SET grade = 'PROF'
WHERE std_id = 0;

-- GroupUser 테이블 내용 삭제
DELETE FROM group_user;

-- GroupProject 테이블 내용 삭제
DELETE FROM group_projects;
