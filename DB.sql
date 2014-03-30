DROP DATABASE IF EXISTS `tradingterminal`;
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
    UNIQUE INDEX `code_UNIQUE` (`code` ASC)
);

CREATE TABLE `tradingterminal`.`simulations` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
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
    UNIQUE INDEX `id_UNIQUE` (`id` ASC)
);
  
CREATE TABLE `tradingterminal`.`reports` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `simulationid` INT UNSIGNED NOT NULL,
    `buyerid` INT UNSIGNED NOT NULL,
    `productcode` INT UNSIGNED NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `count` FLOAT NOT NULL,
    `time` TIME NOT NULL,
    `cost` INT NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `id_UNIQUE` (`id` ASC),
    INDEX `sim` (`simulationid` ASC),
    INDEX `tim` (`time` ASC),
    INDEX `productkey_idx` (`productcode` ASC),
    CONSTRAINT `simkey` FOREIGN KEY (`simulationid`)
        REFERENCES `tradingterminal`.`simulations` (`id`)
        ON DELETE RESTRICT ON UPDATE RESTRICT
);

INSERT INTO `tradingterminal`.`products` VALUES (1,2,'Хлеб \"Общажный с корочкой\"','',2,25.5),(2,3,'Молоко \"ИАТЭшное\"','',1,45),(3,1,'Газета \"Так или нету\"','',1,10),(4,555,'Утюг \"Староста\"','',1,666),(5,10,'Сыр \"Олежка\"','\0',0.3,270),(6,102,'Муз. Сборник \"Кто поёт\"','',1,100),(8,556,'Курица гриль \"Хан-Петухан\"','\0',1.05,175),(9,123,'Освежитель воздуха \"Брянск\"','',1,75),(11,4,'Колбаса \"Цилиндрик\"','\0',0.3,275),(12,6,'Картошка \"Бульба\"','\0',4,45);