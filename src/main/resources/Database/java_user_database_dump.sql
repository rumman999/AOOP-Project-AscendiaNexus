-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1:4306
-- Generation Time: Oct 16, 2025 at 03:11 PM
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
-- Table structure for table `followers`
--

CREATE TABLE `followers` (
  `follower_id` int(11) NOT NULL,
  `following_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
(1, 4),
(1, 6),
(2, 1),
(2, 4),
(2, 8),
(2, 9),
(3, 2),
(3, 5),
(3, 6),
(3, 10),
(4, 1),
(4, 2),
(4, 4),
(4, 8),
(4, 9),
(5, 1),
(5, 5),
(5, 11),
(6, 2),
(6, 6),
(6, 10),
(7, 4),
(7, 8),
(7, 9),
(7, 11),
(8, 1),
(8, 2),
(8, 5),
(8, 6),
(8, 10);

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

--
-- Dumping data for table `jobs`
--

INSERT INTO `jobs` (`id`, `poster_id`, `title`, `description`, `location`, `salary_range`, `posted_at`, `tech_stack`, `job_type`, `requirements`, `company`, `posted_by`) VALUES
(1, 2, 'Senior Full Stack Developer', 'We are seeking an experienced Full Stack Developer to join our growing team. You will be responsible for developing scalable web applications and leading technical initiatives.', 'Dhaka, Bangladesh', '$80,000 - $120,000', '2025-10-01 09:00:00', 'React, Node.js, PostgreSQL, AWS, Docker', 'Full-time', '5+ years experience in full stack development, Strong knowledge of React and Node.js, Experience with cloud platforms, Excellent problem-solving skills', 'Google', 'Muntasir Tofa'),
(2, 2, 'Frontend Developer', 'Join our frontend team to build beautiful and responsive user interfaces. You will work closely with designers and backend developers to create seamless user experiences.', 'Remote', '$60,000 - $90,000', '2025-10-03 10:30:00', 'React, TypeScript, Tailwind CSS, Next.js', 'Full-time', '3+ years of frontend development experience, Expertise in React and TypeScript, Strong CSS skills, Portfolio of previous work', 'Amazon', 'Muntasir Tofa'),
(3, 5, 'Machine Learning Engineer', 'Looking for an ML Engineer to develop and deploy machine learning models for our AI-powered products. You will work on cutting-edge projects involving NLP and computer vision.', 'Dhaka, Bangladesh', '$90,000 - $140,000', '2025-10-05 14:00:00', 'Python, TensorFlow, PyTorch, Kubernetes, MLOps', 'Full-time', 'Masters degree in CS or related field, 4+ years ML experience, Strong programming skills in Python, Experience with MLOps tools', 'Meta', 'Dibbo Kumar'),
(4, 8, 'DevOps Engineer', 'We need a DevOps engineer to manage our cloud infrastructure and improve our CI/CD pipelines. You will be responsible for maintaining system reliability and performance.', 'Hybrid - Dhaka', '$70,000 - $110,000', '2025-10-07 11:15:00', 'AWS, Kubernetes, Terraform, Jenkins, Python', 'Full-time', '3+ years DevOps experience, Strong knowledge of AWS and Kubernetes, Experience with Infrastructure as Code, Good scripting skills', 'Microsoft', 'Evence Rozario'),
(5, 2, 'UI/UX Designer', 'Creative UI/UX Designer wanted to design intuitive and engaging user experiences. You will collaborate with product managers and developers to bring ideas to life.', 'Remote', '$55,000 - $85,000', '2025-10-08 09:45:00', 'Figma, Adobe XD, Sketch, Prototyping Tools', 'Contract', '3+ years UI/UX design experience, Strong portfolio showcasing your work, Proficiency in Figma and Adobe XD, Understanding of user-centered design principles', 'Spotify', 'Muntasir Tofa'),
(6, 5, 'Data Analyst', 'Seeking a Data Analyst to help drive data-informed decisions across the organization. You will analyze large datasets and create insightful reports and dashboards.', 'Dhaka, Bangladesh', '$50,000 - $75,000', '2025-10-10 13:20:00', 'SQL, Python, Tableau, Power BI, Excel', 'Full-time', '2+ years data analysis experience, Strong SQL skills, Experience with data visualization tools, Excellent communication skills', 'Netflix', 'Dibbo Kumar'),
(7, 8, 'Mobile App Developer', 'Join our mobile team to build native iOS and Android applications. You will work on features that impact millions of users worldwide.', 'Remote', '$75,000 - $115,000', '2025-10-12 10:00:00', 'React Native, Swift, Kotlin, Firebase', 'Full-time', '4+ years mobile development experience, Published apps on App Store and Play Store, Strong understanding of mobile UI/UX, Experience with RESTful APIs', 'Uber', 'Evence Rozario'),
(8, 2, 'Product Manager', 'Looking for a Product Manager to lead our core product initiatives. You will define product strategy, prioritize features, and work with cross-functional teams.', 'Dhaka, Bangladesh', '$85,000 - $130,000', '2025-10-14 15:30:00', 'Product Management Tools, Analytics, User Research', 'Full-time', '5+ years product management experience, Strong analytical and communication skills, Experience with Agile methodologies, Technical background preferred', 'Airbnb', 'Muntasir Tofa');

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

--
-- Dumping data for table `job_applications`
--

INSERT INTO `job_applications` (`id`, `job_id`, `applicant_id`, `applied_at`, `status`, `cover_letter`, `cv_path`) VALUES
(1, 1, 1, '2025-10-01 10:30:00', 'reviewed', 'I am excited to apply for the Senior Full Stack Developer position. With over 6 years of experience in building scalable web applications, I believe I would be a great fit for your team.', 'cv_uploads\\torongo_rozario_cv.pdf'),
(2, 1, 4, '2025-10-01 14:20:00', 'accepted', 'I have extensive experience with React and Node.js, and I am passionate about creating efficient and maintainable code.', 'cv_uploads\\adnan_cv.pdf'),
(3, 2, 6, '2025-10-03 11:45:00', 'pending', 'As a frontend specialist with 4 years of experience, I would love to contribute to your team and create amazing user experiences.', 'cv_uploads\\maruf_hossain_cv.pdf'),
(4, 3, 9, '2025-10-05 15:30:00', 'reviewed', 'My background in machine learning and NLP makes me an ideal candidate for this position. I have published research papers in top conferences.', 'cv_uploads\\rozario_cv.pdf'),
(5, 4, 11, '2025-10-07 12:00:00', 'pending', 'I have 4 years of DevOps experience managing large-scale cloud infrastructures and would be excited to join your team.', 'cv_uploads\\evencer_cv.pdf'),
(6, 5, 10, '2025-10-08 10:15:00', 'accepted', 'I am a creative designer with a passion for creating intuitive user interfaces. My portfolio showcases my work with various startups.', 'cv_uploads\\tango_man_cv.pdf'),
(7, 6, 1, '2025-10-10 14:00:00', 'pending', 'With strong analytical skills and experience in data visualization, I can help drive data-informed decisions at your organization.', 'cv_uploads\\torongo_rozario_cv.pdf'),
(8, 7, 4, '2025-10-12 11:30:00', 'reviewed', 'I have published multiple successful mobile apps with thousands of downloads and would love to contribute to your mobile platform.', 'cv_uploads\\adnan_cv.pdf'),
(9, 8, 6, '2025-10-14 16:00:00', 'pending', 'As a product manager with 6 years of experience, I have successfully launched multiple products from conception to market.', 'cv_uploads\\maruf_hossain_cv.pdf');

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
(1, 1, 2, NULL, 'Hey Muntasir! How are you doing?', '2025-10-01 09:00:00'),
(2, 2, 1, NULL, 'Hi Torongo! I am doing great, thanks for asking. How about you?', '2025-10-01 09:02:00'),
(3, 1, 2, NULL, 'Pretty good! Did you see the new job posting I put up?', '2025-10-01 09:05:00'),
(4, 2, 1, NULL, 'Yes! The Senior Full Stack position looks great. We already have some good applications coming in.', '2025-10-01 09:07:00'),
(5, 1, 2, NULL, 'That is awesome! Are you free for a quick call later today?', '2025-10-01 09:10:00'),
(6, 2, 1, NULL, 'Sure, how about 3 PM? We can discuss the candidate pipeline.', '2025-10-01 09:12:00'),
(7, 1, 2, NULL, 'Perfect! See you then.', '2025-10-01 09:15:00'),
(8, 4, 1, NULL, 'Hey Torongo, I just submitted my application for the Full Stack position!', '2025-10-01 14:30:00'),
(9, 1, 4, NULL, 'That is great Adnan! I will make sure to review it personally.', '2025-10-01 14:35:00'),
(10, 4, 1, NULL, 'Thanks! I am really excited about this opportunity.', '2025-10-01 14:40:00'),
(11, 1, 4, NULL, 'Your experience looks solid. Let me discuss with the team and we will get back to you soon.', '2025-10-01 14:45:00'),
(12, 1, NULL, 1, 'Good morning everyone! Hope you all had a great weekend.', '2025-10-02 08:00:00'),
(13, 2, NULL, 1, 'Morning Torongo! It was wonderful, thanks for asking.', '2025-10-02 08:05:00'),
(14, 4, NULL, 1, 'Hey team! Ready to tackle this week.', '2025-10-02 08:10:00'),
(15, 6, NULL, 1, 'Good morning! Looking forward to our team meeting later.', '2025-10-02 08:15:00'),
(16, 1, NULL, 1, 'Great energy! Let us make this week productive.', '2025-10-02 08:20:00'),
(17, 2, 5, NULL, 'Hi Dibbo, can you help me review the ML Engineer applications?', '2025-10-05 10:00:00'),
(18, 5, 2, NULL, 'Of course! Send them over and I will take a look this afternoon.', '2025-10-05 10:05:00'),
(19, 2, 5, NULL, 'Thanks! I have forwarded the top 5 candidates to your email.', '2025-10-05 10:10:00'),
(20, 5, 2, NULL, 'Got them. These look promising!', '2025-10-05 10:15:00'),
(21, 1, NULL, 2, 'Team, we need to finalize the project roadmap by end of this week.', '2025-10-03 09:00:00'),
(22, 4, NULL, 2, 'I can have the technical specifications ready by Wednesday.', '2025-10-03 09:10:00'),
(23, 8, NULL, 2, 'I will complete the infrastructure setup by Thursday.', '2025-10-03 09:15:00'),
(24, 9, NULL, 2, 'Great! I will align the timelines with our deliverables.', '2025-10-03 09:20:00'),
(25, 1, NULL, 2, 'Perfect! Let us have a sync meeting tomorrow at 2 PM.', '2025-10-03 09:25:00'),
(26, 1, 6, NULL, 'Hi Maruf, I saw your profile. Are you interested in the Data Analyst position?', '2025-10-10 11:00:00'),
(27, 6, 1, NULL, 'Hi Torongo! Yes, I am very interested. The role aligns perfectly with my skills.', '2025-10-10 11:05:00'),
(28, 1, 6, NULL, 'Excellent! Would you be available for a quick interview next week?', '2025-10-10 11:10:00'),
(29, 6, 1, NULL, 'Absolutely! I am free any day after 2 PM.', '2025-10-10 11:15:00'),
(30, 1, NULL, 4, 'Has anyone tried the new React 19 features yet?', '2025-10-06 14:00:00'),
(31, 4, NULL, 4, 'Yes! The new compiler is amazing. Significantly improved performance in my projects.', '2025-10-06 14:05:00'),
(32, 8, NULL, 4, 'I am still on React 18, but planning to upgrade soon. Any migration tips?', '2025-10-06 14:10:00'),
(33, 2, NULL, 4, 'The migration guide on the official docs is pretty comprehensive. Should be straightforward.', '2025-10-06 14:15:00'),
(34, 9, NULL, 4, 'I completed the migration last week. Happy to share my experience if needed.', '2025-10-06 14:20:00'),
(35, 8, 9, NULL, 'Hey Rozario, how is your ML project going?', '2025-10-08 16:00:00'),
(36, 9, 8, NULL, 'Going well! Just finished training the model. Accuracy is at 94%.', '2025-10-08 16:05:00'),
(37, 8, 9, NULL, 'That is impressive! What dataset are you using?', '2025-10-08 16:10:00'),
(38, 9, 8, NULL, 'A custom dataset we collected. About 100K samples with detailed annotations.', '2025-10-08 16:15:00'),
(39, 2, NULL, 3, 'I am hosting a webinar on career growth next week. Anyone interested?', '2025-10-09 10:00:00'),
(40, 5, NULL, 3, 'Count me in! What topics will you be covering?', '2025-10-09 10:05:00'),
(41, 6, NULL, 3, 'Sounds interesting! Will it be recorded?', '2025-10-09 10:10:00'),
(42, 10, NULL, 3, 'I would love to attend! Please share the registration link.', '2025-10-09 10:15:00'),
(43, 2, NULL, 3, 'Will cover resume building, interview tips, and salary negotiation. Yes, it will be recorded!', '2025-10-09 10:20:00'),
(44, 1, 10, NULL, 'Congrats on landing the UI/UX position at Spotify!', '2025-10-11 09:00:00'),
(45, 10, 1, NULL, 'Thank you so much Torongo! Your mentorship really helped.', '2025-10-11 09:05:00'),
(46, 1, 10, NULL, 'You earned it! Keep up the great work.', '2025-10-11 09:10:00'),
(47, 4, NULL, 7, 'Just published a new article on transformer models. Check it out!', '2025-10-12 13:00:00'),
(48, 8, NULL, 7, 'Great read! The explanation of attention mechanisms was particularly helpful.', '2025-10-12 13:10:00'),
(49, 9, NULL, 7, 'Shared it on LinkedIn. This is quality content!', '2025-10-12 13:15:00'),
(50, 11, NULL, 7, 'Thanks for sharing! Always learning something new from this group.', '2025-10-12 13:20:00'),
(51, 2, 1, NULL, 'Are we still on for the meeting tomorrow?', '2025-10-14 18:00:00'),
(52, 1, 2, NULL, 'Yes! 10 AM at the office. See you there.', '2025-10-14 18:05:00'),
(53, 4, 1, NULL, 'Thanks for the feedback on my application! Looking forward to next steps.', '2025-10-14 19:00:00'),
(54, 1, 4, NULL, 'You are welcome! We will be in touch soon.', '2025-10-14 19:05:00'),
(55, 1, NULL, 8, 'What tools do you all use for time tracking while working remotely?', '2025-10-13 11:00:00'),
(56, 2, NULL, 8, 'I use Toggl. Simple and effective.', '2025-10-13 11:05:00'),
(57, 5, NULL, 8, 'Clockify works great for me. It is free and has good reporting.', '2025-10-13 11:10:00'),
(58, 6, NULL, 8, 'I prefer RescueTime for automatic tracking.', '2025-10-13 11:15:00'),
(59, 10, NULL, 8, 'Thanks for the suggestions! Will try Toggl first.', '2025-10-13 11:20:00');

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
(5, 'https://www.youtube.com/playlist?list=PL4cUxeGkcC9goXbgTDQ0n_4TBzOO0ocPR', 'Node.js Complete Guide', 8, '2025-10-09 11:20:00');

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
(1, 1, 'Excited to announce that we are hiring! Check out our latest job postings for Full Stack Developer and Machine Learning Engineer positions. Great opportunities await! #Hiring #TechJobs', NULL, '2025-10-01 09:00:00', 15, 5),
(2, 2, 'Just wrapped up an amazing interview with a talented candidate. The future of our team is looking bright! #Recruitment #TeamGrowth', NULL, '2025-10-03 14:30:00', 12, 3),
(3, 4, 'Sharing my latest project - a real-time chat application built with React and Node.js. Learned so much during this journey! #WebDev #React', 'F:\\Windows User Files\\Downloads\\unnamed (1).png', '2025-10-05 10:15:00', 23, 8),
(4, 1, 'Grateful for another productive week! Our team achieved all our sprint goals and even exceeded expectations. #TeamWork #Success', NULL, '2025-10-07 18:00:00', 18, 4),
(5, 6, 'Attended an incredible tech conference today. So many insights on the future of AI and machine learning. Mind blown! #TechConference #AI', NULL, '2025-10-09 16:45:00', 20, 6),
(6, 8, 'Deployed our new microservices architecture to production today. Zero downtime migration! #DevOps #CloudComputing', NULL, '2025-10-11 12:30:00', 16, 4),
(7, 10, 'Celebrating 2 years in the tech industry! Thank you to everyone who has been part of this amazing journey. #Anniversary #Gratitude', NULL, '2025-10-13 09:00:00', 25, 7),
(8, 1, 'abc', NULL, '2025-10-16 16:39:13', 0, 0);

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
(1, 1, 2, 'Great opportunities! I will share this with my network.', '2025-10-01 09:30:00'),
(2, 1, 4, 'Just applied for the Full Stack position! Fingers crossed.', '2025-10-01 10:00:00'),
(3, 1, 6, 'The ML Engineer role looks perfect for my friend. Sending it their way!', '2025-10-01 11:15:00'),
(4, 1, 9, 'Love seeing companies invest in their teams. Best of luck with the hiring!', '2025-10-01 12:30:00'),
(5, 1, 10, 'These positions align perfectly with current industry needs. Well done!', '2025-10-01 14:00:00'),
(6, 2, 1, 'That is fantastic! Can not wait to welcome new team members.', '2025-10-03 15:00:00'),
(7, 2, 4, 'Exciting times ahead for the team!', '2025-10-03 16:30:00'),
(8, 2, 8, 'Growth is always good. Looking forward to meeting them.', '2025-10-03 17:45:00'),
(9, 3, 1, 'Impressive work Adnan! The UI looks clean and modern.', '2025-10-05 11:00:00'),
(10, 3, 2, 'This is really well done! Would love to see a demo.', '2025-10-05 12:30:00'),
(11, 3, 6, 'The real-time features look smooth. Great job!', '2025-10-05 14:00:00'),
(12, 3, 8, 'Nice architecture! Are you planning to open source it?', '2025-10-05 15:30:00'),
(13, 3, 9, 'Wow, this is production-ready quality!', '2025-10-05 16:45:00'),
(14, 3, 10, 'Love the attention to detail. Fantastic work!', '2025-10-05 18:00:00'),
(15, 3, 11, 'This could be a great addition to your portfolio.', '2025-10-05 19:30:00'),
(16, 3, 5, 'Really clean code structure from what I can see!', '2025-10-05 20:00:00'),
(17, 4, 2, 'Well deserved! The team has been crushing it.', '2025-10-07 18:30:00'),
(18, 4, 4, 'Proud to be part of this team!', '2025-10-07 19:00:00'),
(19, 4, 8, 'Great teamwork makes the dream work!', '2025-10-07 20:15:00'),
(20, 4, 9, 'On to the next sprint! Let us keep this momentum.', '2025-10-07 21:00:00'),
(21, 5, 1, 'Which conference was it? Would love to attend next year.', '2025-10-09 17:00:00'),
(22, 5, 2, 'The AI session sounds fascinating!', '2025-10-09 18:30:00'),
(23, 5, 4, 'Did they discuss any new frameworks or tools?', '2025-10-09 19:15:00'),
(24, 5, 8, 'Would love to hear your key takeaways!', '2025-10-09 20:00:00'),
(25, 5, 9, 'Conferences like these are always inspiring.', '2025-10-09 21:30:00'),
(26, 5, 11, 'Planning to attend that one next year for sure!', '2025-10-09 22:00:00'),
(27, 6, 1, 'Zero downtime is the dream! Well executed.', '2025-10-11 13:00:00'),
(28, 6, 2, 'Impressive DevOps work! What was the biggest challenge?', '2025-10-11 14:30:00'),
(29, 6, 4, 'Microservices architecture for the win!', '2025-10-11 15:45:00'),
(30, 6, 9, 'This is the kind of technical excellence we strive for!', '2025-10-11 17:00:00'),
(31, 7, 1, 'Congratulations on 2 years! Here is to many more!', '2025-10-13 09:30:00'),
(32, 7, 2, 'Time flies when you are doing what you love!', '2025-10-13 10:00:00'),
(33, 7, 4, 'Cheers to your continued success!', '2025-10-13 11:15:00'),
(34, 7, 6, 'Two years down, many more to go! Congrats!', '2025-10-13 12:30:00'),
(35, 7, 8, 'Your growth has been remarkable to witness!', '2025-10-13 14:00:00'),
(36, 7, 9, 'Proud to have you in our community!', '2025-10-13 15:30:00'),
(37, 7, 11, 'Happy work anniversary! Keep shining!', '2025-10-13 16:45:00');

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
(1, 1, 2, '2025-10-01 09:15:00'),
(2, 1, 4, '2025-10-01 09:45:00'),
(3, 1, 5, '2025-10-01 10:30:00'),
(4, 1, 6, '2025-10-01 11:00:00'),
(5, 1, 8, '2025-10-01 12:15:00'),
(6, 1, 9, '2025-10-01 13:00:00'),
(7, 1, 10, '2025-10-01 14:30:00'),
(8, 1, 11, '2025-10-01 15:45:00'),
(9, 2, 1, '2025-10-03 14:45:00'),
(10, 2, 4, '2025-10-03 15:30:00'),
(11, 2, 5, '2025-10-03 16:00:00'),
(12, 2, 6, '2025-10-03 17:15:00'),
(13, 2, 8, '2025-10-03 18:00:00'),
(14, 2, 9, '2025-10-03 19:30:00'),
(15, 3, 1, '2025-10-05 10:30:00'),
(16, 3, 2, '2025-10-05 11:15:00'),
(17, 3, 5, '2025-10-05 12:00:00'),
(18, 3, 6, '2025-10-05 13:30:00'),
(19, 3, 8, '2025-10-05 14:45:00'),
(20, 3, 9, '2025-10-05 16:00:00'),
(21, 3, 10, '2025-10-05 17:30:00'),
(22, 3, 11, '2025-10-05 19:00:00'),
(23, 4, 2, '2025-10-07 18:15:00'),
(24, 4, 4, '2025-10-07 18:45:00'),
(25, 4, 5, '2025-10-07 19:30:00'),
(26, 4, 6, '2025-10-07 20:00:00'),
(27, 4, 8, '2025-10-07 20:45:00'),
(28, 4, 9, '2025-10-07 21:30:00'),
(29, 5, 1, '2025-10-09 17:00:00'),
(30, 5, 2, '2025-10-09 17:45:00'),
(31, 5, 4, '2025-10-09 18:30:00'),
(32, 5, 8, '2025-10-09 19:15:00'),
(33, 5, 9, '2025-10-09 20:00:00'),
(34, 5, 11, '2025-10-09 21:00:00'),
(35, 6, 1, '2025-10-11 12:45:00'),
(36, 6, 2, '2025-10-11 13:30:00'),
(37, 6, 4, '2025-10-11 14:15:00'),
(38, 6, 9, '2025-10-11 16:00:00'),
(39, 7, 1, '2025-10-13 09:15:00'),
(40, 7, 2, '2025-10-13 09:45:00'),
(41, 7, 4, '2025-10-13 10:30:00'),
(42, 7, 5, '2025-10-13 11:00:00'),
(43, 7, 6, '2025-10-13 12:00:00'),
(44, 7, 8, '2025-10-13 13:30:00'),
(45, 7, 9, '2025-10-13 15:00:00'),
(46, 7, 11, '2025-10-13 16:30:00');

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
('Torongo Evence Rozario', 'imtorongo@gmail.com', '#23Torongo', '01312025776', 'Admin', 1, 0, '2003-12-23', '56/B East Rajabazar, Tejgaon Dhaka 1215', '[{\"id\":1,\"task\":\"Review job applications\",\"completed\":false,\"priority\":\"high\"},{\"id\":2,\"task\":\"Prepare presentation for team meeting\",\"completed\":true,\"priority\":\"medium\"},{\"id\":3,\"task\":\"Update project documentation\",\"completed\":false,\"priority\":\"low\"}]', 'file:/D:/IMG_0422.png', 'b07b4204-2c4e-4412-a605-648a05f1f256', 150, 21),
('Muntasir Tofa', 'tofa121@gmail.com', 'T@1234r', '01312523555', 'Recruiter', 2, 0, '2000-05-15', 'Gulshan 2, Dhaka 1212', '[{\"id\":1,\"task\":\"Schedule interviews for next week\",\"completed\":true,\"priority\":\"high\"},{\"id\":2,\"task\":\"Post new job openings\",\"completed\":true,\"priority\":\"high\"},{\"id\":3,\"task\":\"Follow up with candidates\",\"completed\":false,\"priority\":\"medium\"}]', 'file:/F:/Windows%20User%20Files/Downloads/unnamed.png', '56bb2b1d-c5a4-4ccf-8ab6-9ff755833aad', 120, 25),
('Adnan Shahriar', 'adnans@yahoo.com', 'B23c23@', '01334567899', 'Jobseeker', 4, 0, '2001-08-10', 'Banani, Dhaka 1213', '[{\"id\":1,\"task\":\"Update resume\",\"completed\":true,\"priority\":\"high\"},{\"id\":2,\"task\":\"Apply for Full Stack position\",\"completed\":true,\"priority\":\"high\"},{\"id\":3,\"task\":\"Prepare for technical interview\",\"completed\":false,\"priority\":\"high\"}]', 'file:/F:/Windows%20User%20Files/Downloads/unnamed%20(1).png', 'c8af9a0a-8177-4a37-a8f2-f2d018aff33d', 80, 24),
('Dibbo Kumar', 'dk@yahoo.com', 'DK123@k', '01734567896', 'Recruiter', 5, 0, '1998-03-20', 'Dhanmondi 15, Dhaka 1209', '[{\"id\":1,\"task\":\"Review ML Engineer candidates\",\"completed\":true,\"priority\":\"high\"},{\"id\":2,\"task\":\"Coordinate with hiring managers\",\"completed\":false,\"priority\":\"medium\"}]', 'file:/D:/1-removebg-preview.png', '2238e8e5-5ee8-43d6-aec7-76f919af8bd4', 110, 27),
('Maruf Hossain', 'maruf@gmail.com', 'Ma#21ac', '01324567897', 'Jobseeker', 6, 0, '2002-11-05', 'Uttara Sector 7, Dhaka 1230', '[{\"id\":1,\"task\":\"Learn advanced SQL\",\"completed\":false,\"priority\":\"medium\"},{\"id\":2,\"task\":\"Build data visualization portfolio\",\"completed\":false,\"priority\":\"high\"},{\"id\":3,\"task\":\"Apply for Data Analyst role\",\"completed\":true,\"priority\":\"high\"}]', 'file:/D:/1-removebg-preview.png', '5f6bb25b-2264-4ba7-9ab6-9211f48c01f3', 65, 23),
('Evence Rozario', 'evence@gmail.com', '@23torongo', '01618528315', 'Recruiter', 8, 0, '1997-07-12', 'Mirpur 10, Dhaka 1216', '[{\"id\":1,\"task\":\"Post DevOps opening\",\"completed\":true,\"priority\":\"high\"},{\"id\":2,\"task\":\"Screen resumes\",\"completed\":true,\"priority\":\"medium\"}]', 'file:/D:/profile_pics/evence.png', 'd6844e44-28ae-4d51-b832-c25d94214020', 95, 28),
('Rozario Khan', 'rozario121@gmail.com', '12#$ToO', '01720025776', 'Jobseeker', 9, 0, '2000-09-25', 'Mohammadpur, Dhaka 1207', '[{\"id\":1,\"task\":\"Complete ML course\",\"completed\":false,\"priority\":\"high\"},{\"id\":2,\"task\":\"Work on personal project\",\"completed\":false,\"priority\":\"medium\"}]', 'file:/D:/profile_pics/rozario.png', 'aede313c-ac7f-4582-8c0b-9a9e080516c9', 70, 25),
('Tango Ahmed', 'tango101@yahoo.com', '@23Toro', '01378945612', 'Jobseeker', 10, 0, '2001-02-14', 'Bashundhara R/A, Dhaka 1229', '[{\"id\":1,\"task\":\"Improve design portfolio\",\"completed\":true,\"priority\":\"high\"},{\"id\":2,\"task\":\"Apply for UI/UX positions\",\"completed\":true,\"priority\":\"high\"}]', 'file:/D:/profile_pics/tango.png', 'a0f022c4-9546-4d83-be96-f4d78508c759', 85, 24),
('Evencer Rahman', 'evencer@gmail.com', '#23Torongo', '01312025777', 'Jobseeker', 11, 0, '1999-12-30', 'Shantinagar, Dhaka 1217', '[{\"id\":1,\"task\":\"Study Kubernetes\",\"completed\":false,\"priority\":\"high\"},{\"id\":2,\"task\":\"Get AWS certification\",\"completed\":false,\"priority\":\"medium\"}]', 'file:/D:/profile_pics/evencer.png', '98418003-7fdd-46e8-8aff-7fb85d50f9c5', 60, 26);

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
  ADD KEY `fk_receiver` (`receiver_id`);

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
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `job_applications`
--
ALTER TABLE `job_applications`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=60;

--
-- AUTO_INCREMENT for table `playlists`
--
ALTER TABLE `playlists`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `post_comments`
--
ALTER TABLE `post_comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT for table `post_likes`
--
ALTER TABLE `post_likes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

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
