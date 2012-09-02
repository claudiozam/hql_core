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
 (2,'id_alumno, nombre, apellido, legajo, carrera, fecha_ingreso','alumnos','alumnos','id_alumno','legajo','carrera','carrera'),
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
  `fecha_ingreso` date NOT NULL,
  PRIMARY KEY (`id_alumno`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `hqldb`.`alumnos`
--

/*!40000 ALTER TABLE `alumnos` DISABLE KEYS */;
LOCK TABLES `alumnos` WRITE;
INSERT INTO `hqldb`.`alumnos` VALUES  (1,'nombre1','apellido1',1,'Informatica','2012-03-01'),
 (2,'nombre2','apellido2',2,'Abogacía','2012-08-01'),
 (3,'nombre3','apellido3',3,'Abogacia','2011-03-01'),
 (4,'nombre4','apellido4',4,'Informatica','2011-03-01'),
 (5,'nombre5','apellido5',5,'Arquitectura','2005-03-01'),
 (6,'nombre6','apellido6',6,'Administracion','2011-08-01'),
 (7,'nombre7','apellido7',7,'Contador Publico','2011-03-01'),
 (8,'nombre8','apellido8',8,'Administracion','2011-08-01'),
 (9,'nombre9','apellido9',9,'Hoteleria','2010-08-01'),
 (10,'nombre10','apellido10',10,'Periodismo','2012-08-01'),
 (11,'nombre11','apellido11',11,'Psicologia','2009-03-01'),
 (12,'nombre12','apellido12',12,'Diseño Grafico','2012-03-01'),
 (13,'nombre13','apellido13',13,'Informatica','2012-03-01'),
 (14,'nombre14','apellido14',14,'Informatica','2010-03-01'),
 (15,'nombre15','apellido15',15,'Periodismo','2010-03-01'),
 (16,'nombre16','apellido16',16,'Arquitectura','2005-03-01'),
 (17,'nombre17','apellido17',17,'Arquitectura','2005-08-01');
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




/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
