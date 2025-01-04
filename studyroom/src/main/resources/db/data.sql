INSERT INTO Users (std_id, name, nickname, password, email, grade, date_created, last_updated)
VALUES
    (45, '학생1', '생1', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std1@pusan.ac.kr', 'STD', NOW(), NOW()),
    (46, '학생2', '생2', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std2@pusan.ac.kr', 'STD', NOW(), NOW()),
    (47, '학생3', '생3', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std3@pusan.ac.kr', 'STD', NOW(), NOW()),
    (48, '학생4', '생4', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std4@pusan.ac.kr', 'STD', NOW(), NOW()),
    (49, '학생5', '생5', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std5@pusan.ac.kr', 'STD', NOW(), NOW()),
    (50, '학생6', '생6', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std6@pusan.ac.kr', 'STD', NOW(), NOW()),
    (51, '학생7', '생7', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std7@pusan.ac.kr', 'STD', NOW(), NOW()),
    (52, '학생8', '생8', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std8@pusan.ac.kr', 'STD', NOW(), NOW()),
    (53, '학생9', '생9', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std9@pusan.ac.kr', 'STD', NOW(), NOW()),
    (54, '학생10', '생10', '$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G', 'std10@pusan.ac.kr', 'STD', NOW(), NOW()),
    (1,'교수','교수님','$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G','prof@prof','PROF',now(),now()),
    (2,'조교','조교1','$2a$10$zvgyqFdymsnZDE5z.F9yZ.IyzdXTzWNGWv1xI0G220zmZ2vJpmV2G','ta@ta','TA',now(),now());

INSERT INTO lecture_lists(lecture_id, std_id_id, date_created, last_updated, name)
VALUES (1,1,now(),now(),'database'),
       (2,1,now(),now(),'file');

INSERT INTO lecture_user(lecturelist_id, std_id)
VALUES (1,45),
       (1,46),
       (1,47),
       (1,48),
       (2,45),
       (2,46),
       (2,47),
       (2,48),
       (2,49),
       (2,50);
-- SELECT * FROM Users WHERE std_id = 1;
--
-- INSERT INTO group_projects (gp_id, group_valid, group_name, group_detail, created_user_id_id, lecture_id_id, date_created, last_updated)
-- VALUES (nextval('primary_sequence_gp'), FALSE, 'GroupName1', 'This is a group detail.', 45, 1, NOW(), NOW()),
--  (nextval('primary_sequence_gp'), FALSE, 'GroupName1', 'This is a group detail.', 45, 2, NOW(), NOW());
--


