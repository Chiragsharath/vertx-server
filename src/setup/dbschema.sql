CREATE DATABASE IF NOT EXISTS plianced;

USE plianced;

DROP TABLE IF EXISTS user_profile;
CREATE TABLE user_profile (
  id INTEGER NOT NULL AUTO_INCREMENT,
  uuid varchar(64) NOT NULL,
  active char(1) NOT NULL DEFAULT 'A',
  loginType char(1) NOT NULL DEFAULT 'E', -- E - Email, G - Google, F - Facebook, T - Twitter
  userType char(1) NOT NULL DEFAULT 'E', -- E for Expert, U for User
  email varchar(255) NOT NULL,
  phone char(15) NOT NULL,
  profile TEXT NOT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  UNIQUE KEY unique_email (email),
  UNIQUE KEY unique_uuid (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS expert_register;
CREATE TABLE expert_register (
  id INTEGER NOT NULL AUTO_INCREMENT,
  status char(1) NOT NULL DEFAULT 'P', -- P for Pending, A for Approved, R for Rejected
  requestType char(1) NOT NULL DEFAULT 'S', -- S for Self, I for Invited
  email varchar(255) NOT NULL,
  phone char(15) NOT NULL,
  name varchar(128) NULL,
  industry varchar(64) NULL,
  subIndustry varchar(64) NULL,
  roleFunction varchar(64) NULL,
  expertise varchar(255) NULL,
  requestedOn timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actedOn timestamp NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY key_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS expert_referral;
CREATE TABLE expert_referral (
  id INTEGER NOT NULL AUTO_INCREMENT,
  status char(1) NOT NULL DEFAULT 'P', -- R for Referred, C for Converted
  rname varchar(255) NOT NULL, -- Referrer's name
  remail varchar(255) NOT NULL, -- Referrer's email
  email varchar(255) NOT NULL,
  phone char(15) NOT NULL,
  name varchar(128) NULL,
  title varchar(64) NULL,
  company varchar(128) NULL,
  referredOn timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  actedOn timestamp NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY key_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS expert_profile;
CREATE TABLE expert_profile (
   id INTEGER NOT NULL AUTO_INCREMENT,
   uuid varchar(64) NOT NULL,
   email varchar(255) NOT NULL,
   expYears INTEGER NOT NULL DEFAULT 0,
   availability char(1) NOT NULL DEFAULT 'F', -- F Full time
   hourlyRate INTEGER NOT NULL DEFAULT 0,
   summary TEXT NULL,
   basicInfo TEXT NULL,
   experience TEXT NULL,
   expertise TEXT NULL,
   education TEXT NULL,
   touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   UNIQUE KEY unique_email (email),
   UNIQUE KEY unique_uuid (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS bank_account;
CREATE TABLE bank_account (
   id INTEGER NOT NULL AUTO_INCREMENT,
   uuid varchar(64) NOT NULL,
   status char(1) NOT NULL default 'A',
   payeeName varchar(64) NOT NULL,
   accountNo varchar(64) NOT NULL,
   accountType varchar(16) NOT NULL,
   routingNumber varchar(32) NOT NULL,
   payeeAddress TEXT NOT NULL,
   bankName varchar(64) NOT NULL,
   bankAddress TEXT NOT NULL,
   touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   KEY uuid_key (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS code_master;
CREATE TABLE code_master(
  id INTEGER NOT NULL AUTO_INCREMENT,
  lookupType char(10) NOT NULL DEFAULT 'NA',
  levelType char(10) NOT NULL DEFAULT 'A',
  additionalType varchar(16) NULL,
  code char(10) NOT NULL,
  title varchar(64) NOT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY type_key (lookupType),
  KEY code_key (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Code Master';

DROP TABLE IF EXISTS topic_master;
CREATE TABLE topic_master(
  id INTEGER NOT NULL AUTO_INCREMENT,
  topicName varchar(64) NOT NULL,
  industry varchar(64) NULL,
  subIndustry varchar(64) NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Topic Master';

DROP TABLE IF EXISTS venue_master;
CREATE TABLE venue_master(
  id INTEGER NOT NULL AUTO_INCREMENT,
  name varchar(128) NOT NULL,
  address TEXT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Venue Master';

DROP TABLE IF EXISTS fee_master;
CREATE TABLE fee_master(
  id INTEGER NOT NULL AUTO_INCREMENT,
  code char(10) NOT NULL,
  eventLevel char(10) NOT NULL,
  industry char(10) NOT NULL,
  subIndustry char(10) NOT NULL,
  duration char(10) NOT NULL,
  feeUnit char(10) NOT NULL,
  description varchar(32) NOT NULL,
  feeAmount DOUBLE NOT NULL,
  discountAmount DOUBLE NOT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY industry_key (industry)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Fee Master';

DROP TABLE IF EXISTS industry_master;
CREATE TABLE industry_master(
  id INTEGER NOT NULL AUTO_INCREMENT,
  industry varchar(64) NOT NULL DEFAULT 'NA',
  subIndustry varchar(64) NULL,
  keyFunction varchar(64) NULL,
  subFunction varchar(64) NULL,
  skills TEXT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY industry_key (industry)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Industry Master';

DROP TABLE IF EXISTS event_log;
CREATE TABLE event_log (
  id INTEGER NOT NULL AUTO_INCREMENT,
  uuid varchar(64) NOT NULL,
  eventType char(10) NOT NULL,
  eventData TEXT NOT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY key_uuid (uuid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS event_errors;
CREATE TABLE event_errors (
  id INTEGER NOT NULL AUTO_INCREMENT,
  uuid varchar(64) NOT NULL,
  eventType varchar(64) NOT NULL,
  eventData TEXT NOT NULL,
  reason varchar(255) NOT NULL,
  touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS event_master;
CREATE TABLE event_master (
   id INTEGER NOT NULL AUTO_INCREMENT,
   userId varchar(64) NOT NULL,
   companyId varchar(64) NOT NULL,
   contentType char(10) NOT NULL,
   topicName varchar(64) NOT NULL,
   topicDetails TEXT  NULL,
   status char(10) NOT NULL,
   tags varchar(255) NOT NULL,
   industry varchar(64) NOT NULL,
   subIndustry varchar(64) NOT NULL,
   data TEXT NULL,
   schedule TEXT NULL,
   modified TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   feeStructure char(10) NOT NULL,
   eventLevel char(10),
   duration char(10),
   durationType char(10),
   touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS file_master;
CREATE TABLE file_master (
   id INTEGER NOT NULL AUTO_INCREMENT,
   active char(1) NOT NULL,
   userId varchar(64) NOT NULL,
   eventId INTEGER NOT NULL,
   name varchar(64) NOT NULL,
   contentCode char(10) NOT NULL,
   contentType char(24) NOT NULL,
   fbUrl TEXT NULL,
   s3Url TEXT NULL,
   fbJson TEXT NULL,
   touchtime timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (id),
   KEY uuid_key (userId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `business_bank_account`;
CREATE TABLE IF NOT EXISTS `business_bank_account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgCode` varchar(64) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  `payeeName` varchar(64) NOT NULL,
  `accountNo` varchar(64) NOT NULL,
  `accountType` varchar(16) NOT NULL,
  `routingNumber` varchar(32) NOT NULL,
  `payeeAddress` text NOT NULL,
  `bankName` varchar(64) NOT NULL,
  `bankAddress` text NOT NULL,
  `touchtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `uuid_key` (`orgCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- Data exporting was unselected.


-- Dumping structure for table plianced.business_register
DROP TABLE IF EXISTS `business_register`;
CREATE TABLE IF NOT EXISTS `business_register` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgCode` varchar(50) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'P',
  `businessName` varchar(128) NOT NULL,
  `industry` varchar(64) DEFAULT NULL,
  `subIndustry` varchar(64) DEFAULT NULL,
  `businessSize` int(11) DEFAULT NULL,
  `businessRegion` varchar(255) DEFAULT NULL,
  `businessAdmin` text NOT NULL,
  `requestedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `actedOn` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index 3` (`orgCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS `organization_master` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `orgCode` varchar(50) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'P',
  `businessName` varchar(128) NOT NULL,
  `industry` varchar(64) DEFAULT NULL,
  `subIndustry` varchar(64) DEFAULT NULL,
  `businessSize` int(11) DEFAULT NULL,
  `businessRegion` varchar(255) DEFAULT NULL,
  `planId` varchar(64) NOT NULL,
  `planStartDate` datetime NOT NULL,
  `planEndDate`    datetime NOT NULL,
  `favicon` varchar(500) DEFAULT NULL,
  `requestedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index 3` (`orgCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;


CREATE TABLE IF NOT EXISTS `team_master` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `teamId` varchar(50) DEFAULT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  `teamName` varchar(128) NOT NULL,
  `orgCode` varchar(64) NOT NULL,
  `adminId` varchar(64) DEFAULT NULL,
  `requestedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `Index 3` (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS `team_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` varchar(50) NOT NULL,
  `status` char(1) NOT NULL DEFAULT 'A',
  `teamCode` varchar(64) NOT NULL,
  `Role` varchar(64) NOT NULL DEFAULT 'U',
  `requestedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;

CREATE TABLE IF NOT EXISTS `organization_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uuid` varchar(50) NOT NULL,
  `fname` varchar(50) NOT NULL,
  `lname` varchar(50) NOT NULL,
  `active` char(1) NOT NULL DEFAULT 'A',
  `orgCode` varchar(64) NOT NULL,
  `email` varchar(64) NOT NULL ,
  `phone` varchar(15) NOT NULL,
  `requestedOn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;