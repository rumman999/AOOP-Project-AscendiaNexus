-- Create database and switch to it
CREATE DATABASE IF NOT EXISTS `java_user_database`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE=utf8mb4_general_ci;
USE `java_user_database`;

-- Table: groups
DROP TABLE IF EXISTS `groups`;
CREATE TABLE `groups` (
                          `id` INT(11) NOT NULL AUTO_INCREMENT,
                          `name` VARCHAR(100) NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
INSERT INTO `groups` (`id`, `name`) VALUES
                                        (1, 'abc123'),
                                        (2, 'UIU Squad'),
                                        (3, 'Toxic'),
                                        (4, 'BestOFBest'),
                                        (5, 'ABC SQUAD'),
                                        (6, '456AS'),
                                        (7, 'QWERTY'),
                                        (8, 'QUERTY'),
                                        (9, 'ds'),
                                        (10, 'dED'),
                                        (11, 'asdw');

-- Table: user
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
                        `id` INT(11) NOT NULL AUTO_INCREMENT,
                        `full_name` VARCHAR(127) NOT NULL,
                        `email` VARCHAR(50) NOT NULL,
                        `password` VARCHAR(50) NOT NULL,
                        `phone_number` VARCHAR(11) NOT NULL,
                        `account_type` VARCHAR(50) NOT NULL,
                        `login` TINYINT(1) NOT NULL DEFAULT 0,
                        `DOB` DATE DEFAULT NULL,
                        `Address` VARCHAR(255) DEFAULT NULL,
                        `todo_list` LONGTEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
                        `profile_pic` VARCHAR(255) DEFAULT NULL,
                        `UUID` VARCHAR(36) NOT NULL,
                        `Coin` INT(11) DEFAULT NULL,
                        `Age` INT(11) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
INSERT INTO `user` (`full_name`, `email`, `password`, `phone_number`, `account_type`, `id`, `login`, `DOB`, `Address`, `todo_list`, `profile_pic`, `UUID`, `Coin`, `Age`) VALUES
                                                                                                                                                                              ('Torongo Evence Rozario', 'imtorongo@gmail.com', '#23Torongo', '01312025776', 'Admin', 1, 0, '2003-12-23', '56/B East Rajabazar, Tejgaon Dhaka 1215', '[]', 'file:/D:/IMG_0422.png', 'b07b4204-2c4e-4412-a605-648a05f1f256', 0, 21),
                                                                                                                                                                              ('Muntasir Tofa', 'tofa121@gmail.com', 'T@1234r', '01312523555', 'Recruiter', 2, 0, NULL, '', '[]', 'file:/D:/shield-alt-2.png', '56bb2b1d-c5a4-4ccf-8ab6-9ff755833aad', 0, 0),
                                                                                                                                                                              ('Adnan', 'adnans@yahoo.com', 'B23c23@', '01334567899', 'Jobseeker', 4, 0, NULL, '', '', '', 'c8af9a0a-8177-4a37-a8f2-f2d018aff33d', 0, 0),
                                                                                                                                                                              ('Dibbo Kumar', 'dk@yahoo.com', 'DK123@k', '01734567896', 'Recruiter', 5, 0, NULL, '', '', 'file:/D:/1-removebg-preview.png', '2238e8e5-5ee8-43d6-aec7-76f919af8bd4', 0, 0),
                                                                                                                                                                              ('Maruf Hossain', 'abc@gmail.com', 'Ma#21ac', '01324567897', 'Jobseeker', 6, 0, NULL, '', '', 'file:/D:/1-removebg-preview.png', '5f6bb25b-2264-4ba7-9ab6-9211f48c01f3', 0, 0),
                                                                                                                                                                              ('Evence Rozario', 'torongoxd@gmail.com', '23torongo', '01618528315', 'Recruiter', 8, 0, NULL, '', '', '', 'd6844e44-28ae-4d51-b832-c25d94214020', 0, 0),
                                                                                                                                                                              ('Rozario', 'torongo121@gmail.com', '12#$ToO', '01720025776', 'Jobseeker', 9, 0, NULL, '', '', '', 'aede313c-ac7f-4582-8c0b-9a9e080516c9', 0, 0),
                                                                                                                                                                              ('Tango Man', 'tango101@yahoo.com', '@23Toro', '01378945612', 'Jobseeker', 10, 0, NULL, '', '', '', 'a0f022c4-9546-4d83-be96-f4d78508c759', 0, 0),
                                                                                                                                                                              ('EvenceR', 'imtorono@gmail.com', '#23Torongo', '01312025777', 'Jobseeker', 11, 0, NULL, '', '', '', '98418003-7fdd-46e8-8aff-7fb85d50f9c5', 0, 0),
                                                                                                                                                                              ('Alvinur', 'alvi121@gmail.com', '123@123Aa', '01378945611', 'Admin', 12, 0, NULL, '', '', '', '', 0, 0),
                                                                                                                                                                              ('Rumman Karim', 'rumman@gmail.com', '!12Rumman', '01534567899', 'Admin', 13, 0, NULL, NULL, NULL, NULL, '52b1d351-459f-4b12-a24f-b2117f5b2246', NULL, NULL),
                                                                                                                                                                              ('abc', 'abc@gmail.com', '!12Abcd', '000', 'Admin', 14, 0, NULL, NULL, NULL, NULL, '123', NULL, NULL),
                                                                                                                                                                              ('Alvinur', 'alvi121@gmail.com', '123@123Aa', '01378945611', 'Admin', 15, 0, NULL, '', '', '', '12d5453b-d1a0-451e-bf75-30e8eb047b0b', 0, 0);

-- Table: group_members
DROP TABLE IF EXISTS `group_members`;
CREATE TABLE `group_members` (
                                 `group_id` INT(11) NOT NULL,
                                 `user_id` INT(11) NOT NULL,
                                 PRIMARY KEY (`group_id`,`user_id`),
                                 KEY `idx_group_members_user` (`user_id`),
                                 CONSTRAINT `group_members_ibfk_1`
                                     FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
                                 CONSTRAINT `group_members_ibfk_2`
                                     FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
INSERT INTO `group_members` (`group_id`, `user_id`) VALUES
                                                        (1, 1),(1, 2),(1, 4),(1, 6),(2, 1),(2, 4),(2, 8),
                                                        (3, 1),(3, 2),(3, 8),(3, 9),(4, 2),(4, 4),(4, 6),(4, 9),(4, 11),
                                                        (5, 1),(5, 2),(5, 6),(5, 10),(6, 1),(6, 4),(6, 5),(6, 9),(6, 11),
                                                        (7, 1),(7, 8),(8, 1),(8, 2),(8, 4),(8, 6),(9, 2),(9, 5),
                                                        (10, 2),(10, 5),(10, 6),(11, 1),(11, 5),(11, 6);

-- Table: job
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
                       `id` INT(11) NOT NULL AUTO_INCREMENT,
                       `poster_id` INT(11) NOT NULL,
                       `title` VARCHAR(150) NOT NULL,
                       `description` TEXT NOT NULL,
                       `tech_stack` TEXT DEFAULT NULL,
                       `job_type` VARCHAR(100) DEFAULT NULL,
                       `requirements` TEXT DEFAULT NULL,
                       `location` VARCHAR(100) DEFAULT NULL,
                       `salary_range` VARCHAR(50) DEFAULT NULL,
                       `posted_at` DATETIME DEFAULT CURRENT_TIMESTAMP(),
                       PRIMARY KEY (`id`),
                       KEY `idx_job_poster` (`poster_id`),
                       CONSTRAINT `fk_job_poster`
                           FOREIGN KEY (`poster_id`) REFERENCES `user` (`id`)
                               ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
INSERT INTO `job` (`id`, `poster_id`, `title`, `description`, `tech_stack`, `job_type`, `requirements`, `location`, `salary_range`, `posted_at`) VALUES
                                                                                                                                                     (1, 1, 'Job 1', 'A job very very job', NULL, NULL, NULL, 'Gulshan', '700,000', '2025-10-02 18:00:33'),
                                                                                                                                                     (3, 1, 'dsa', 'asd', 'adsa', 'ad', 'asd', 'adsasd', '25', '2025-10-04 10:56:41'),
                                                                                                                                                     (4, 1, 'ds', 'sad', 'sad', 'adsasd', 'ad', 'dsa', 'sad', '2025-10-04 10:57:12'),
                                                                                                                                                     (5, 5, 'recruiter job', 'good', 'javagood', 'Full time', 'be cool', 'Dhaka', '50k', '2025-10-04 11:00:46');

-- Table: job_applications
DROP TABLE IF EXISTS `job_applications`;
CREATE TABLE `job_applications` (
                                    `id` INT(11) NOT NULL AUTO_INCREMENT,
                                    `job_id` INT(11) NOT NULL,
                                    `applicant_id` INT(11) NOT NULL,
                                    `applied_at` DATETIME DEFAULT CURRENT_TIMESTAMP(),
                                    `status` ENUM('pending','reviewed','accepted','rejected') DEFAULT 'pending',
                                    `cover_letter` TEXT DEFAULT NULL,
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `unique_application` (`job_id`,`applicant_id`),
                                    KEY `idx_job_applicant` (`applicant_id`),
                                    CONSTRAINT `job_applications_ibfk_1`
                                        FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE CASCADE,
                                    CONSTRAINT `job_applications_ibfk_2`
                                        FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Table: messages
DROP TABLE IF EXISTS `messages`;
CREATE TABLE `messages` (
                            `id` INT(11) NOT NULL AUTO_INCREMENT,
                            `sender_id` INT(11) NOT NULL,
                            `receiver_id` INT(11) DEFAULT NULL,
                            `group_id` INT(11) DEFAULT NULL,
                            `message_text` TEXT NOT NULL,
                            `timestamp` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
                            PRIMARY KEY (`id`),
                            KEY `idx_messages_sender` (`sender_id`),
                            KEY `idx_messages_receiver` (`receiver_id`),
                            KEY `idx_messages_group` (`group_id`),
                            CONSTRAINT `fk_messages_sender`
                                FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                            CONSTRAINT `fk_messages_receiver`
                                FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
