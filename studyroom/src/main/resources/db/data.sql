INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (45, '김예슬', '예쓸', '1234', 'Ys@', 'STD', NOW(), NOW()),
    (46, '김예슬1', '예쓸1', '1234', 'Ys1@', 'STD', NOW(), NOW()),
    (47, '김예슬2', '예쓸2', '1234', 'Ys2@', 'STD', NOW(), NOW()),
    (48, '김예슬3', '예쓸3', '1234', 'Ys3@', 'STD', NOW(), NOW()),
    (333333333, '리더', 'lead1', '1234', 'lead1@',null, NOW(), NOW());

INSERT INTO lecture_lists (lecture_id, name, std_id_id, date_created, last_updated)
VALUES
    (1, 'DataBase', 111111111, NOW(), NOW()),
    (2, 'DataStructures', 111111111, NOW(), NOW());

-- SELECT * FROM Users WHERE std_id = 1;
--
-- INSERT INTO group_projects (gp_id, group_valid, group_name, group_detail, created_user_id_id, lecture_id_id, date_created, last_updated)
-- VALUES (nextval('primary_sequence_gp'), FALSE, 'GroupName1', 'This is a group detail.', 45, 1, NOW(), NOW()),
--  (nextval('primary_sequence_gp'), FALSE, 'GroupName1', 'This is a group detail.', 45, 2, NOW(), NOW());
--


