-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: 2025-11-07 at 06:00 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `java_user_database`
--
CREATE DATABASE IF NOT EXISTS `java_user_database` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `java_user_database`;

-- --------------------------------------------------------

--
-- Table structure for table `followers`
--

CREATE TABLE `followers` (
                             `follower_id` int(11) NOT NULL,
                             `following_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `followers`
--

INSERT INTO `followers` (`follower_id`, `following_id`) VALUES
                                                            (1, 2),
                                                            (1, 3),
                                                            (1, 5),
                                                            (2, 1),
                                                            (2, 3),
                                                            (3, 1),
                                                            (3, 2),
                                                            (3, 4),
                                                            (4, 3),
                                                            (4, 5),
                                                            (5, 1),
                                                            (5, 2);

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
                          `id` int(11) NOT NULL,
                          `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`id`, `name`) VALUES
                                        (1, 'Tech Innovators'),
                                        (2, 'Project Alpha Team'),
                                        (3, 'Career Development Hub'),
                                        (4, 'Software Engineers United'),
                                        (5, 'Startup Founders Network'),
                                        (6, 'Design & UX Collective'),
                                        (7, 'Data Science Enthusiasts'),
                                        (8, 'Remote Workers Guild');

-- --------------------------------------------------------

--
-- Table structure for table `group_members`
--

CREATE TABLE `group_members` (
                                 `group_id` int(11) NOT NULL,
                                 `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `group_members`
--

INSERT INTO `group_members` (`group_id`, `user_id`) VALUES
                                                        (1, 1),
                                                        (1, 2),
                                                        (1, 3),
                                                        (2, 1),
                                                        (2, 4),
                                                        (3, 2),
                                                        (3, 3),
                                                        (3, 4),
                                                        (3, 5),
                                                        (4, 1),
                                                        (4, 3),
                                                        (4, 4),
                                                        (5, 1),
                                                        (5, 5),
                                                        (6, 2),
                                                        (7, 4),
                                                        (8, 1),
                                                        (8, 2),
                                                        (8, 5);

-- --------------------------------------------------------

--
-- Table structure for table `jobs`
--

CREATE TABLE `jobs` (
                        `id` int(11) NOT NULL,
                        `poster_id` int(11) NOT NULL,
                        `title` varchar(150) NOT NULL,
                        `description` text NOT NULL,
                        `location` varchar(100) DEFAULT NULL,
                        `salary_range` varchar(50) DEFAULT NULL,
                        `posted_at` datetime DEFAULT current_timestamp(),
                        `tech_stack` text DEFAULT NULL,
                        `job_type` varchar(100) DEFAULT NULL,
                        `requirements` text DEFAULT NULL,
                        `company` varchar(255) DEFAULT NULL,
                        `posted_by` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `job_applications`
--

CREATE TABLE `job_applications` (
                                    `id` int(11) NOT NULL,
                                    `job_id` int(11) NOT NULL,
                                    `applicant_id` int(11) NOT NULL,
                                    `applied_at` datetime DEFAULT current_timestamp(),
                                    `status` enum('pending','reviewed','accepted','rejected') DEFAULT 'pending',
                                    `cover_letter` text DEFAULT NULL,
                                    `cv_path` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
                            `id` int(11) NOT NULL,
                            `sender_id` int(11) NOT NULL,
                            `receiver_id` int(11) DEFAULT NULL,
                            `group_id` int(11) DEFAULT NULL,
                            `message_text` text NOT NULL,
                            `timestamp` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`id`, `sender_id`, `receiver_id`, `group_id`, `message_text`, `timestamp`) VALUES
                                                                                                       (1, 1, 2, NULL, 'Hi Beth, welcome to the platform!', '2025-11-01 09:00:00'),
                                                                                                       (2, 2, 1, NULL, 'Thanks, Alex! It looks great. Glad to be here.', '2025-11-01 09:02:00'),
                                                                                                       (3, 3, 4, NULL, 'Hey Dana, did you see Beth\'s post about new jobs coming up?', '2025-11-02 10:30:00'),
                                                                                                       (4, 4, 3, NULL, 'I did! Eager to see what they post.', '2025-11-02 10:35:00'),
                                                                                                       (5, 1, NULL, 1, 'Hello @Tech Innovators! Welcome to the group.', '2025-11-03 11:00:00'),
                                                                                                       (6, 3, NULL, 1, 'Hi Alex! Thanks for the add.', '2025-11-03 11:05:00'),
                                                                                                       (7, 5, 2, NULL, 'Great job on that last recruitment drive, Beth.', '2025-11-04 14:20:00'),
                                                                                                       (8, 2, 5, NULL, 'Thanks Evan! We found some great candidates.', '2025-11-04 14:22:00');

-- --------------------------------------------------------

--
-- Table structure for table `playlists`
--

CREATE TABLE `playlists` (
                             `id` int(11) NOT NULL,
                             `url` varchar(255) NOT NULL,
                             `name` varchar(255) NOT NULL,
                             `created_by` int(11) DEFAULT NULL,
                             `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `playlists`
--

INSERT INTO `playlists` (`id`, `url`, `name`, `created_by`, `created_at`) VALUES
                                                                              (1, 'https://www.youtube.com/watch?v=bWKbdPAovFA&list=PLoW9ZoLJX39Xcdaa4Dn5WLREHblolbji4', 'Web Development Fundamentals', 1, '2025-10-01 10:00:00'),
                                                                              (2, 'https://www.youtube.com/playlist?list=PLALM3TzRCwCsyNvIPvw1OQvpDKZjw4knp', 'Machine Learning Essentials', 1, '2025-10-03 14:30:00'),
                                                                              (3, 'https://www.youtube.com/playlist?list=PLillGF-RfqbZ2ybcoD2OaabW2P7Ws8CWu', 'React Tutorial Series', 4, '2025-10-05 09:15:00'),
                                                                              (4, 'https://www.youtube.com/playlist?list=PLZlA0Gpn_vH_uZs4vJMIhcinABSTUH2bY', 'JavaScript Algorithms', 2, '2025-10-07 16:45:00'),
                                                                              (5, 'https://www.youtube.com/playlist?list=PL4cUxeGkcC9goXbgTDQ0n_4TBzOO0ocPR', 'Node.js Complete Guide', 3, '2025-10-09 11:20:00');

-- --------------------------------------------------------

--
-- Table structure for table `posts`
--

CREATE TABLE `posts` (
                         `id` int(11) NOT NULL,
                         `user_id` int(11) NOT NULL,
                         `caption` text DEFAULT NULL,
                         `image_path` varchar(500) DEFAULT NULL,
                         `created_at` datetime DEFAULT current_timestamp(),
                         `likes_count` int(11) DEFAULT 0,
                         `comments_count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `posts`
--

INSERT INTO `posts` (`id`, `user_id`, `caption`, `image_path`, `created_at`, `likes_count`, `comments_count`) VALUES
                                                                                                                  (1, 1, 'Welcome to AscendiaNexus! As an admin, I\'m here to help. Feel free to reach out with any questions. #Welcome #Community', NULL, '2025-11-01 10:00:00', 4, 1),
                                                                                                                  (2, 2, 'Our company is scaling up! We\'ll be posting several new roles in engineering and design soon. Keep an eye on the job board! #Hiring #Recruiting #TechJobs', NULL, '2025-11-02 11:30:00', 3, 1),
                                                                                                                  (3, 3, 'Just finished a new personal project using JavaFX and Spring Boot. It\'s amazing to see what these frameworks can do together. #JavaFX #SpringBoot #Developer #Portfolio', NULL, '2025-11-03 15:00:00', 2, 2),
                                                                                                                  (4, 4, 'I\'m currently looking for tips on how to optimize my resume for data science roles. What key metrics do recruiters look for? Any advice is welcome! #DataScience #Jobseeker #ResumeTips', NULL, '2025-11-04 09:00:00', 2, 2),
                                                                                                                  (5, 1, 'A quick reminder to all members: please keep all community discussions professional and respectful. Let\'s build a supportive network! #CommunityGuidelines', NULL, '2025-11-05 14:00:00', 1, 0),
                                                                                                                  (6, 5, 'Excited to be on this platform. Looking to connect with other recruiters and talented job seekers. #Networking', NULL, '2025-11-06 10:00:00', 1, 0);

-- --------------------------------------------------------

--
-- Table structure for table `post_comments`
--

CREATE TABLE `post_comments` (
                                 `id` int(11) NOT NULL,
                                 `post_id` int(11) NOT NULL,
                                 `user_id` int(11) NOT NULL,
                                 `comment_text` text NOT NULL,
                                 `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `post_comments`
--

INSERT INTO `post_comments` (`id`, `post_id`, `user_id`, `comment_text`, `created_at`) VALUES
                                                                                           (1, 1, 2, 'Glad to be here!', '2025-11-01 10:05:00'),
                                                                                           (2, 2, 3, 'Great! I\'ll be sure to check it out.', '2025-11-02 11:45:00'),
                                                                                           (3, 3, 1, 'Nice work, Charlie. Would love to see a demo or the source code if you\'re sharing!', '2025-11-03 16:00:00'),
                                                                                           (4, 3, 3, '@Alex Chen Thanks! I\'ll clean up the repo and share it soon.', '2025-11-03 16:05:00'),
                                                                                           (5, 4, 5, 'Great question, Dana. Focus on quantifiable achievements! \'Increased model accuracy by 15%\' is much stronger than just \'Improved models\'.', '2025-11-04 09:30:00'),
                                                                                           (6, 4, 4, '@Evan Wright That\'s fantastic advice, thank you!', '2025-11-04 09:35:00');

-- --------------------------------------------------------

--
-- Table structure for table `post_likes`
--

CREATE TABLE `post_likes` (
                              `id` int(11) NOT NULL,
                              `post_id` int(11) NOT NULL,
                              `user_id` int(11) NOT NULL,
                              `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `post_likes`
--

INSERT INTO `post_likes` (`id`, `post_id`, `user_id`, `created_at`) VALUES
                                                                        (1, 1, 2, '2025-11-01 10:05:00'),
                                                                        (2, 1, 3, '2025-11-01 10:10:00'),
                                                                        (3, 1, 4, '2025-11-01 10:15:00'),
                                                                        (4, 1, 5, '2025-11-01 10:20:00'),
                                                                        (5, 2, 1, '2025-11-02 11:35:00'),
                                                                        (6, 2, 3, '2025-11-02 11:40:00'),
                                                                        (7, 2, 5, '2025-11-02 11:42:00'),
                                                                        (8, 3, 1, '2025-11-03 16:00:00'),
                                                                        (9, 3, 4, '2025-11-03 16:02:00'),
                                                                        (10, 4, 3, '2025-11-04 09:05:00'),
                                                                        (11, 4, 2, '2025-11-04 09:07:00'),
                                                                        (12, 5, 2, '2025-11-05 14:05:00'),
                                                                        (13, 6, 1, '2025-11-06 10:10:00');

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
                        `full_name` varchar(127) NOT NULL,
                        `email` varchar(50) NOT NULL,
                        `password` varchar(50) NOT NULL,
                        `phone_number` varchar(11) NOT NULL,
                        `account_type` varchar(50) NOT NULL,
                        `id` int(11) NOT NULL,
                        `login` tinyint(1) NOT NULL DEFAULT 0,
                        `DOB` date DEFAULT NULL,
                        `Address` varchar(255) DEFAULT NULL,
                        `todo_list` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL,
                        `profile_pic` varchar(255) DEFAULT NULL,
                        `UUID` varchar(36) NOT NULL,
                        `Coin` int(11) DEFAULT 0,
                        `Age` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`full_name`, `email`, `password`, `phone_number`, `account_type`, `id`, `login`, `DOB`, `Address`, `todo_list`, `profile_pic`, `UUID`, `Coin`, `Age`) VALUES
                                                                                                                                                                              ('Alex Chen', 'alex@email.com', 'Alex@123', '01700000001', 'Admin', 1, 0, '1995-05-10', 'House 1, Road 2, Banani, Dhaka', '[{\"id\":1,\"task\":\"Review community guidelines\",\"completed\":false,\"priority\":\"high\"},{\"id\":2,\"task\":\"Onboard new recruiters\",\"completed\":true,\"priority\":\"medium\"}]', 'com/example/aoop_project/Images/profile.jpg', '111a111a-1111-1111-1111-111a111a111a', 200, 30),
                                                                                                                                                                              ('Bethany Smith', 'beth@email.com', 'Beth@123', '01800000002', 'Recruiter', 2, 0, '1992-08-20', 'House 3, Road 4, Gulshan, Dhaka', '[{\"id\":1,\"task\":\"Post new developer role\",\"completed\":false,\"priority\":\"high\"}]', 'com/example/aoop_project/Images/profile.jpg', '222b222b-2222-2222-2222-222b222b222b', 150, 33),
                                                                                                                                                                              ('Charlie Day', 'charlie@email.com', 'Charlie@123', '01900000003', 'Jobseeker', 3, 0, '2000-01-15', 'House 5, Road 6, Dhanmondi, Dhaka', '[{\"id\":1,\"task\":\"Update JavaFX project portfolio\",\"completed\":true,\"priority\":\"high\"},{\"id\":2,\"task\":\"Apply for 3 new jobs\",\"completed\":false,\"priority\":\"medium\"}]', 'com/example/aoop_project/Images/profile.jpg', '333c333c-3333-3333-3333-333c333c333c', 100, 25),
                                                                                                                                                                              ('Dana Scully', 'dana@email.com', 'Dana@123', '01600000004', 'Jobseeker', 4, 0, '1998-11-30', 'House 7, Road 8, Mohakhali, Dhaka', '[{\"id\":1,\"task\":\"Finish Data Science course\",\"completed\":false,\"priority\":\"high\"},{\"id\":2,\"task\":\"Practice SQL queries\",\"completed\":false,\"priority\":\"medium\"}]', 'com/example/aoop_project/Images/profile.jpg', '444d444d-4444-4444-4444-444d444d444d', 50, 26),
                                                                                                                                                                              ('Evan Wright', 'evan@email.com', 'Evan@123', '01500000005', 'Recruiter', 5, 0, '1994-03-25', 'House 9, Road 10, Uttara, Dhaka', '[]', 'com/example/aoop_project/Images/profile.jpg', '555e555e-5555-5555-5555-555e555e555e', 120, 31);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `followers`
--
ALTER TABLE `followers`
    ADD PRIMARY KEY (`follower_id`,`following_id`),
    ADD KEY `following_id` (`following_id`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
    ADD PRIMARY KEY (`id`);

--
-- Indexes for table `group_members`
--
ALTER TABLE `group_members`
    ADD PRIMARY KEY (`group_id`,`user_id`),
    ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `jobs`
--
ALTER TABLE `jobs`
    ADD PRIMARY KEY (`id`),
    ADD KEY `idx_poster` (`poster_id`);

--
-- Indexes for table `job_applications`
--
ALTER TABLE `job_applications`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `unique_application` (`job_id`,`applicant_id`),
    ADD KEY `applicant_id` (`applicant_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
    ADD PRIMARY KEY (`id`),
    ADD KEY `fk_sender` (`sender_id`),
    ADD KEY `fk_receiver` (`receiver_id`),
    ADD KEY `fk_group` (`group_id`);

--
-- Indexes for table `playlists`
--
ALTER TABLE `playlists`
    ADD PRIMARY KEY (`id`),
    ADD KEY `created_by` (`created_by`);

--
-- Indexes for table `posts`
--
ALTER TABLE `posts`
    ADD PRIMARY KEY (`id`),
    ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `post_comments`
--
ALTER TABLE `post_comments`
    ADD PRIMARY KEY (`id`),
    ADD KEY `post_id` (`post_id`),
    ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `post_likes`
--
ALTER TABLE `post_likes`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `unique_like` (`post_id`,`user_id`),
    ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
    ADD PRIMARY KEY (`id`),
    ADD UNIQUE KEY `email` (`email`),
    ADD UNIQUE KEY `UUID` (`UUID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `jobs`
--
ALTER TABLE `jobs`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `job_applications`
--
ALTER TABLE `job_applications`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `playlists`
--
ALTER TABLE `playlists`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `post_comments`
--
ALTER TABLE `post_comments`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `post_likes`
--
ALTER TABLE `post_likes`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
    MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `followers`
--
ALTER TABLE `followers`
    ADD CONSTRAINT `followers_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `followers_ibfk_2` FOREIGN KEY (`following_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `group_members`
--
ALTER TABLE `group_members`
    ADD CONSTRAINT `group_members_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `group_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `jobs`
--
ALTER TABLE `jobs`
    ADD CONSTRAINT `fk_poster` FOREIGN KEY (`poster_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `job_applications`
--
ALTER TABLE `job_applications`
    ADD CONSTRAINT `job_applications_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `job_applications_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
    ADD CONSTRAINT `fk_group` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `fk_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
    ADD CONSTRAINT `fk_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `playlists`
--
ALTER TABLE `playlists`
    ADD CONSTRAINT `playlists_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `posts`
--
ALTER TABLE `posts`
    ADD CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `post_comments`
--
ALTER TABLE `post_comments`
    ADD CONSTRAINT `post_comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `post_comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `post_likes`
--
ALTER TABLE `post_likes`
    ADD CONSTRAINT `post_likes_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
    ADD CONSTRAINT `post_likes_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;