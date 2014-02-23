CREATE DATABASE `tradingterminal`;

CREATE TABLE `tradingterminal`.`products` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` INT UNSIGNED NOT NULL,
  `name` VARCHAR(50) NOT NULL,
  `ispacked` BIT NOT NULL,
  `count` FLOAT UNSIGNED NOT NULL,
  `cost` FLOAT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  UNIQUE INDEX `code_UNIQUE` (`code` ASC));

 CREATE TABLE `tradingterminal`.`simulations` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `date` DATE NULL,
  `peoplecount` INT UNSIGNED NOT NULL,
  `goodscount` INT UNSIGNED NOT NULL,
  `peoplearrived` INT UNSIGNED NOT NULL,
  `peopleserved` INT UNSIGNED NOT NULL,
  `avggoodscount` FLOAT UNSIGNED NOT NULL,
  `avgprofit` FLOAT UNSIGNED NOT NULL,
  `profit` INT UNSIGNED NOT NULL,
  `maxqueue` INT UNSIGNED NOT NULL,
  `maxqueuetime` TIME NOT NULL,
  `iscorrect` BIT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC));
  
CREATE TABLE `tradingterminal`.`reports` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `simulationid` INT UNSIGNED NOT NULL,
  `buyerid` INT UNSIGNED NOT NULL,
  `productcode` INT UNSIGNED NOT NULL,
  `count` FLOAT UNSIGNED NOT NULL,
  `time` TIME NOT NULL,
  `cost` INT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `sim` (`simulationid` ASC),
  INDEX `tim` (`time` ASC),
  INDEX `productkey_idx` (`productcode` ASC),
  CONSTRAINT `simkey`
    FOREIGN KEY (`simulationid`)
    REFERENCES `tradingterminal`.`simulations` (`id`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
  CONSTRAINT `productkey`
    FOREIGN KEY (`productcode`)
    REFERENCES `tradingterminal`.`products` (`code`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT);
