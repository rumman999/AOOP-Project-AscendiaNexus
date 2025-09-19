-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: Sep 19, 2025 at 04:20 PM
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
('Torongo Evence Rozario', 'imtorongo@gmail.com', '#23Torongo', '01312025776', 'Admin', 1, 0, '2003-12-23', '56/B East Rajabazar, Tejgaon Dhaka 1215', '[{\"task\":\"Play Games\",\"done\":false},{\"task\":\"Do Electronics Question papers\",\"done\":false},{\"task\":\"Learn python\",\"done\":true},{\"task\":\"I want to learn Cooking\",\"done\":false}]', 'file:/D:/IMG_0422.png', 'b07b4204-2c4e-4412-a605-648a05f1f256', 0, 21),
('Muntasir Tofa', 'tofa121@gmail.com', 'T@1234r', '01312523555', 'Recruiter', 2, 0, NULL, '', '', 'file:/D:/shield-alt-2.png', '56bb2b1d-c5a4-4ccf-8ab6-9ff755833aad', 0, 0),
('Adnan', 'adnans@yahoo.com', 'B23c23@', '01334567899', 'Jobseeker', 4, 0, NULL, '', '', '', 'c8af9a0a-8177-4a37-a8f2-f2d018aff33d', 0, 0),
('Dibbo Kumar', 'dk@yahoo.com', 'DK123@k', '01734567896', 'Recruiter', 5, 0, NULL, '', '', 'file:/D:/1-removebg-preview.png', '2238e8e5-5ee8-43d6-aec7-76f919af8bd4', 0, 0),
('Maruf Hossain', 'abc@gmail.com', 'Ma#21', '01324567897', 'Jobseeker', 6, 0, NULL, '', '', '', '5f6bb25b-2264-4ba7-9ab6-9211f48c01f3', 0, 0),
('Evence Rozario', 'torongoxd@gmail.com', '23torongo', '01618528315', 'Recruiter', 8, 0, NULL, '', '', '', 'd6844e44-28ae-4d51-b832-c25d94214020', 0, 0),
('Rozario', 'torongo121@gmail.com', '12#$ToO', '01720025776', 'Jobseeker', 9, 0, NULL, '', '', '', 'aede313c-ac7f-4582-8c0b-9a9e080516c9', 0, 0),
('Tango Man', 'tango101@yahoo.com', '@23Toro', '01378945612', 'Jobseeker', 10, 0, NULL, '', '', '', 'a0f022c4-9546-4d83-be96-f4d78508c759', 0, 0),
('EvenceR', 'imtorono@gmail.com', '#23Torongo', '01312025777', 'Jobseeker', 11, 0, NULL, '', '', '', '98418003-7fdd-46e8-8aff-7fb85d50f9c5', 0, 0),
('Alvinur', 'alvi121@gmail.com', '123@123Aa', '01378945611', 'Admin', 12, 0, NULL, '', '', '', '', 0, 0),
('Rumman Karim', 'rumman@gmail.com', '!12Rumman', '01534567899', 'Admin', 13, 0, NULL, NULL, NULL, NULL, '52b1d351-459f-4b12-a24f-b2117f5b2246', NULL, NULL),
('abc', '123', 'abc', '000', 'Admin', 14, 0, NULL, NULL, NULL, NULL, '123', NULL, NULL),
('Alvinur', 'alvi121@gmail.com', '123@123Aa', '01378945611', 'Admin', 15, 0, NULL, '', '', '', '12d5453b-d1a0-451e-bf75-30e8eb047b0b', 0, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
