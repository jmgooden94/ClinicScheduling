-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema clinic
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema clinic
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `clinic` DEFAULT CHARACTER SET latin1 ;
USE `clinic` ;

-- -----------------------------------------------------
-- Table `clinic`.`Address`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`Address` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `street` VARCHAR(100) NOT NULL,
  `city` VARCHAR(100) NOT NULL,
  `state` VARCHAR(100) NOT NULL,
  `zip` VARCHAR(5) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `clinic`.`Patient`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`Patient` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `phone_number` VARCHAR(30) NOT NULL,
  `address_fk` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `address_fk_idx` (`address_fk` ASC),
  CONSTRAINT `address_fk`
    FOREIGN KEY (`address_fk`)
    REFERENCES `clinic`.`Address` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `clinic`.`Provider`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`Provider` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(100) NOT NULL,
  `last_name` VARCHAR(100) NOT NULL,
  `provider_type` VARCHAR(45) NOT NULL,
  `active` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `clinic`.`Appointment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`Appointment` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `reason` VARCHAR(500) NOT NULL,
  `start_time` DATETIME NOT NULL,
  `end_time` DATETIME NOT NULL,
  `provider_fk` INT(11) NOT NULL,
  `patient_fk` INT(11) NOT NULL,
  `appt_type` VARCHAR(45) DEFAULT NULL,
  `status` VARCHAR(45) DEFAULT NULL,
  `smoker` TINYINT(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `appt_patient_fk_idx` (`patient_fk` ASC),
  INDEX `appt_provider_fk_idx` (`provider_fk` ASC),
  CONSTRAINT `appt_patient_fk`
    FOREIGN KEY (`patient_fk`)
    REFERENCES `clinic`.`Patient` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `appt_provider_fk`
    FOREIGN KEY (`provider_fk`)
    REFERENCES `clinic`.`Provider` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;


-- -----------------------------------------------------
-- Table `clinic`.`Availability`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `clinic`.`Availability` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `start_time` TIME NOT NULL,
  `end_time` TIME NOT NULL,
  `provider_fk` INT(11) NOT NULL,
  `monday` TINYINT(1) NOT NULL DEFAULT 0,
  `tuesday` TINYINT(1) NOT NULL DEFAULT 0,
  `wednesday` TINYINT(1) NOT NULL DEFAULT 0,
  `thursday` TINYINT(1) NOT NULL DEFAULT 0,
  `friday` TINYINT(1) NOT NULL DEFAULT 0,
  `saturday` TINYINT(1) NOT NULL DEFAULT 0,
  `sunday` TINYINT(1) NOT NULL DEFAULT 0,
  `week` INT(1) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `provider_fk_idx` (`provider_fk` ASC),
  CONSTRAINT `provider_fk`
    FOREIGN KEY (`provider_fk`)
    REFERENCES `clinic`.`Provider` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE)
ENGINE = InnoDB
DEFAULT CHARACTER SET = latin1;

CREATE TABLE IF NOT EXISTS `clinic`.`user`(`id` INTEGER(11) NOT NULL AUTO_INCREMENT, `username` varchar(30) NOT NULL UNIQUE, `role` varchar(30) NOT NULL, PRIMARY KEY(`id`));

CREATE USER IF NOT EXISTS 'clinic_admin'@'%' IDENTIFIED BY 'defaultAdminPassword';
GRANT ALL ON clinic.* TO 'clinic_admin' WITH GRANT OPTION;
GRANT CREATE USER ON *.* TO 'clinic_admin' WITH GRANT OPTION;
INSERT INTO clinic.user(username, role) VALUES('clinic_admin', 'ADMIN');