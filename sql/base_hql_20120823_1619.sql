-- MySQL Administrator dump 1.4
--
-- ------------------------------------------------------
-- Server version	5.1.63-0ubuntu0.10.04.1


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


--
-- Create schema hqldb
--

CREATE DATABASE IF NOT EXISTS hqldb;
USE hqldb;

--
-- Definition of table `hqldb`.`DataEntity`
--

DROP TABLE IF EXISTS `hqldb`.`DataEntity`;
CREATE TABLE  `hqldb`.`DataEntity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `colummns` varchar(255) DEFAULT NULL,
  `tables` varchar(255) DEFAULT NULL,
  `alias` varchar(255) DEFAULT NULL,
  `countColumn` varchar(255) DEFAULT NULL,
  `defaultOrderBy` varchar(255) DEFAULT NULL,
  `groupColumn` varchar(255) DEFAULT NULL,
  `sumColumn` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`DataEntity`
--

/*!40000 ALTER TABLE `DataEntity` DISABLE KEYS */;
LOCK TABLES `DataEntity` WRITE;
INSERT INTO `hqldb`.`DataEntity` VALUES  (1,'razon_social, cuit, tipo_cliente, total_ventas','clientes','clientes','razon_social','razon_social','tipo_cliente','total_ventas'),
 (2,'id_alumno, nombre, apellido, legajo, carrera','alumnos','alumnos','id_alumno','legajo','carrera','carrera'),
 (3,NULL,NULL,'',NULL,NULL,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `DataEntity` ENABLE KEYS */;


--
-- Definition of table `hqldb`.`NaturalQueryCommand`
--

DROP TABLE IF EXISTS `hqldb`.`NaturalQueryCommand`;
CREATE TABLE  `hqldb`.`NaturalQueryCommand` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `commandType` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`NaturalQueryCommand`
--

/*!40000 ALTER TABLE `NaturalQueryCommand` DISABLE KEYS */;
LOCK TABLES `NaturalQueryCommand` WRITE;
INSERT INTO `hqldb`.`NaturalQueryCommand` VALUES  (1,'action','contar'),
 (2,'action','graficar'),
 (3,'action','listar');
UNLOCK TABLES;
/*!40000 ALTER TABLE `NaturalQueryCommand` ENABLE KEYS */;


--
-- Definition of table `hqldb`.`alumnos`
--

DROP TABLE IF EXISTS `hqldb`.`alumnos`;
CREATE TABLE  `hqldb`.`alumnos` (
  `id_alumno` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `apellido` varchar(255) NOT NULL,
  `legajo` int(11) NOT NULL,
  `carrera` varchar(255) NOT NULL,
  PRIMARY KEY (`id_alumno`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`alumnos`
--

/*!40000 ALTER TABLE `alumnos` DISABLE KEYS */;
LOCK TABLES `alumnos` WRITE;
INSERT INTO `hqldb`.`alumnos` VALUES  (0,'nombre2','apellido2',2222,'informatica'),
 (1,'nombre2','apellido2',2222,'medicina');
UNLOCK TABLES;
/*!40000 ALTER TABLE `alumnos` ENABLE KEYS */;


--
-- Definition of table `hqldb`.`position1`
--

DROP TABLE IF EXISTS `hqldb`.`position1`;
CREATE TABLE  `hqldb`.`position1` (
  `value` char(1) NOT NULL DEFAULT '',
  `desc` char(20) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=MyISAM AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`position1`
--

/*!40000 ALTER TABLE `position1` DISABLE KEYS */;
LOCK TABLES `position1` WRITE;
INSERT INTO `hqldb`.`position1` VALUES  ('A','Adjetivos',1),
 ('R','Advervios',2),
 ('D','Determinantes',3),
 ('N','Nombres',4),
 ('V','Verbos',5),
 ('P','Pronombres',6),
 ('C','Conjunciones',7),
 ('I','Interjecciones',8),
 ('S','Preposiciones',9),
 ('F','Signos de Puntuacion',10),
 ('Z','Cifras y Numerales',11),
 ('W','Fechas y Horas',12);
UNLOCK TABLES;
/*!40000 ALTER TABLE `position1` ENABLE KEYS */;


--
-- Definition of table `hqldb`.`position2`
--

DROP TABLE IF EXISTS `hqldb`.`position2`;
CREATE TABLE  `hqldb`.`position2` (
  `id` int(11) NOT NULL DEFAULT '0',
  `value` char(1) DEFAULT NULL,
  `desc` char(20) DEFAULT NULL,
  `id_position1` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`position2`
--

/*!40000 ALTER TABLE `position2` DISABLE KEYS */;
LOCK TABLES `position2` WRITE;
INSERT INTO `hqldb`.`position2` VALUES  (1,'Q','Calificativo',1),
 (3,'0','-',1),
 (2,'O','Ordinal',1),
 (5,'N','Negativo',2),
 (4,'G','General',2),
 (11,'A','Articulo',3),
 (10,'I','Indefinido',3),
 (9,'E','Exclamativo',3),
 (8,'T','Interrogativo',3),
 (7,'P','Posesivo',3),
 (6,'D','Demostrativo',3),
 (13,'P','Propio',4),
 (12,'C','Comun',4),
 (16,'S','Semiauxiliar',5),
 (15,'A','Auxiliar',5),
 (14,'M','Principal',5),
 (23,'E','Exclamativo',6),
 (22,'R','Relativo',6),
 (21,'T','Interrogativo',6),
 (20,'I','Indefinido',6),
 (19,'X','Posesivo',6),
 (18,'D','Demostrativo',6),
 (17,'P','Personal',6),
 (30,'u','unidad',11),
 (29,'p','porcentaje',11),
 (28,'m','Moneda',11),
 (27,'d','partitivo',11),
 (26,'P','Preposicion',9),
 (25,'S','Subordinada',7),
 (24,'C','Coordinada',7);
UNLOCK TABLES;
/*!40000 ALTER TABLE `position2` ENABLE KEYS */;


--
-- Definition of table `hqldb`.`position3`
--

DROP TABLE IF EXISTS `hqldb`.`position3`;
CREATE TABLE  `hqldb`.`position3` (
  `id` int(11) NOT NULL DEFAULT '0',
  `value` char(1) DEFAULT NULL,
  `desc` char(20) DEFAULT NULL,
  `id_position1` int(11) DEFAULT NULL,
  `id_position2` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`position3`
--

/*!40000 ALTER TABLE `position3` DISABLE KEYS */;
LOCK TABLES `position3` WRITE;
UNLOCK TABLES;
/*!40000 ALTER TABLE `position3` ENABLE KEYS */;


--
-- Definition of table `hqldb`.`tags`
--

DROP TABLE IF EXISTS `hqldb`.`tags`;
CREATE TABLE  `hqldb`.`tags` (
  `position1` char(1) DEFAULT NULL,
  `position2` char(1) DEFAULT NULL,
  `position3` char(1) DEFAULT NULL,
  `position4` char(1) DEFAULT NULL,
  `position5` char(1) DEFAULT NULL,
  `position6` char(1) DEFAULT NULL,
  `position7` char(1) DEFAULT NULL,
  `position8` char(1) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`tags`
--

/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
LOCK TABLES `tags` WRITE;
INSERT INTO `hqldb`.`tags` VALUES  ('A',NULL,NULL,NULL,NULL,NULL,NULL,NULL);
UNLOCK TABLES;
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
