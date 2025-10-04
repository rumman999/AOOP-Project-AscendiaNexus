-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: Oct 04, 2025 at 05:07 PM
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
(1, 4),
(1, 6),
(2, 1),
(2, 4),
(2, 8),
(3, 1),
(3, 2),
(3, 8),
(3, 9),
(4, 2),
(4, 4),
(4, 6),
(4, 9),
(4, 11),
(5, 1),
(5, 2),
(5, 6),
(5, 10),
(6, 1),
(6, 4),
(6, 5),
(6, 9),
(6, 11),
(7, 1),
(7, 8),
(8, 1),
(8, 2),
(8, 4),
(8, 6),
(9, 2),
(9, 5),
(10, 2),
(10, 5),
(10, 6),
(11, 1),
(11, 5),
(11, 6);

-- --------------------------------------------------------

--
-- Table structure for table `job`
--

CREATE TABLE `job` (
  `id` int(11) NOT NULL,
  `poster_id` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text NOT NULL,
  `tech_stack` text DEFAULT NULL,
  `job_type` varchar(100) DEFAULT NULL,
  `requirements` text DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `salary_range` varchar(50) DEFAULT NULL,
  `posted_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `job`
--

INSERT INTO `job` (`id`, `poster_id`, `title`, `description`, `tech_stack`, `job_type`, `requirements`, `location`, `salary_range`, `posted_at`) VALUES
(1, 1, 'Job 1', 'A job very very job', NULL, NULL, NULL, 'Gulshan', '700,000', '2025-10-02 18:00:33'),
(3, 1, 'dsa', 'asd', 'adsa', 'ad', 'asd', 'adsasd', '25', '2025-10-04 10:56:41'),
(4, 1, 'ds', 'sad', 'sad', 'adsasd', 'ad', 'dsa', 'sad', '2025-10-04 10:57:12'),
(5, 5, 'recruiter job', 'good', 'javagood', 'Full time', 'be cool', 'Dhaka', '50k', '2025-10-04 11:00:46');

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
  `cover_letter` text DEFAULT NULL
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
(1, 1, 2, NULL, 'Hello', '2025-09-22 15:55:35'),
(2, 2, 1, NULL, 'Hi', '2025-09-22 15:55:50'),
(3, 2, 1, NULL, 'hey', '2025-09-22 15:58:21'),
(4, 1, 2, NULL, 'hydsajflkj', '2025-09-22 15:58:42'),
(5, 1, NULL, NULL, 'h3llo', '2025-09-22 16:21:07'),
(6, 1, NULL, NULL, 'ho2', '2025-09-22 16:21:13'),
(7, 1, NULL, NULL, 'hi', '2025-09-22 16:21:58'),
(8, 1, NULL, NULL, 'hello', '2025-09-22 16:24:16'),
(9, 4, NULL, NULL, 'how are you', '2025-09-22 16:24:29'),
(10, 1, NULL, NULL, 'helll', '2025-09-22 16:25:30'),
(11, 4, 2, NULL, 'hey man how are you', '2025-09-22 16:25:56'),
(12, 2, 4, NULL, 'hi', '2025-09-22 16:27:11'),
(13, 4, 2, NULL, 'asdkfjhaskldfhlaskjdhfkljadsk', '2025-09-22 16:27:51'),
(14, 2, 4, NULL, 'dsfa', '2025-09-22 16:28:04'),
(15, 2, 4, NULL, 'sdfs', '2025-09-22 16:28:12'),
(16, 1, 2, NULL, 'hello', '2025-09-26 15:12:59'),
(17, 2, 1, NULL, 'hello', '2025-09-26 15:13:15'),
(18, 1, NULL, NULL, 'hello', '2025-09-26 15:30:42'),
(19, 2, 1, NULL, 'hi', '2025-09-26 15:32:16'),
(20, 1, 2, NULL, 'asdfasfasdfas', '2025-09-26 15:33:13'),
(21, 1, 2, NULL, 'sdfasfadsfas', '2025-09-26 15:33:19'),
(22, 1, 2, NULL, 'sadfasdfasdfas', '2025-09-26 15:33:26'),
(23, 2, 1, NULL, 'hi', '2025-09-26 15:34:04'),
(24, 2, 1, NULL, 'hello', '2025-09-26 15:34:51'),
(25, 1, 2, NULL, 'hello', '2025-09-26 15:35:14'),
(26, 1, 2, NULL, 'hello', '2025-09-26 15:35:26'),
(27, 1, 2, NULL, 'hello', '2025-09-26 15:35:40'),
(28, 2, 1, NULL, 'jlksjfkjaj', '2025-09-26 15:36:01'),
(29, 1, NULL, NULL, 'hello server', '2025-09-26 15:38:23'),
(30, 2, 1, NULL, 'helo', '2025-09-26 15:39:21'),
(31, 2, 1, NULL, 'hello', '2025-09-26 15:39:33'),
(32, 1, 2, NULL, 'hi', '2025-09-26 15:40:07'),
(33, 1, 2, NULL, 'hi', '2025-09-26 15:44:12'),
(34, 2, 1, NULL, 'abc', '2025-09-26 15:45:02'),
(35, 1, 2, NULL, 'hilo', '2025-09-26 15:58:27'),
(36, 2, 1, NULL, 'dfasd', '2025-09-26 15:58:44'),
(37, 1, 2, NULL, 'dsf', '2025-09-26 15:58:55'),
(38, 2, 1, NULL, 'dfsdf', '2025-09-26 15:59:05'),
(39, 2, NULL, NULL, 'hey', '2025-09-26 16:07:05'),
(40, 1, NULL, NULL, 'how are you all doing', '2025-09-26 16:07:22'),
(41, 1, 2, NULL, 'abc', '2025-09-26 16:07:46'),
(42, 2, 1, NULL, 'abc', '2025-09-26 16:08:05'),
(43, 2, 1, NULL, 'hello', '2025-09-27 04:18:10'),
(44, 2, 1, NULL, 'adsf', '2025-09-27 04:27:19'),
(45, 1, NULL, NULL, 'hello', '2025-09-28 15:39:17'),
(46, 1, 2, NULL, 'hellodjfslkj', '2025-09-28 15:39:47'),
(47, 2, 1, NULL, 'dsjfhkjsd', '2025-09-28 15:40:04'),
(48, 2, NULL, NULL, 'hellodf;asj', '2025-09-28 15:47:03'),
(49, 1, NULL, NULL, 'hskldhfakskfashkjldshakjlhaskdhkladshkljsadfhkj', '2025-09-28 15:49:10'),
(50, 2, 1, NULL, 'hellodsafasdfa', '2025-09-28 15:49:41'),
(51, 1, NULL, NULL, 'hello', '2025-09-28 16:22:41'),
(52, 1, NULL, NULL, 'hello', '2025-09-28 16:24:41'),
(53, 1, NULL, NULL, 'hello', '2025-09-29 15:19:31'),
(54, 1, NULL, NULL, 'I am torongo', '2025-09-29 15:19:44'),
(55, 2, 1, NULL, 'abc 123', '2025-09-29 15:22:12'),
(56, 1, 2, NULL, 'hi', '2025-09-29 15:23:32'),
(57, 2, 1, NULL, 'yellow', '2025-09-29 15:23:42'),
(58, 1, NULL, NULL, 'hello', '2025-09-29 15:59:22'),
(59, 1, NULL, NULL, 'hello', '2025-09-29 16:01:16'),
(60, 4, 1, NULL, 'hey man how are you doing', '2025-09-29 16:02:13'),
(61, 1, 4, NULL, 'i\'m fine how are you doing', '2025-09-29 16:02:45'),
(62, 4, 1, NULL, 'abcd', '2025-09-29 16:03:32'),
(63, 1, 4, NULL, 'acda', '2025-09-29 16:03:51'),
(64, 4, 2, NULL, 'peew', '2025-09-29 16:04:38'),
(65, 2, 4, NULL, 'few', '2025-09-29 16:05:15'),
(66, 4, 2, NULL, 'fewfew', '2025-09-29 16:05:59'),
(67, 2, 1, NULL, 'hello', '2025-09-30 07:13:06'),
(68, 1, 2, NULL, 'mellow', '2025-09-30 07:13:27'),
(69, 1, 2, NULL, 'bcd', '2025-09-30 07:14:16'),
(70, 2, 1, NULL, 'acd', '2025-09-30 07:14:32'),
(71, 1, 2, NULL, 'jkl', '2025-09-30 07:15:10'),
(72, 2, 1, NULL, 'hellods', '2025-09-30 07:18:43'),
(73, 2, 1, NULL, 'mellows', '2025-09-30 07:21:52'),
(74, 1, 2, NULL, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '2025-09-30 07:22:27'),
(75, 2, NULL, NULL, 'hewllo', '2025-09-30 07:28:14'),
(76, 1, 2, NULL, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaassssssssssssssssssssssssssddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd', '2025-09-30 07:29:43'),
(77, 1, 2, NULL, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '2025-09-30 07:32:39'),
(78, 1, 2, NULL, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaassssssssssssssssssddddddddddddddddddddddddddddddddddd', '2025-09-30 07:40:49'),
(79, 1, 2, NULL, 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', '2025-09-30 07:41:13'),
(80, 1, NULL, NULL, 'hello', '2025-09-30 10:06:20'),
(81, 1, NULL, NULL, 'hello', '2025-09-30 10:07:38'),
(82, 2, 1, NULL, 'abc 1324', '2025-09-30 10:12:58'),
(83, 1, 2, NULL, 'adfasdfsa', '2025-09-30 10:13:24'),
(84, 1, NULL, NULL, 'hello', '2025-09-30 10:43:17'),
(85, 1, NULL, NULL, 'hello', '2025-09-30 10:44:34'),
(86, 1, NULL, NULL, 'hello', '2025-09-30 10:47:56'),
(87, 1, NULL, NULL, 'hello', '2025-09-30 10:53:08'),
(88, 1, NULL, NULL, 'hello', '2025-09-30 10:54:51'),
(89, 1, NULL, NULL, 'hello', '2025-09-30 11:01:37'),
(90, 1, NULL, NULL, 'hello', '2025-09-30 12:04:25'),
(91, 1, NULL, 1, 'Abcd', '2025-09-30 12:12:31'),
(92, 2, NULL, 1, 'efgh', '2025-09-30 12:13:49'),
(93, 1, NULL, 2, 'hello', '2025-09-30 12:16:07'),
(94, 1, NULL, 2, '12345454', '2025-09-30 12:22:30'),
(95, 1, NULL, 2, 'asdfasfasfas', '2025-09-30 12:23:18'),
(96, 1, NULL, 2, 'dsfasfasdfa', '2025-09-30 12:26:58'),
(97, 1, NULL, NULL, 'hello', '2025-09-30 12:30:01'),
(98, 2, NULL, NULL, 'hello', '2025-09-30 12:30:31'),
(99, 1, NULL, 2, 'jdsklajlkj', '2025-09-30 12:30:47'),
(100, 2, NULL, 1, 'adfasf', '2025-09-30 12:31:01'),
(101, 1, NULL, 1, 'asdfasdfdas', '2025-09-30 12:31:09'),
(102, 2, NULL, 1, 'Hello tofa', '2025-09-30 12:31:27'),
(103, 1, 2, NULL, 'hello man how are you doing', '2025-09-30 12:34:15'),
(104, 2, 1, NULL, 'i\'m fine how are you doing', '2025-09-30 12:34:33'),
(105, 2, NULL, NULL, 'yellow', '2025-09-30 12:36:40'),
(106, 1, NULL, NULL, 'GROUP|2|Torongo Evence Rozario|adfasdfa', '2025-09-30 12:56:56'),
(107, 2, NULL, NULL, 'USER|1|Muntasir Tofa|adfasf', '2025-09-30 12:57:42'),
(108, 2, NULL, NULL, 'USER|1|Muntasir Tofa|sdfas', '2025-09-30 12:57:56'),
(109, 1, NULL, NULL, 'ALL|0|Torongo Evence Rozario|adsfas', '2025-09-30 12:59:35'),
(110, 1, NULL, NULL, 'GROUP|2|Torongo Evence Rozario|sdfas', '2025-09-30 12:59:43'),
(111, 1, NULL, NULL, 'USER|2|Torongo Evence Rozario|adfas', '2025-09-30 12:59:50'),
(112, 2, NULL, NULL, 'USER|1|Muntasir Tofa|sadfas', '2025-09-30 13:00:26'),
(113, 1, NULL, NULL, 'USER|2|Torongo Evence Rozario|fgdgd', '2025-09-30 13:00:41'),
(114, 2, NULL, NULL, 'USER|1|Muntasir Tofa|aaaa', '2025-09-30 13:00:51'),
(115, 2, NULL, NULL, 'hello', '2025-09-30 13:02:07'),
(116, 1, NULL, NULL, 'hello', '2025-09-30 13:02:17'),
(117, 2, NULL, 1, 'hey nnam', '2025-09-30 13:02:39'),
(118, 1, NULL, NULL, 'hey', '2025-09-30 13:14:07'),
(119, 1, NULL, NULL, 'heykj', '2025-09-30 13:14:13'),
(120, 1, NULL, 2, 'sdfa', '2025-09-30 13:14:38'),
(121, 1, NULL, 2, 'adfasdfasdfasdfadsfasdfadsfdasfsdkfsldjakfjdsklaj', '2025-09-30 13:14:52'),
(122, 2, NULL, NULL, 'yo yo yo', '2025-09-30 13:17:29'),
(123, 1, NULL, 2, 'dsfa', '2025-09-30 13:17:36'),
(124, 2, 1, NULL, 'dfasdf', '2025-09-30 13:17:54'),
(125, 2, NULL, 1, 'asdfas', '2025-09-30 13:18:02'),
(126, 2, NULL, NULL, 'hello', '2025-09-30 13:27:26'),
(127, 2, NULL, NULL, 'hello', '2025-09-30 13:28:50'),
(128, 1, NULL, 2, 'dfsa', '2025-09-30 13:28:59'),
(129, 2, NULL, NULL, 'hey]', '2025-09-30 13:31:36'),
(130, 1, NULL, NULL, 'djsfl', '2025-09-30 13:31:45'),
(131, 2, 1, NULL, 'fdsfd', '2025-09-30 13:31:55'),
(132, 1, 2, NULL, 'dfsd', '2025-09-30 13:32:06'),
(133, 2, 1, NULL, 'afaksjlfkjaskljflkj', '2025-09-30 13:32:19'),
(134, 2, NULL, NULL, 'hello', '2025-09-30 13:36:23'),
(135, 1, NULL, 2, 'dsafdsa', '2025-09-30 13:36:47'),
(136, 2, 1, NULL, 'sdfs', '2025-09-30 13:36:58'),
(137, 1, 2, NULL, 'fgdsgds', '2025-09-30 13:37:08'),
(138, 1, NULL, NULL, 'hello', '2025-09-30 13:42:09'),
(139, 1, 2, NULL, 'dsfa', '2025-09-30 13:42:20'),
(140, 2, 1, NULL, 'sadfasdfasdfas', '2025-09-30 13:43:10'),
(141, 2, NULL, NULL, 'hello', '2025-09-30 13:52:22'),
(142, 1, 2, NULL, 'afasd', '2025-09-30 13:52:34'),
(143, 2, 1, NULL, 'afasdddddddddddddddddddddddddddddddddddddddddddddddd', '2025-09-30 13:52:52'),
(144, 1, 2, NULL, 'sdafasdfasf', '2025-09-30 13:53:03'),
(145, 1, NULL, NULL, 'hey', '2025-09-30 13:57:50'),
(146, 2, NULL, NULL, 'hello', '2025-09-30 13:57:57'),
(147, 1, NULL, 2, 'sdfsadf', '2025-09-30 13:58:04'),
(148, 2, 1, NULL, 'dsfasfasfasfasfadsklfjalskdjfklas', '2025-09-30 13:58:25'),
(149, 1, 2, NULL, 'sdfasfasdfsa', '2025-09-30 13:58:34'),
(150, 2, 1, NULL, 'how are you torongo', '2025-09-30 13:58:48'),
(151, 1, 2, NULL, 'im fine', '2025-09-30 13:58:55'),
(152, 2, NULL, 1, 'sadfadsfasdflkasjlkdfadsjf;ladsjf;l', '2025-09-30 13:59:08'),
(153, 2, NULL, NULL, 'hello', '2025-09-30 14:33:46'),
(154, 2, NULL, NULL, 'hello', '2025-09-30 15:24:26'),
(155, 1, NULL, NULL, 'how are you', '2025-09-30 15:24:36'),
(156, 2, 1, NULL, 'jsdlkfjkl', '2025-09-30 15:25:04'),
(157, 1, NULL, NULL, 'hello', '2025-09-30 15:37:14'),
(158, 1, NULL, NULL, 'hello', '2025-09-30 15:39:13'),
(159, 1, NULL, NULL, 'hello', '2025-09-30 15:40:45'),
(160, 1, NULL, NULL, 'hello', '2025-09-30 15:43:14'),
(161, 2, NULL, NULL, 'dfsefs', '2025-09-30 15:46:46'),
(162, 1, NULL, NULL, 'hello', '2025-09-30 15:52:12'),
(163, 2, NULL, NULL, 'hey man', '2025-09-30 15:55:49'),
(164, 1, NULL, NULL, 'hey', '2025-09-30 15:57:40'),
(165, 1, NULL, NULL, 'hey', '2025-09-30 16:01:22'),
(166, 1, NULL, NULL, 'hello', '2025-09-30 16:02:37'),
(167, 2, NULL, 4, 'adsfasf4', '2025-10-01 11:40:32');

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
  `Coin` int(11) DEFAULT NULL,
  `Age` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

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

--
-- Indexes for dumped tables
--

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
-- Indexes for table `job`
--
ALTER TABLE `job`
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
  ADD KEY `fk_receiver` (`receiver_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `job`
--
ALTER TABLE `job`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `job_applications`
--
ALTER TABLE `job_applications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=168;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `group_members`
--
ALTER TABLE `group_members`
  ADD CONSTRAINT `group_members_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `group_members_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `job`
--
ALTER TABLE `job`
  ADD CONSTRAINT `fk_poster` FOREIGN KEY (`poster_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `job_applications`
--
ALTER TABLE `job_applications`
  ADD CONSTRAINT `job_applications_ibfk_1` FOREIGN KEY (`job_id`) REFERENCES `job` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `job_applications_ibfk_2` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `fk_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `user` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `fk_sender` FOREIGN KEY (`sender_id`) REFERENCES `user` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
