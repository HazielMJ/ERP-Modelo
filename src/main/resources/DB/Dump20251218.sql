-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: erp
-- ------------------------------------------------------
-- Server version	8.0.43

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `almacen`
--

DROP TABLE IF EXISTS `almacen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `almacen` (
  `id_almacen` int NOT NULL AUTO_INCREMENT,
  `codigo_almacen` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_almacen` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ubicacion` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `capacidad_total` int DEFAULT NULL,
  `espacio_utilizado` int DEFAULT '0',
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_almacen`),
  UNIQUE KEY `codigo_almacen` (`codigo_almacen`),
  KEY `idx_codigo` (`codigo_almacen`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `almacen`
--

LOCK TABLES `almacen` WRITE;
/*!40000 ALTER TABLE `almacen` DISABLE KEYS */;
INSERT INTO `almacen` VALUES (1,'ALM-001','Almacén Principal','Bodega Central',10000,0,'ACTIVO','2025-11-06 16:59:30');
/*!40000 ALTER TABLE `almacen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `apertura_caja`
--

DROP TABLE IF EXISTS `apertura_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `apertura_caja` (
  `id_apertura` int NOT NULL AUTO_INCREMENT,
  `id_caja` int NOT NULL,
  `id_usuario` int NOT NULL,
  `fecha_apertura` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_cierre` datetime DEFAULT NULL,
  `saldo_inicial` double NOT NULL,
  `saldo_final` double NOT NULL,
  `total_ingresos` double NOT NULL,
  `total_egresos` double NOT NULL,
  `saldo_esperado` decimal(15,2) GENERATED ALWAYS AS (((`saldo_inicial` + `total_ingresos`) - `total_egresos`)) VIRTUAL,
  `saldo_real` double NOT NULL,
  `diferencia` decimal(15,2) GENERATED ALWAYS AS ((`saldo_real` - ((`saldo_inicial` + `total_ingresos`) - `total_egresos`))) VIRTUAL,
  `estado` enum('ABIERTA','CERRADA') COLLATE utf8mb4_unicode_ci DEFAULT 'ABIERTA',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_apertura`),
  KEY `idx_caja` (`id_caja`),
  KEY `idx_fecha` (`fecha_apertura`),
  KEY `idx_estado` (`estado`),
  KEY `idx_usuario` (`id_usuario`),
  CONSTRAINT `apertura_caja_ibfk_1` FOREIGN KEY (`id_caja`) REFERENCES `caja` (`id_caja`) ON DELETE RESTRICT,
  CONSTRAINT `apertura_caja_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_apertura_saldo` CHECK ((`saldo_inicial` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `apertura_caja`
--

LOCK TABLES `apertura_caja` WRITE;
/*!40000 ALTER TABLE `apertura_caja` DISABLE KEYS */;
INSERT INTO `apertura_caja` (`id_apertura`, `id_caja`, `id_usuario`, `fecha_apertura`, `fecha_cierre`, `saldo_inicial`, `saldo_final`, `total_ingresos`, `total_egresos`, `saldo_real`, `estado`, `observaciones`) VALUES (1,1,1,'2024-11-01 08:00:00','2024-11-01 18:00:00',5000,21121.68,32243.36,0,21121.68,'CERRADA',NULL),(2,1,4,'2024-11-02 08:00:00','2024-11-02 18:00:00',5000,10760.52,12521.04,1000,10760.52,'CERRADA',NULL),(3,1,4,'2024-11-03 08:00:00','2024-11-03 18:00:00',5000,5435.68,1271.36,400,5435.68,'CERRADA',NULL),(4,1,1,'2024-11-04 08:00:00','2025-11-27 15:25:04',5000,0,1346.76,0,0,'CERRADA',NULL),(5,1,1,'2025-11-27 22:17:18',NULL,1000,0,0,0,1000,'ABIERTA','Apertura de caja\n');
/*!40000 ALTER TABLE `apertura_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asistencia`
--

DROP TABLE IF EXISTS `asistencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `asistencia` (
  `id_asistencia` int NOT NULL AUTO_INCREMENT,
  `id_empleado` int NOT NULL,
  `fecha` date NOT NULL,
  `hora_entrada` time DEFAULT NULL,
  `hora_salida` time DEFAULT NULL,
  `horas_trabajadas` decimal(5,2) DEFAULT '0.00',
  `horas_extras` decimal(5,2) DEFAULT '0.00',
  `retrasos_minutos` int DEFAULT '0',
  `tipo_asistencia` enum('PRESENTE','FALTA','JUSTIFICADA','PERMISO','VACACIONES','INCAPACIDAD') COLLATE utf8mb4_unicode_ci DEFAULT 'PRESENTE',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `justificacion` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_asistencia`),
  UNIQUE KEY `uk_empleado_fecha` (`id_empleado`,`fecha`),
  KEY `idx_empleado` (`id_empleado`),
  KEY `idx_fecha` (`fecha`),
  KEY `idx_tipo` (`tipo_asistencia`),
  CONSTRAINT `asistencia_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`) ON DELETE CASCADE,
  CONSTRAINT `chk_horas_validas` CHECK (((`horas_trabajadas` >= 0) and (`horas_extras` >= 0)))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asistencia`
--

LOCK TABLES `asistencia` WRITE;
/*!40000 ALTER TABLE `asistencia` DISABLE KEYS */;
INSERT INTO `asistencia` VALUES (1,6,'2024-10-28','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(2,6,'2024-10-29','09:05:00','18:00:00',7.92,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(3,6,'2024-10-30','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(4,6,'2024-10-31','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(5,6,'2024-11-01','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(6,7,'2024-10-28','09:15:00','18:00:00',7.75,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(7,7,'2024-10-29','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(8,7,'2024-10-30',NULL,NULL,0.00,0.00,0,'FALTA',NULL,NULL,'2025-11-06 17:22:19'),(9,7,'2024-10-31','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19'),(10,7,'2024-11-01','09:00:00','18:00:00',8.00,0.00,0,'PRESENTE',NULL,NULL,'2025-11-06 17:22:19');
/*!40000 ALTER TABLE `asistencia` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `caja`
--

DROP TABLE IF EXISTS `caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caja` (
  `id_caja` int NOT NULL AUTO_INCREMENT,
  `numero_caja` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_caja` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `ubicacion` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVA','INACTIVA','MANTENIMIENTO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVA',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_caja`),
  UNIQUE KEY `numero_caja` (`numero_caja`),
  KEY `idx_numero` (`numero_caja`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caja`
--

LOCK TABLES `caja` WRITE;
/*!40000 ALTER TABLE `caja` DISABLE KEYS */;
INSERT INTO `caja` VALUES (1,'CAJA-001','Caja Principal','Planta Baja','ACTIVA','2025-11-06 16:59:30'),(2,'CAJA-002','Caja Secundaria','Planta Baja','ACTIVA','2025-11-06 17:13:12'),(3,'CAJA-003','Caja Express','Entrada Principal','ACTIVA','2025-11-06 17:13:12');
/*!40000 ALTER TABLE `caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `codigo_categoria` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_categoria` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `categoria_padre_id` int DEFAULT NULL,
  `estado` enum('ACTIVA','INACTIVA') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVA',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `codigo_categoria` (`codigo_categoria`),
  KEY `idx_nombre` (`nombre_categoria`),
  KEY `idx_estado` (`estado`),
  KEY `fk_categoria_padre` (`categoria_padre_id`),
  CONSTRAINT `fk_categoria_padre` FOREIGN KEY (`categoria_padre_id`) REFERENCES `categoria` (`id_categoria`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'CAT-001','Electrónica','Productos electrónicos y tecnología',NULL,'ACTIVA','2025-11-06 16:59:30'),(2,'CAT-002','Hogar','Artículos para el hogar',NULL,'ACTIVA','2025-11-06 16:59:30'),(3,'CAT-003','Alimentos','Productos alimenticios',NULL,'ACTIVA','2025-11-06 16:59:30'),(4,'CAT-004','Ropa y Calzado','Prendas de vestir y calzado',NULL,'ACTIVA','2025-11-06 17:13:12'),(5,'CAT-005','Deportes','Artículos deportivos',NULL,'ACTIVA','2025-11-06 17:13:12'),(6,'CAT-006','Juguetería','Juguetes y juegos',NULL,'ACTIVA','2025-11-06 17:13:12'),(7,'CAT-007','Ferretería','Herramientas y materiales',NULL,'ACTIVA','2025-11-06 17:13:12'),(8,'CAT-008','Oficina','Artículos de oficina y papelería',NULL,'ACTIVA','2025-11-06 17:13:12');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cliente`
--

DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `tipo_cliente` enum('NATURAL','JURIDICA') COLLATE utf8mb4_unicode_ci DEFAULT 'NATURAL',
  `nombre_razon_social` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rfc` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` text COLLATE utf8mb4_unicode_ci,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `limite_credito` decimal(15,2) DEFAULT '0.00',
  `saldo_actual` decimal(15,2) DEFAULT '0.00',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cliente`),
  KEY `idx_nombre` (`nombre_razon_social`),
  KEY `idx_estado` (`estado`),
  KEY `idx_rfc` (`rfc`),
  CONSTRAINT `chk_cliente_limite` CHECK ((`limite_credito` >= 0)),
  CONSTRAINT `chk_cliente_saldo` CHECK ((`saldo_actual` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cliente`
--

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (1,'NATURAL','Cliente General',NULL,'0000000000','general@cliente.com',NULL,'ACTIVO',0.00,0.00,NULL,'2025-11-06 16:59:30','2025-11-06 16:59:30'),(2,'NATURAL','Juan Pérez Martínez','PEMJ850612ABC','5512345001','juan.perez@email.com','Av. Insurgentes 123, CDMX','ACTIVO',50000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(3,'JURIDICA','Comercializadora ABC S.A. de C.V.','COM951020XYZ','5523456001','ventas@comercializadoraabc.com','Calle Reforma 456, CDMX','ACTIVO',200000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(4,'NATURAL','María Fernanda Ruiz','RUFM900308DEF','5534567001','mafe.ruiz@email.com','Av. Universidad 789, CDMX','ACTIVO',30000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(5,'JURIDICA','Distribuidora El Sol S.A.','DIS880515GHI','5545678001','compras@distelsol.com','Blvd. Juárez 321, Estado de México','ACTIVO',150000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(6,'NATURAL','Roberto Carlos Gómez','GORC871225JKL','5556789001','roberto.gomez@email.com','Calle Hidalgo 654, CDMX','ACTIVO',25000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(7,'NATURAL','Laura Sánchez Vega','SAVL920430MNO','5567890001','laura.sanchez@email.com','Av. Constitución 987, CDMX','ACTIVO',35000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(8,'JURIDICA','Supermercados Unidos S.A.','SUP750620PQR','5578901001','proveedores@superunidos.com','Carr. México-Toluca Km 15','ACTIVO',500000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(9,'NATURAL','Pedro González Torres','GOTP881115STU','5589012001','pedro.torres@email.com','Calle Morelos 147, CDMX','ACTIVO',20000.00,0.00,NULL,'2025-11-06 17:13:12','2025-11-06 17:13:12'),(10,'NATURAL','Alexa Cortes Martinez','HBI334324BJN','5567493986','AlexaCortes@gmail.com','','ACTIVO',25000.00,1000.00,'','2025-11-28 02:33:47','2025-11-27 20:37:07');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `compra`
--

DROP TABLE IF EXISTS `compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `compra` (
  `id_compra` int NOT NULL AUTO_INCREMENT,
  `numero_compra` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_compra` date NOT NULL,
  `id_proveedor` int NOT NULL,
  `id_usuario` int NOT NULL,
  `numero_factura_proveedor` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subtotal` decimal(15,2) DEFAULT '0.00',
  `impuestos` decimal(15,2) DEFAULT '0.00',
  `descuento` decimal(15,2) DEFAULT '0.00',
  `total_compra` decimal(15,2) DEFAULT '0.00',
  `estado` enum('PENDIENTE','RECIBIDA','PARCIAL','ANULADA') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `tipo_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','CREDITO') COLLATE utf8mb4_unicode_ci DEFAULT 'EFECTIVO',
  `fecha_entrega_estimada` date DEFAULT NULL,
  `fecha_entrega_real` date DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_compra`),
  UNIQUE KEY `numero_compra` (`numero_compra`),
  KEY `id_usuario` (`id_usuario`),
  KEY `idx_fecha` (`fecha_compra`),
  KEY `idx_proveedor` (`id_proveedor`),
  KEY `idx_estado` (`estado`),
  KEY `idx_numero` (`numero_compra`),
  CONSTRAINT `compra_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`) ON DELETE RESTRICT,
  CONSTRAINT `compra_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_compra_total` CHECK ((`total_compra` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `compra`
--

LOCK TABLES `compra` WRITE;
/*!40000 ALTER TABLE `compra` DISABLE KEYS */;
INSERT INTO `compra` VALUES (1,'COM-2024-001','2024-10-15',1,1,'FAC-DTE-12345',47900.00,7664.00,0.00,55564.00,'RECIBIDA','CREDITO','2024-10-20',NULL,NULL,'2025-11-06 17:13:12'),(2,'COM-2024-002','2024-10-20',2,1,'FAC-IEM-67890',47000.00,7520.00,0.00,54520.00,'RECIBIDA','TRANSFERENCIA','2024-10-25',NULL,NULL,'2025-11-06 17:13:12'),(3,'COM-2024-003','2024-10-25',3,1,'FAC-ACE-11111',21460.00,3433.60,0.00,24893.60,'RECIBIDA','EFECTIVO','2024-10-28',NULL,NULL,'2025-11-06 17:13:12'),(4,'COM-2024-004','2024-11-01',4,1,'FAC-TCN-22222',25500.00,4080.00,0.00,29580.00,'PENDIENTE','CREDITO','2024-11-10',NULL,NULL,'2025-11-06 17:13:12');
/*!40000 ALTER TABLE `compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contabilidad`
--

DROP TABLE IF EXISTS `contabilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contabilidad` (
  `id_asiento` int NOT NULL AUTO_INCREMENT,
  `fecha_asiento` date NOT NULL,
  `numero_asiento` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `estado` enum('BORRADOR','CONTABILIZADO','ANULADO') COLLATE utf8mb4_unicode_ci DEFAULT 'BORRADOR',
  `id_usuario` int NOT NULL,
  `total_debe` decimal(15,2) DEFAULT '0.00',
  `total_haber` decimal(15,2) DEFAULT '0.00',
  `periodo_contable` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_contabilizacion` timestamp NULL DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_asiento`),
  UNIQUE KEY `numero_asiento` (`numero_asiento`),
  KEY `id_usuario` (`id_usuario`),
  KEY `idx_fecha_asiento` (`fecha_asiento`),
  KEY `idx_periodo` (`periodo_contable`),
  KEY `idx_estado` (`estado`),
  KEY `idx_numero` (`numero_asiento`),
  CONSTRAINT `contabilidad_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_balance` CHECK (((`total_debe` = `total_haber`) or (`estado` = _utf8mb4'BORRADOR')))
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contabilidad`
--

LOCK TABLES `contabilidad` WRITE;
/*!40000 ALTER TABLE `contabilidad` DISABLE KEYS */;
INSERT INTO `contabilidad` VALUES (1,'2024-11-01','ASI-2024-001','Venta de mercancía VEN-2024-001','CONTABILIZADO',1,14500.00,14500.00,'2024-11',NULL,'2025-11-06 17:22:19'),(2,'2024-11-01','ASI-2024-002','Compra de mercancía COM-2024-001','CONTABILIZADO',1,58000.00,58000.00,'2024-10',NULL,'2025-11-06 17:22:19'),(3,'2024-11-15','ASI-2024-003','Pago de nómina primera quincena octubre','CONTABILIZADO',1,98500.00,98500.00,'2024-10',NULL,'2025-11-06 17:22:19');
/*!40000 ALTER TABLE `contabilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `departamento`
--

DROP TABLE IF EXISTS `departamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `departamento` (
  `id_departamento` int NOT NULL AUTO_INCREMENT,
  `codigo_departamento` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_departamento` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `departamento_padre_id` int DEFAULT NULL,
  `id_jefe` int DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_departamento`),
  UNIQUE KEY `codigo_departamento` (`codigo_departamento`),
  KEY `idx_nombre` (`nombre_departamento`),
  KEY `idx_estado` (`estado`),
  KEY `fk_departamento_padre` (`departamento_padre_id`),
  KEY `fk_departamento_jefe` (`id_jefe`),
  CONSTRAINT `fk_departamento_jefe` FOREIGN KEY (`id_jefe`) REFERENCES `empleado` (`id_empleado`) ON DELETE SET NULL,
  CONSTRAINT `fk_departamento_padre` FOREIGN KEY (`departamento_padre_id`) REFERENCES `departamento` (`id_departamento`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `departamento`
--

LOCK TABLES `departamento` WRITE;
/*!40000 ALTER TABLE `departamento` DISABLE KEYS */;
INSERT INTO `departamento` VALUES (1,'DEPT-001','Dirección General','Dirección y administración general',NULL,1,'ACTIVO','2025-11-06 17:06:08'),(2,'DEPT-002','Recursos Humanos','Gestión de personal y nómina',NULL,2,'ACTIVO','2025-11-06 17:06:08'),(3,'DEPT-003','Ventas','Departamento de ventas y atención al cliente',NULL,3,'ACTIVO','2025-11-06 17:06:08'),(4,'DEPT-004','Compras','Gestión de compras y proveedores',NULL,5,'ACTIVO','2025-11-06 17:06:08'),(5,'DEPT-005','Almacén','Control de inventarios y logística',NULL,10,'ACTIVO','2025-11-06 17:06:08'),(6,'DEPT-006','Contabilidad','Contabilidad y finanzas',NULL,4,'ACTIVO','2025-11-06 17:06:08'),(7,'DEPT-007','Sistemas','Tecnologías de información',NULL,NULL,'ACTIVO','2025-11-06 17:06:08');
/*!40000 ALTER TABLE `departamento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_compra`
--

DROP TABLE IF EXISTS `detalle_compra`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_compra` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_compra` int NOT NULL,
  `id_producto` int NOT NULL,
  `cantidad` decimal(10,2) DEFAULT '1.00',
  `precio_unitario` decimal(15,2) DEFAULT '0.00',
  `descuento` decimal(15,2) DEFAULT '0.00',
  `subtotal` decimal(15,2) DEFAULT '0.00',
  `impuesto` decimal(15,2) DEFAULT '0.00',
  `total` decimal(15,2) DEFAULT '0.00',
  `cantidad_recibida` decimal(10,2) DEFAULT '0.00',
  PRIMARY KEY (`id_detalle`),
  KEY `idx_compra` (`id_compra`),
  KEY `idx_producto` (`id_producto`),
  CONSTRAINT `detalle_compra_ibfk_1` FOREIGN KEY (`id_compra`) REFERENCES `compra` (`id_compra`) ON DELETE CASCADE,
  CONSTRAINT `detalle_compra_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE RESTRICT,
  CONSTRAINT `chk_detalle_compra_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `chk_detalle_compra_recibida` CHECK (((`cantidad_recibida` >= 0) and (`cantidad_recibida` <= `cantidad`)))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_compra`
--

LOCK TABLES `detalle_compra` WRITE;
/*!40000 ALTER TABLE `detalle_compra` DISABLE KEYS */;
INSERT INTO `detalle_compra` VALUES (1,1,1,5.00,8500.00,0.00,42500.00,6800.00,49300.00,5.00),(2,1,4,3.00,1800.00,0.00,5400.00,864.00,6264.00,3.00),(3,2,2,50.00,150.00,0.00,7500.00,1200.00,8700.00,50.00),(4,2,3,30.00,450.00,0.00,13500.00,2160.00,15660.00,30.00),(5,2,5,40.00,650.00,0.00,26000.00,4160.00,30160.00,40.00),(6,3,10,200.00,35.00,0.00,7000.00,1120.00,8120.00,200.00),(7,3,11,250.00,18.00,0.00,4500.00,720.00,5220.00,250.00),(8,3,12,180.00,22.00,0.00,3960.00,633.60,4593.60,180.00),(9,3,13,300.00,20.00,0.00,6000.00,960.00,6960.00,300.00),(10,4,14,100.00,80.00,0.00,8000.00,1280.00,9280.00,0.00),(11,4,15,50.00,350.00,0.00,17500.00,2800.00,20300.00,0.00);
/*!40000 ALTER TABLE `detalle_compra` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_contable`
--

DROP TABLE IF EXISTS `detalle_contable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_contable` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_asiento` int NOT NULL,
  `numero_linea` int NOT NULL,
  `cuenta_contable` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion_linea` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `debe` decimal(15,2) DEFAULT '0.00',
  `haber` decimal(15,2) DEFAULT '0.00',
  PRIMARY KEY (`id_detalle`),
  KEY `idx_asiento` (`id_asiento`),
  KEY `idx_cuenta` (`cuenta_contable`),
  CONSTRAINT `detalle_contable_ibfk_1` FOREIGN KEY (`id_asiento`) REFERENCES `contabilidad` (`id_asiento`) ON DELETE CASCADE,
  CONSTRAINT `chk_debe_o_haber` CHECK ((((`debe` > 0) and (`haber` = 0)) or ((`haber` > 0) and (`debe` = 0))))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_contable`
--

LOCK TABLES `detalle_contable` WRITE;
/*!40000 ALTER TABLE `detalle_contable` DISABLE KEYS */;
INSERT INTO `detalle_contable` VALUES (7,1,1,'1105-001','Bancos - Efectivo recibido',14500.00,0.00),(8,1,2,'4101-001','Ventas - Productos',0.00,12500.00),(9,1,3,'2108-001','IVA por pagar',0.00,2000.00),(10,2,1,'1401-001','Inventarios - Mercancías',50000.00,0.00),(11,2,2,'1106-001','IVA Acreditable',8000.00,0.00),(12,2,3,'2201-001','Proveedores por pagar',0.00,58000.00),(13,3,1,'5101-001','Sueldos y salarios',98500.00,0.00),(14,3,2,'2107-001','Retenciones por pagar',0.00,13350.00),(15,3,3,'2108-002','IMSS por pagar',0.00,6150.00),(16,3,4,'1105-002','Bancos - Pago nómina',0.00,79000.00);
/*!40000 ALTER TABLE `detalle_contable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_nomina`
--

DROP TABLE IF EXISTS `detalle_nomina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_nomina` (
  `id_detalle_nomina` int NOT NULL AUTO_INCREMENT,
  `id_nomina` int NOT NULL,
  `tipo` enum('PERCEPCION','DEDUCCION') COLLATE utf8mb4_unicode_ci NOT NULL,
  `concepto` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `clave_sat` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `monto` decimal(12,2) NOT NULL,
  PRIMARY KEY (`id_detalle_nomina`),
  KEY `idx_nomina` (`id_nomina`),
  KEY `idx_tipo` (`tipo`),
  CONSTRAINT `detalle_nomina_ibfk_1` FOREIGN KEY (`id_nomina`) REFERENCES `nomina` (`id_nomina`) ON DELETE CASCADE,
  CONSTRAINT `chk_monto_positivo` CHECK ((`monto` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_nomina`
--

LOCK TABLES `detalle_nomina` WRITE;
/*!40000 ALTER TABLE `detalle_nomina` DISABLE KEYS */;
INSERT INTO `detalle_nomina` VALUES (1,1,'PERCEPCION','Sueldo Base','001',25000.00),(2,1,'DEDUCCION','ISR','002',2000.00),(3,1,'DEDUCCION','IMSS','001',1500.00),(4,2,'PERCEPCION','Sueldo Base','001',22500.00),(5,2,'DEDUCCION','ISR','002',1800.00),(6,2,'DEDUCCION','IMSS','001',1350.00);
/*!40000 ALTER TABLE `detalle_nomina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_venta`
--

DROP TABLE IF EXISTS `detalle_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_venta` int NOT NULL,
  `id_producto` int NOT NULL,
  `cantidad` double DEFAULT NULL,
  `precio_unitario` double DEFAULT NULL,
  `descuento` decimal(15,2) DEFAULT '0.00',
  `subtotal` double DEFAULT NULL,
  `impuesto` decimal(15,2) DEFAULT '0.00',
  `total` decimal(15,2) DEFAULT '0.00',
  PRIMARY KEY (`id_detalle`),
  KEY `idx_venta` (`id_venta`),
  KEY `idx_producto` (`id_producto`),
  CONSTRAINT `detalle_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE CASCADE,
  CONSTRAINT `detalle_venta_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE RESTRICT,
  CONSTRAINT `chk_detalle_venta_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `chk_detalle_venta_precio` CHECK ((`precio_unitario` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_venta`
--

LOCK TABLES `detalle_venta` WRITE;
/*!40000 ALTER TABLE `detalle_venta` DISABLE KEYS */;
INSERT INTO `detalle_venta` VALUES (1,1,1,1,12500,0.00,12500,2000.00,14500.00),(2,2,2,2,299,0.00,598,95.68,693.68),(3,2,3,1,799,0.00,799,127.84,926.84),(4,3,6,1,799,100.00,699,111.84,810.84),(5,4,14,3,149,0.00,447,71.52,518.52),(18,7,4,1,3200,0.00,3200,512.00,3712.00),(19,7,5,1,1299,0.00,1299,207.84,1506.84),(20,7,9,1,2199,300.00,1899,303.84,2202.84),(21,8,24,1,129,0.00,129,20.64,149.64),(22,9,24,1,129,0.00,129,20.64,149.64),(23,10,24,7,129,0.00,903,144.48,1047.48);
/*!40000 ALTER TABLE `detalle_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `empleado`
--

DROP TABLE IF EXISTS `empleado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `empleado` (
  `id_empleado` int NOT NULL AUTO_INCREMENT,
  `codigo_empleado` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `apellido` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_documento` enum('DNI','CURP','RFC','PASAPORTE','OTRO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_documento` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `genero` enum('M','F','OTRO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rfc` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `curp` varchar(18) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nss` varchar(11) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cp` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_contratacion` date NOT NULL,
  `fecha_termino` date DEFAULT NULL,
  `puesto_id` int DEFAULT NULL,
  `departamento_id` int DEFAULT NULL,
  `salario_base` decimal(12,2) DEFAULT '0.00',
  `tipo_contrato` enum('INDEFINIDO','TEMPORAL','PRACTICAS','HONORARIOS') COLLATE utf8mb4_unicode_ci DEFAULT 'INDEFINIDO',
  `jornada` enum('COMPLETA','PARCIAL','POR_HORAS') COLLATE utf8mb4_unicode_ci DEFAULT 'COMPLETA',
  `estado` enum('ACTIVO','INACTIVO','BAJA') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `rol` enum('ADMIN','GERENTE','VENDEDOR','CONTADOR','ALMACENISTA','RRHH','EMPLEADO') COLLATE utf8mb4_unicode_ci DEFAULT 'EMPLEADO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_empleado`),
  UNIQUE KEY `codigo_empleado` (`codigo_empleado`),
  KEY `puesto_id` (`puesto_id`),
  KEY `departamento_id` (`departamento_id`),
  KEY `idx_nombre` (`nombre`,`apellido`),
  KEY `idx_codigo` (`codigo_empleado`),
  KEY `idx_estado` (`estado`),
  KEY `idx_rol` (`rol`),
  CONSTRAINT `empleado_ibfk_1` FOREIGN KEY (`puesto_id`) REFERENCES `puesto` (`id_puesto`) ON DELETE SET NULL,
  CONSTRAINT `empleado_ibfk_2` FOREIGN KEY (`departamento_id`) REFERENCES `departamento` (`id_departamento`) ON DELETE SET NULL,
  CONSTRAINT `chk_fecha_termino` CHECK (((`fecha_termino` is null) or (`fecha_termino` >= `fecha_contratacion`)))
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `empleado`
--

LOCK TABLES `empleado` WRITE;
/*!40000 ALTER TABLE `empleado` DISABLE KEYS */;
INSERT INTO `empleado` VALUES (1,'EMP-001','Haziel','Machado Jimenez','RFC','XAXX010101000',NULL,NULL,'XAXX010101000','XAXX010101HDFXXX00',NULL,NULL,'Psicologia #66','5585296674','Haziel2702@gmail.com','2025-01-01',NULL,1,1,50000.00,'INDEFINIDO','COMPLETA','ACTIVO','ADMIN','2025-11-06 16:59:30','2025-11-10 01:04:58'),(2,'EMP-002','María','González López','RFC',NULL,NULL,NULL,'GOLM850315ABC','GOLM850315MDFNPR01',NULL,NULL,NULL,'5512345678','maria.gonzalez@empresa.com','2023-01-15',NULL,2,2,45000.00,'INDEFINIDO','COMPLETA','ACTIVO','GERENTE','2025-11-06 17:13:12','2025-11-06 17:13:12'),(3,'EMP-003','Carlos','Rodríguez Pérez','RFC',NULL,NULL,NULL,'ROPC820720XYZ','ROPC820720HDFRDR02',NULL,NULL,'Psicologia #66','5523456789','carlos.rodriguez@empresa.com','2023-02-01',NULL,2,3,42000.00,'INDEFINIDO','COMPLETA','ACTIVO','GERENTE','2025-11-06 17:13:12','2025-11-08 01:36:14'),(4,'EMP-004','Ana','Martínez Silva','RFC',NULL,NULL,NULL,'MASA900505DEF','MASA900505MDFRNN03',NULL,NULL,NULL,'5534567890','ana.martinez@empresa.com','2023-03-10',NULL,7,6,48000.00,'INDEFINIDO','COMPLETA','ACTIVO','CONTADOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(5,'EMP-005','Roberto','Sánchez Torres','RFC',NULL,NULL,NULL,'SATR880415GHI','SATR880415HDFSNN04',NULL,NULL,NULL,'5545678901','roberto.sanchez@empresa.com','2023-04-05',NULL,2,4,40000.00,'INDEFINIDO','COMPLETA','ACTIVO','GERENTE','2025-11-06 17:13:12','2025-11-06 17:13:12'),(6,'EMP-006','Luis','Hernández Cruz','RFC',NULL,NULL,NULL,'HECL920815JKL','HECL920815HDFRZS05',NULL,NULL,NULL,'5556789012','luis.hernandez@empresa.com','2023-06-01',NULL,4,3,12000.00,'INDEFINIDO','COMPLETA','ACTIVO','VENDEDOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(7,'EMP-007','Carmen','López Ramírez','RFC',NULL,NULL,NULL,'LORC950220MNO','LORC950220MDFRPM06',NULL,NULL,NULL,'5567890123','carmen.lopez@empresa.com','2023-07-15',NULL,4,3,11500.00,'INDEFINIDO','COMPLETA','ACTIVO','VENDEDOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(8,'EMP-008','Jorge','García Flores','RFC',NULL,NULL,NULL,'GAFJ881210PQR','GAFJ881210HDFSNN07',NULL,NULL,NULL,'5578901234','jorge.garcia@empresa.com','2023-08-20',NULL,4,3,12500.00,'INDEFINIDO','COMPLETA','ACTIVO','VENDEDOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(9,'EMP-009','Patricia','Morales Díaz','RFC',NULL,NULL,NULL,'MODP910325STU','MODP910325MDFRST08',NULL,NULL,NULL,'5589012345','patricia.morales@empresa.com','2024-01-10',NULL,4,3,11800.00,'INDEFINIDO','COMPLETA','ACTIVO','VENDEDOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(10,'EMP-010','Pedro','Ramírez García','RFC',NULL,NULL,NULL,'RAGP910625VWX','RAGP910625HDFMRD09',NULL,NULL,NULL,'5590123456','pedro.ramirez@empresa.com','2023-05-10',NULL,6,5,13000.00,'INDEFINIDO','COMPLETA','ACTIVO','ALMACENISTA','2025-11-06 17:13:12','2025-11-06 17:13:12'),(11,'EMP-011','Rosa','Flores Mendoza','RFC',NULL,NULL,NULL,'FLMR870330YZA','FLMR870330MDFLRS10',NULL,NULL,NULL,'5501234567','rosa.flores@empresa.com','2023-09-01',NULL,6,5,12800.00,'INDEFINIDO','COMPLETA','ACTIVO','ALMACENISTA','2025-11-06 17:13:12','2025-11-06 17:13:12'),(12,'EMP-012','Miguel','Torres Vargas','RFC',NULL,NULL,NULL,'TOVM890520BCD','TOVM890520HDFRRS11',NULL,NULL,NULL,'5512345679','miguel.torres@empresa.com','2023-10-15',NULL,6,5,13200.00,'INDEFINIDO','COMPLETA','ACTIVO','ALMACENISTA','2025-11-06 17:13:12','2025-11-06 17:13:12'),(13,'EMP-013','Diana','Castro Ruiz','RFC',NULL,NULL,NULL,'CARD931115EFG','CARD931115MDFRST12',NULL,NULL,NULL,'5523456780','diana.castro@empresa.com','2024-01-10',NULL,5,3,10000.00,'INDEFINIDO','COMPLETA','ACTIVO','VENDEDOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(14,'EMP-014','Javier','Jiménez Ortiz','RFC',NULL,NULL,NULL,'JIOJ940820HIJ','JIOJ940820HDFJMB13',NULL,NULL,NULL,'5534567891','javier.jimenez@empresa.com','2024-02-15',NULL,5,3,9800.00,'INDEFINIDO','COMPLETA','ACTIVO','VENDEDOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(15,'EMP-015','Laura','Vázquez Medina','RFC',NULL,NULL,NULL,'VAML870615KLM','VAML870615MDFZDR14',NULL,NULL,NULL,'5545678902','laura.vazquez@empresa.com','2023-05-20',NULL,8,6,18000.00,'INDEFINIDO','COMPLETA','ACTIVO','CONTADOR','2025-11-06 17:13:12','2025-11-06 17:13:12'),(16,'EMP-016','Fernando','Ríos Gutiérrez','RFC',NULL,NULL,NULL,'RIGF860910NOP','RIGF860910HDFSTR15',NULL,NULL,NULL,'5556789013','fernando.rios@empresa.com','2023-06-10',NULL,9,4,20000.00,'INDEFINIDO','COMPLETA','ACTIVO','EMPLEADO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(17,'EMP-017','José','Mendoza Salazar','RFC',NULL,NULL,NULL,'MESJ920305QRS','MESJ920305HDFNLS16',NULL,NULL,NULL,'5567890124','jose.mendoza@empresa.com','2023-11-01',NULL,10,5,12000.00,'INDEFINIDO','COMPLETA','ACTIVO','EMPLEADO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(18,'EMP-018','Ricardo','Herrera Luna','RFC',NULL,NULL,NULL,'HELR880725TUV','HELR880725HDFRRC17',NULL,NULL,NULL,'5578901235','ricardo.herrera@empresa.com','2024-03-01',NULL,10,5,11500.00,'INDEFINIDO','COMPLETA','ACTIVO','EMPLEADO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(19,'EMP-38033','Alexa','Cortes','CURP','XAXX010101000','2012-07-17','M','XAXX010101220','ROPC820720HDFRMNAR','98543457775','57440','Psicologia #66','5585296674','AlexaCort@gmail.com','2025-12-17',NULL,7,6,2300.00,'INDEFINIDO','PARCIAL','ACTIVO','EMPLEADO','2025-12-18 04:48:58','2025-12-18 04:48:58'),(20,'EMP-24324','Eduardo','Castro','CURP','XAXX010101020','1995-10-18','M','ROPC820720MW6','MAJE020627HMXRA4','98543457775','57440','Ciencias #45','5523453986','Edu@gmail.com','2025-12-18',NULL,1,1,5600.00,'TEMPORAL','COMPLETA','ACTIVO','EMPLEADO','2025-12-18 18:05:24','2025-12-18 18:05:24');
/*!40000 ALTER TABLE `empleado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `envio`
--

DROP TABLE IF EXISTS `envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `envio` (
  `id_envio` int NOT NULL AUTO_INCREMENT,
  `numero_guia` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_venta` int DEFAULT NULL,
  `id_cliente` int NOT NULL,
  `id_metodo_envio` int DEFAULT NULL,
  `id_ruta` int DEFAULT NULL,
  `id_transportista` int DEFAULT NULL,
  `direccion_entrega` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `ciudad` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_destino` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `codigo_postal` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono_contacto` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `referencia_direccion` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` datetime DEFAULT CURRENT_TIMESTAMP,
  `fecha_estimada_entrega` date DEFAULT NULL,
  `fecha_entrega_real` date DEFAULT NULL,
  `estado` enum('PENDIENTE','PREPARANDO','EN_TRANSITO','ENTREGADO','CANCELADO','DEVUELTO') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `costo_envio` decimal(10,2) DEFAULT '0.00',
  `peso_kg` decimal(10,2) DEFAULT '0.00',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_envio`),
  UNIQUE KEY `numero_guia` (`numero_guia`),
  KEY `id_venta` (`id_venta`),
  KEY `id_metodo_envio` (`id_metodo_envio`),
  KEY `id_ruta` (`id_ruta`),
  KEY `id_transportista` (`id_transportista`),
  KEY `idx_numero_guia` (`numero_guia`),
  KEY `idx_estado` (`estado`),
  KEY `idx_fecha_creacion` (`fecha_creacion`),
  KEY `idx_cliente` (`id_cliente`),
  CONSTRAINT `envio_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE SET NULL,
  CONSTRAINT `envio_ibfk_2` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT,
  CONSTRAINT `envio_ibfk_3` FOREIGN KEY (`id_metodo_envio`) REFERENCES `metodo_envio` (`id_metodo_envio`) ON DELETE SET NULL,
  CONSTRAINT `envio_ibfk_4` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`) ON DELETE SET NULL,
  CONSTRAINT `envio_ibfk_5` FOREIGN KEY (`id_transportista`) REFERENCES `transportista` (`id_transportista`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `envio`
--

LOCK TABLES `envio` WRITE;
/*!40000 ALTER TABLE `envio` DISABLE KEYS */;
INSERT INTO `envio` VALUES (1,'ENV-2024-001',1,2,2,1,1,'Calle Reforma 456, Col. Centro','CDMX','Ciudad de México','06000','5523456001',NULL,'2025-11-06 11:22:19','2024-11-02',NULL,'ENTREGADO',120.00,5.50,NULL),(2,'ENV-2024-002',4,5,1,2,2,'Calle Hidalgo 654, Col. Del Valle','CDMX','Ciudad de México','03100','5556789001',NULL,'2025-11-06 11:22:19','2024-11-05',NULL,'EN_TRANSITO',50.00,8.30,NULL),(3,'ENV-2024-003',5,4,3,3,3,'Blvd. Juárez 321, Col. Industrial','Naucalpan','Estado de México','53370','5545678001',NULL,'2025-11-06 11:22:19','2024-11-06','2025-12-01','PREPARANDO',250.00,12.00,NULL);
/*!40000 ALTER TABLE `envio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `factura`
--

DROP TABLE IF EXISTS `factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `factura` (
  `id_factura` int NOT NULL AUTO_INCREMENT,
  `numero_factura` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `serie` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `folio` int DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `id_cliente` int NOT NULL,
  `id_venta` int DEFAULT NULL,
  `id_compra` int DEFAULT NULL,
  `subtotal` decimal(15,2) DEFAULT '0.00',
  `impuestos` decimal(15,2) DEFAULT '0.00',
  `descuentos` decimal(15,2) DEFAULT '0.00',
  `total` decimal(15,2) DEFAULT '0.00',
  `estado` enum('PENDIENTE','PAGADA','VENCIDA','ANULADA') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `tipo_factura` enum('VENTA','COMPRA') COLLATE utf8mb4_unicode_ci DEFAULT 'VENTA',
  `forma_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') COLLATE utf8mb4_unicode_ci DEFAULT 'EFECTIVO',
  `uso_cfdi` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `metodo_pago` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uuid_fiscal` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_factura`),
  UNIQUE KEY `numero_factura` (`numero_factura`),
  KEY `id_venta` (`id_venta`),
  KEY `id_compra` (`id_compra`),
  KEY `id_usuario` (`id_usuario`),
  KEY `idx_numero` (`numero_factura`),
  KEY `idx_fecha` (`fecha_emision`),
  KEY `idx_estado` (`estado`),
  KEY `idx_cliente` (`id_cliente`),
  KEY `idx_tipo_factura` (`tipo_factura`),
  CONSTRAINT `factura_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT,
  CONSTRAINT `factura_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE SET NULL,
  CONSTRAINT `factura_ibfk_3` FOREIGN KEY (`id_compra`) REFERENCES `compra` (`id_compra`) ON DELETE SET NULL,
  CONSTRAINT `factura_ibfk_4` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_factura_vencimiento` CHECK ((`fecha_vencimiento` >= `fecha_emision`))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `factura`
--

LOCK TABLES `factura` WRITE;
/*!40000 ALTER TABLE `factura` DISABLE KEYS */;
INSERT INTO `factura` VALUES (1,'FAC-A-001','A',1,'2024-11-01','2024-11-16',2,1,NULL,12500.00,2000.00,0.00,14500.00,'PAGADA','VENTA','EFECTIVO','G03','PUE',NULL,1,'2025-11-06 17:22:19'),(2,'FAC-A-002','A',2,'2024-11-01','2024-11-16',3,2,NULL,1398.00,223.68,0.00,1621.68,'PAGADA','VENTA','TARJETA','P01','PUE',NULL,4,'2025-11-06 17:22:19'),(3,'FAC-A-003','A',3,'2024-11-02','2024-11-17',5,4,NULL,5397.00,863.52,0.00,6260.52,'PAGADA','VENTA','TRANSFERENCIA','G01','PUE',NULL,1,'2025-11-06 17:22:19'),(4,'FAC-A-004','A',4,'2024-11-02','2024-12-02',4,5,NULL,25000.00,4000.00,0.00,28000.00,'PENDIENTE','VENTA','MIXTO','G03','PPD',NULL,4,'2025-11-06 17:22:19');
/*!40000 ALTER TABLE `factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facturacion`
--

DROP TABLE IF EXISTS `facturacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturacion` (
  `id_factura` int NOT NULL AUTO_INCREMENT,
  `descuentos` decimal(15,2) DEFAULT NULL,
  `estado` enum('PENDIENTE','PAGADA','VENCIDA','ANULADA') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `folio` int DEFAULT NULL,
  `forma_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') COLLATE utf8mb4_unicode_ci DEFAULT 'EFECTIVO',
  `impuestos` decimal(15,2) DEFAULT NULL,
  `metodo_pago` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_factura` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `serie` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `subtotal` decimal(15,2) DEFAULT NULL,
  `tipo_factura` enum('VENTA','COMPRA') COLLATE utf8mb4_unicode_ci DEFAULT 'VENTA',
  `total` decimal(15,2) DEFAULT NULL,
  `uso_cfdi` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `uuid_fiscal` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_cliente` int NOT NULL,
  `id_usuario` int NOT NULL,
  `id_venta` int DEFAULT NULL,
  PRIMARY KEY (`id_factura`),
  UNIQUE KEY `UKnyearh0t83fe9918t5e74fudg` (`numero_factura`),
  KEY `FKgk6cwg035pqa78ee5icw7peec` (`id_cliente`),
  KEY `FKhkralnseykr6noa3cowj22m5v` (`id_usuario`),
  KEY `FKnppht61xn17idmx1ra8cv234w` (`id_venta`),
  CONSTRAINT `FKgk6cwg035pqa78ee5icw7peec` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `FKhkralnseykr6noa3cowj22m5v` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`),
  CONSTRAINT `FKnppht61xn17idmx1ra8cv234w` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturacion`
--

LOCK TABLES `facturacion` WRITE;
/*!40000 ALTER TABLE `facturacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `facturacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `impuesto`
--

DROP TABLE IF EXISTS `impuesto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `impuesto` (
  `id_impuesto` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo` enum('IVA','ISR','IEPS','OTRO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `tasa` decimal(5,2) NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_impuesto`),
  CONSTRAINT `chk_tasa` CHECK ((`tasa` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `impuesto`
--

LOCK TABLES `impuesto` WRITE;
/*!40000 ALTER TABLE `impuesto` DISABLE KEYS */;
INSERT INTO `impuesto` VALUES (1,'IVA General','IVA',16.00,'Impuesto al Valor Agregado tasa general','ACTIVO','2025-11-06 17:13:12'),(2,'IVA Frontera','IVA',8.00,'IVA tasa fronteriza','ACTIVO','2025-11-06 17:13:12'),(3,'IVA Exento','IVA',0.00,'Productos exentos de IVA','ACTIVO','2025-11-06 17:13:12'),(4,'IEPS Bebidas','IEPS',10.00,'IEPS para bebidas azucaradas','ACTIVO','2025-11-06 17:13:12'),(5,'ISR Retención','ISR',10.00,'Retención de ISR','ACTIVO','2025-11-06 17:13:12');
/*!40000 ALTER TABLE `impuesto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventario`
--

DROP TABLE IF EXISTS `inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventario` (
  `id_inventario` int NOT NULL AUTO_INCREMENT,
  `producto_id` int NOT NULL,
  `almacen_id` int NOT NULL,
  `stock_actual` int DEFAULT '0',
  `stock_reservado` int DEFAULT '0',
  `stock_disponible` int GENERATED ALWAYS AS ((`stock_actual` - `stock_reservado`)) VIRTUAL,
  `ubicacion_estante` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_ultimo_movimiento` datetime DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_inventario`),
  UNIQUE KEY `uk_producto_almacen` (`producto_id`,`almacen_id`),
  KEY `idx_producto` (`producto_id`),
  KEY `idx_almacen` (`almacen_id`),
  KEY `idx_stock` (`stock_actual`),
  CONSTRAINT `inventario_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`) ON DELETE CASCADE,
  CONSTRAINT `inventario_ibfk_2` FOREIGN KEY (`almacen_id`) REFERENCES `almacen` (`id_almacen`) ON DELETE CASCADE,
  CONSTRAINT `chk_stock_no_negativo` CHECK ((`stock_actual` >= 0)),
  CONSTRAINT `chk_stock_reservado_valido` CHECK (((`stock_reservado` >= 0) and (`stock_reservado` <= `stock_actual`)))
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventario`
--

LOCK TABLES `inventario` WRITE;
/*!40000 ALTER TABLE `inventario` DISABLE KEYS */;
INSERT INTO `inventario` (`id_inventario`, `producto_id`, `almacen_id`, `stock_actual`, `stock_reservado`, `ubicacion_estante`, `fecha_ultimo_movimiento`, `fecha_creacion`) VALUES (1,1,1,29,0,'A-01','2025-11-06 11:13:12','2025-11-06 17:13:12'),(2,2,1,198,0,'A-02','2025-11-06 11:13:12','2025-11-06 17:13:12'),(3,3,1,109,0,'A-03','2025-11-06 11:13:12','2025-11-06 17:13:12'),(4,4,1,37,0,'A-04','2025-11-06 11:20:38','2025-11-06 17:13:12'),(5,5,1,159,0,'A-05','2025-11-06 11:20:38','2025-11-06 17:13:12'),(6,6,1,44,0,'B-01','2025-11-06 11:13:12','2025-11-06 17:13:12'),(7,7,1,75,0,'B-02',NULL,'2025-11-06 17:13:12'),(8,8,1,200,0,'B-03',NULL,'2025-11-06 17:13:12'),(9,9,1,27,0,'B-04','2025-11-06 11:20:38','2025-11-06 17:13:12'),(10,10,1,650,0,'C-01','2025-11-06 11:13:12','2025-11-06 17:13:12'),(11,11,1,770,0,'C-02','2025-11-06 11:13:12','2025-11-06 17:13:12'),(12,12,1,560,0,'C-03','2025-11-06 11:13:12','2025-11-06 17:13:12'),(13,13,1,900,0,'C-04','2025-11-06 11:13:12','2025-11-06 17:13:12'),(14,14,1,177,0,'D-01','2025-11-06 11:13:12','2025-11-06 17:13:12'),(15,15,1,85,0,'D-02',NULL,'2025-11-06 17:13:12'),(16,16,1,95,0,'E-01',NULL,'2025-11-06 17:13:12'),(17,17,1,55,0,'E-02',NULL,'2025-11-06 17:13:12'),(18,18,1,110,0,'F-01',NULL,'2025-11-06 17:13:12'),(19,19,1,92,0,'F-02',NULL,'2025-11-06 17:13:12'),(20,20,1,65,0,'G-01',NULL,'2025-11-06 17:13:12'),(21,21,1,135,0,'G-02',NULL,'2025-11-06 17:13:12'),(22,22,1,175,0,'H-01',NULL,'2025-11-06 17:13:12'),(23,23,1,210,0,'H-02',NULL,'2025-11-06 17:13:12'),(24,24,1,96,0,'H-03','2025-11-25 00:09:25','2025-11-06 17:13:12'),(25,25,1,285,0,'H-04',NULL,'2025-11-06 17:13:12');
/*!40000 ALTER TABLE `inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metodo_envio`
--

DROP TABLE IF EXISTS `metodo_envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodo_envio` (
  `id_metodo_envio` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `costo_base` decimal(10,2) DEFAULT '0.00',
  `costo_por_km` decimal(10,2) DEFAULT '0.00',
  `tiempo_estimado` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_metodo_envio`),
  KEY `idx_nombre` (`nombre`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metodo_envio`
--

LOCK TABLES `metodo_envio` WRITE;
/*!40000 ALTER TABLE `metodo_envio` DISABLE KEYS */;
INSERT INTO `metodo_envio` VALUES (1,'Estándar','Entrega estándar 3-5 días hábiles',50.00,2.50,'3-5 días','ACTIVO','2025-11-06 17:13:12'),(2,'Express','Entrega express 24-48 horas',120.00,5.00,'24-48 horas','ACTIVO','2025-11-06 17:13:12'),(3,'Same Day','Entrega el mismo día',200.00,8.00,'Mismo día','ACTIVO','2025-11-06 17:13:12'),(4,'Recolección en Tienda','Cliente recoge en sucursal',0.00,0.00,'Inmediato','ACTIVO','2025-11-06 17:13:12'),(5,'Estándar','Entrega estándar 3-5 días hábiles',50.00,2.50,'3-5 días','ACTIVO','2025-12-02 00:11:13'),(6,'Express','Entrega express 24-48 horas',120.00,5.00,'24-48 horas','ACTIVO','2025-12-02 00:11:13'),(7,'Same Day','Entrega el mismo día',200.00,8.00,'Mismo día','ACTIVO','2025-12-02 00:11:13'),(8,'Recolección en Tienda','Cliente recoge en sucursal',0.00,0.00,'Inmediato','ACTIVO','2025-12-02 00:11:13'),(9,'Estándar','Entrega estándar 3-5 días hábiles',50.00,2.50,'3-5 días','ACTIVO','2025-12-02 00:11:55'),(10,'Express','Entrega express 24-48 horas',120.00,5.00,'24-48 horas','ACTIVO','2025-12-02 00:11:55'),(11,'Same Day','Entrega el mismo día',200.00,8.00,'Mismo día','ACTIVO','2025-12-02 00:11:55'),(12,'Recolección en Tienda','Cliente recoge en sucursal',0.00,0.00,'Inmediato','ACTIVO','2025-12-02 00:11:55');
/*!40000 ALTER TABLE `metodo_envio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento_caja`
--

DROP TABLE IF EXISTS `movimiento_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_caja` (
  `id_movimiento` bigint NOT NULL AUTO_INCREMENT,
  `id_apertura` int NOT NULL,
  `tipo_movimiento` enum('INGRESO','EGRESO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `categoria` enum('VENTA','COMPRA','GASTO','RETIRO','DEPOSITO','AJUSTE','OTRO') COLLATE utf8mb4_unicode_ci NOT NULL,
  `monto` double NOT NULL,
  `concepto` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `id_venta` int DEFAULT NULL,
  `forma_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','MIXTO') COLLATE utf8mb4_unicode_ci DEFAULT 'EFECTIVO',
  `referencia` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  `fecha_movimiento` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_usuario` (`id_usuario`),
  KEY `id_venta` (`id_venta`),
  KEY `idx_apertura` (`id_apertura`),
  KEY `idx_tipo` (`tipo_movimiento`),
  KEY `idx_fecha` (`fecha_movimiento`),
  KEY `idx_categoria` (`categoria`),
  CONSTRAINT `movimiento_caja_ibfk_1` FOREIGN KEY (`id_apertura`) REFERENCES `apertura_caja` (`id_apertura`) ON DELETE RESTRICT,
  CONSTRAINT `movimiento_caja_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `movimiento_caja_ibfk_3` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE SET NULL,
  CONSTRAINT `chk_movimiento_monto` CHECK ((`monto` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_caja`
--

LOCK TABLES `movimiento_caja` WRITE;
/*!40000 ALTER TABLE `movimiento_caja` DISABLE KEYS */;
INSERT INTO `movimiento_caja` VALUES (1,1,'INGRESO','VENTA',14500,'Venta VEN-2024-001',1,1,'EFECTIVO',NULL,NULL,'2024-11-01 16:30:00'),(2,1,'INGRESO','VENTA',1621.68,'Venta VEN-2024-002',4,2,'TARJETA',NULL,NULL,'2024-11-01 17:45:00'),(3,2,'INGRESO','VENTA',6260.52,'Venta VEN-2024-004',1,4,'TRANSFERENCIA',NULL,NULL,'2024-11-02 15:15:00'),(4,2,'EGRESO','GASTO',500,'Compra de insumos de limpieza',1,NULL,'EFECTIVO',NULL,NULL,'2024-11-02 20:30:00'),(5,3,'INGRESO','VENTA',635.68,'Venta VEN-2024-006',4,6,'EFECTIVO',NULL,NULL,'2024-11-03 18:00:00'),(6,3,'EGRESO','RETIRO',200,'Retiro para cambio',4,NULL,'EFECTIVO',NULL,NULL,'2024-11-03 21:00:00'),(7,4,'INGRESO','VENTA',149.64,'Venta VTA-1764029169973',1,8,'EFECTIVO','VTA-1764029169973',NULL,'2025-11-25 06:06:12'),(8,4,'INGRESO','VENTA',149.64,'Venta VTA-1764029213314',1,9,'EFECTIVO','VTA-1764029213314',NULL,'2025-11-25 06:06:54'),(9,4,'INGRESO','VENTA',1047.48,'Venta VTA-1764029365314',1,10,'EFECTIVO','VTA-1764029365314',NULL,'2025-11-25 06:09:26');
/*!40000 ALTER TABLE `movimiento_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento_inventario`
--

DROP TABLE IF EXISTS `movimiento_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_inventario` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `fecha_movimiento` datetime DEFAULT CURRENT_TIMESTAMP,
  `tipo_movimiento` enum('ENTRADA','SALIDA','AJUSTE','TRANSFERENCIA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `producto_id` int NOT NULL,
  `almacen_origen_id` int DEFAULT NULL,
  `almacen_destino_id` int DEFAULT NULL,
  `cantidad` decimal(10,2) NOT NULL,
  `costo_unitario` decimal(12,2) DEFAULT NULL,
  `referencia_tipo` enum('COMPRA','VENTA','AJUSTE','TRANSFERENCIA') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `referencia_id` int DEFAULT NULL,
  `id_usuario` int NOT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_movimiento`),
  KEY `almacen_origen_id` (`almacen_origen_id`),
  KEY `almacen_destino_id` (`almacen_destino_id`),
  KEY `id_usuario` (`id_usuario`),
  KEY `idx_fecha` (`fecha_movimiento`),
  KEY `idx_producto` (`producto_id`),
  KEY `idx_tipo` (`tipo_movimiento`),
  KEY `idx_referencia` (`referencia_tipo`,`referencia_id`),
  CONSTRAINT `movimiento_inventario_ibfk_1` FOREIGN KEY (`producto_id`) REFERENCES `producto` (`id_producto`) ON DELETE RESTRICT,
  CONSTRAINT `movimiento_inventario_ibfk_2` FOREIGN KEY (`almacen_origen_id`) REFERENCES `almacen` (`id_almacen`) ON DELETE SET NULL,
  CONSTRAINT `movimiento_inventario_ibfk_3` FOREIGN KEY (`almacen_destino_id`) REFERENCES `almacen` (`id_almacen`) ON DELETE SET NULL,
  CONSTRAINT `movimiento_inventario_ibfk_4` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_cantidad_positiva` CHECK ((`cantidad` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_inventario`
--

LOCK TABLES `movimiento_inventario` WRITE;
/*!40000 ALTER TABLE `movimiento_inventario` DISABLE KEYS */;
INSERT INTO `movimiento_inventario` VALUES (1,'2025-11-06 11:13:12','ENTRADA',1,NULL,1,5.00,8500.00,'COMPRA',1,1,NULL),(2,'2025-11-06 11:13:12','ENTRADA',4,NULL,1,3.00,1800.00,'COMPRA',1,1,NULL),(3,'2025-11-06 11:13:12','ENTRADA',2,NULL,1,50.00,150.00,'COMPRA',2,1,NULL),(4,'2025-11-06 11:13:12','ENTRADA',3,NULL,1,30.00,450.00,'COMPRA',2,1,NULL),(5,'2025-11-06 11:13:12','ENTRADA',5,NULL,1,40.00,650.00,'COMPRA',2,1,NULL),(6,'2025-11-06 11:13:12','ENTRADA',10,NULL,1,200.00,35.00,'COMPRA',3,1,NULL),(7,'2025-11-06 11:13:12','ENTRADA',11,NULL,1,250.00,18.00,'COMPRA',3,1,NULL),(8,'2025-11-06 11:13:12','ENTRADA',12,NULL,1,180.00,22.00,'COMPRA',3,1,NULL),(9,'2025-11-06 11:13:12','ENTRADA',13,NULL,1,300.00,20.00,'COMPRA',3,1,NULL),(10,'2025-11-06 11:13:12','SALIDA',1,1,NULL,1.00,12500.00,'VENTA',1,1,NULL),(11,'2025-11-06 11:13:12','SALIDA',2,1,NULL,2.00,299.00,'VENTA',2,4,NULL),(12,'2025-11-06 11:13:12','SALIDA',3,1,NULL,1.00,799.00,'VENTA',2,4,NULL),(13,'2025-11-06 11:13:12','SALIDA',6,1,NULL,1.00,799.00,'VENTA',3,4,NULL),(14,'2025-11-06 11:13:12','SALIDA',14,1,NULL,3.00,149.00,'VENTA',4,1,NULL),(23,'2025-11-06 11:20:38','SALIDA',4,1,NULL,1.00,3200.00,'VENTA',7,1,NULL),(24,'2025-11-06 11:20:38','SALIDA',5,1,NULL,1.00,1299.00,'VENTA',7,1,NULL),(25,'2025-11-06 11:20:38','SALIDA',9,1,NULL,1.00,2199.00,'VENTA',7,1,NULL),(26,'2025-11-24 18:06:10','SALIDA',24,1,NULL,1.00,129.00,'VENTA',8,1,NULL),(27,'2025-11-24 18:06:53','SALIDA',24,1,NULL,1.00,129.00,'VENTA',9,1,NULL),(28,'2025-11-24 18:09:25','SALIDA',24,1,NULL,7.00,129.00,'VENTA',10,1,NULL);
/*!40000 ALTER TABLE `movimiento_inventario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nomina`
--

DROP TABLE IF EXISTS `nomina`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nomina` (
  `id_nomina` int NOT NULL AUTO_INCREMENT,
  `id_empleado` int NOT NULL,
  `id_asiento_contable` int DEFAULT NULL,
  `periodo_nomina` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `fecha_pago` date NOT NULL,
  `dias_trabajados` int DEFAULT '0',
  `salario_diario` decimal(12,2) DEFAULT '0.00',
  `total_percepciones` decimal(12,2) DEFAULT '0.00',
  `total_deducciones` decimal(12,2) DEFAULT '0.00',
  `total_neto` decimal(12,2) DEFAULT '0.00',
  `tipo_nomina` enum('MENSUAL','QUINCENAL','SEMANAL') COLLATE utf8mb4_unicode_ci DEFAULT 'QUINCENAL',
  `estado` enum('PENDIENTE','PAGADA','ANULADA') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_nomina`),
  KEY `id_asiento_contable` (`id_asiento_contable`),
  KEY `idx_empleado` (`id_empleado`),
  KEY `idx_periodo` (`periodo_nomina`),
  KEY `idx_fecha_pago` (`fecha_pago`),
  KEY `idx_estado` (`estado`),
  CONSTRAINT `nomina_ibfk_1` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`) ON DELETE RESTRICT,
  CONSTRAINT `nomina_ibfk_2` FOREIGN KEY (`id_asiento_contable`) REFERENCES `contabilidad` (`id_asiento`) ON DELETE SET NULL,
  CONSTRAINT `chk_nomina_fechas` CHECK (((`fecha_fin` >= `fecha_inicio`) and (`fecha_pago` >= `fecha_fin`))),
  CONSTRAINT `chk_nomina_neto` CHECK ((`total_neto` = (`total_percepciones` - `total_deducciones`)))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nomina`
--

LOCK TABLES `nomina` WRITE;
/*!40000 ALTER TABLE `nomina` DISABLE KEYS */;
INSERT INTO `nomina` VALUES (1,1,NULL,'2024-10-Q1','2024-10-01','2024-10-15','2024-10-15',15,1666.67,25000.00,3500.00,21500.00,'QUINCENAL','PAGADA','2025-11-06 17:22:19'),(2,2,NULL,'2024-10-Q1','2024-10-01','2024-10-15','2024-10-15',15,1500.00,22500.00,3150.00,19350.00,'QUINCENAL','PAGADA','2025-11-06 17:22:19'),(3,3,NULL,'2024-10-Q1','2024-10-01','2024-10-15','2024-10-15',15,1400.00,21000.00,2940.00,18060.00,'QUINCENAL','PAGADA','2025-11-06 17:22:19'),(4,4,NULL,'2024-10-Q1','2024-10-01','2024-10-15','2024-10-15',15,1600.00,24000.00,3360.00,20640.00,'QUINCENAL','PAGADA','2025-11-06 17:22:19'),(5,6,NULL,'2024-10-Q1','2024-10-01','2024-10-15','2024-10-15',15,400.00,6000.00,840.00,5160.00,'QUINCENAL','PAGADA','2025-11-06 17:22:19');
/*!40000 ALTER TABLE `nomina` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `producto`
--

DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `codigo_producto` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `codigo_barras` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_producto` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `categoria_id` int DEFAULT NULL,
  `marca` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `modelo` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unidad_medida` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `precio_compra` decimal(12,2) DEFAULT '0.00',
  `precio_venta` decimal(12,2) DEFAULT '0.00',
  `margen_utilidad` decimal(5,2) DEFAULT '0.00',
  `stock_minimo` int DEFAULT '0',
  `stock_maximo` int DEFAULT '0',
  `punto_reorden` int DEFAULT '0',
  `aplica_iva` tinyint(1) DEFAULT '1',
  `tasa_iva` decimal(5,2) DEFAULT '16.00',
  `estado` enum('ACTIVO','DESCONTINUADO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_producto` (`codigo_producto`),
  KEY `idx_codigo` (`codigo_producto`),
  KEY `idx_codigo_barras` (`codigo_barras`),
  KEY `idx_nombre` (`nombre_producto`),
  KEY `idx_categoria` (`categoria_id`),
  KEY `idx_estado` (`estado`),
  CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`categoria_id`) REFERENCES `categoria` (`id_categoria`) ON DELETE SET NULL,
  CONSTRAINT `chk_precios` CHECK (((`precio_compra` >= 0) and (`precio_venta` >= 0))),
  CONSTRAINT `chk_stocks` CHECK (((`stock_minimo` >= 0) and (`stock_maximo` >= `stock_minimo`)))
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `producto`
--

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (1,'PROD-001','7501234567890','Laptop HP 15\"','Laptop HP Intel i5, 8GB RAM, 256GB SSD',1,'HP',NULL,'PIEZA',8500.00,12500.00,0.00,5,50,10,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(2,'PROD-002','7501234567891','Mouse Inalámbrico Logitech','Mouse inalámbrico ergonómico',1,'Logitech',NULL,'PIEZA',150.00,299.00,0.00,20,100,30,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(3,'PROD-003','7501234567892','Teclado Mecánico RGB','Teclado mecánico retroiluminado',1,'Redragon',NULL,'PIEZA',450.00,799.00,0.00,15,80,25,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(4,'PROD-004','7501234567893','Monitor LED 24\"','Monitor Full HD 24 pulgadas',1,'Samsung',NULL,'PIEZA',1800.00,3200.00,0.00,10,40,15,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(5,'PROD-005','7501234567894','Audífonos Bluetooth','Audífonos inalámbricos con cancelación de ruido',1,'Sony',NULL,'PIEZA',650.00,1299.00,0.00,25,100,35,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(6,'PROD-006','7501234567895','Licuadora 3 Velocidades','Licuadora de vidrio 1.5L',2,'Oster',NULL,'PIEZA',450.00,799.00,0.00,10,50,15,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(7,'PROD-007','7501234567896','Plancha de Vapor','Plancha de vapor 1200W',2,'Black & Decker',NULL,'PIEZA',280.00,499.00,0.00,15,60,20,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(8,'PROD-008','7501234567897','Juego de Toallas 3 Piezas','Toallas 100% algodón',2,'Vianney',NULL,'JUEGO',180.00,349.00,0.00,30,150,50,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(9,'PROD-009','7501234567898','Aspiradora 1600W','Aspiradora con filtro HEPA',2,'Electrolux',NULL,'PIEZA',1200.00,2199.00,0.00,8,30,12,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(10,'PROD-010','7501234567899','Aceite Vegetal 1L','Aceite vegetal comestible',3,'Nutrioli',NULL,'LITRO',35.00,59.00,0.00,100,500,150,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(11,'PROD-011','7501234567900','Arroz Blanco 1kg','Arroz blanco grano largo',3,'Verde Valle',NULL,'KILOGRAMO',18.00,32.00,0.00,150,600,200,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(12,'PROD-012','7501234567901','Frijol Negro 1kg','Frijol negro selecto',3,'Islas',NULL,'KILOGRAMO',22.00,38.00,0.00,120,500,180,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(13,'PROD-013','7501234567902','Azúcar Refinada 1kg','Azúcar refinada estándar',3,'Zulka',NULL,'KILOGRAMO',20.00,35.00,0.00,200,700,250,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(14,'PROD-014','7501234567903','Playera Algodón M','Playera 100% algodón talla M',4,'Hanes',NULL,'PIEZA',80.00,149.00,0.00,40,200,60,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(15,'PROD-015','7501234567904','Jeans Mezclilla 32','Pantalón mezclilla talla 32',4,'Levi\'s',NULL,'PIEZA',350.00,699.00,0.00,20,100,30,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(16,'PROD-016','7501234567905','Balón Fútbol No.5','Balón de fútbol profesional',5,'Nike',NULL,'PIEZA',200.00,399.00,0.00,25,100,35,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(17,'PROD-017','7501234567906','Pesas 5kg Par','Par de pesas de 5kg',5,'Sportfit',NULL,'PAR',180.00,349.00,0.00,15,60,20,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(18,'PROD-018','7501234567907','Rompecabezas 1000 Piezas','Rompecabezas paisajes',6,'Ravensburger',NULL,'PIEZA',120.00,249.00,0.00,30,120,45,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(19,'PROD-019','7501234567908','Muñeca 30cm','Muñeca articulada con accesorios',6,'Barbie',NULL,'PIEZA',180.00,349.00,0.00,25,100,35,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(20,'PROD-020','7501234567909','Martillo 16oz','Martillo de acero con mango',7,'Truper',NULL,'PIEZA',85.00,169.00,0.00,20,80,30,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(21,'PROD-021','7501234567910','Desarmador Plano 6\"','Desarmador plana 6 pulgadas',7,'Stanley',NULL,'PIEZA',35.00,69.00,0.00,40,150,60,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(22,'PROD-022','7501234567911','Resma Papel Bond','Resma papel carta 500 hojas',8,'Scribe',NULL,'RESMA',85.00,149.00,0.00,50,200,75,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(23,'PROD-023','7501234567912','Pluma Azul Caja 12','Caja de 12 plumas azules',8,'Bic',NULL,'CAJA',45.00,89.00,0.00,60,250,90,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(24,'PROD-024','7501234567913','Engrapadora Estándar','Engrapadora metálica',8,'Swingline',NULL,'PIEZA',65.00,129.00,0.00,35,120,50,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12'),(25,'PROD-025','7501234567914','Carpeta Tamaño Carta','Carpeta de 3 argollas',8,'Wilson Jones',NULL,'PIEZA',28.00,55.00,0.00,80,300,120,1,16.00,'ACTIVO','2025-11-06 17:13:12','2025-11-06 17:13:12');
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `nombre_empresa` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `rfc` varchar(13) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` text COLLATE utf8mb4_unicode_ci,
  `telefono` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacto_nombre` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contacto_telefono` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `fecha_registro` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `limite_credito` decimal(15,2) DEFAULT '0.00',
  `saldo_actual` decimal(15,2) DEFAULT '0.00',
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_proveedor`),
  KEY `idx_nombre` (`nombre_empresa`),
  KEY `idx_estado` (`estado`),
  KEY `idx_rfc` (`rfc`),
  CONSTRAINT `chk_proveedor_limite` CHECK ((`limite_credito` >= 0)),
  CONSTRAINT `chk_proveedor_saldo` CHECK ((`saldo_actual` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor`
--

LOCK TABLES `proveedor` WRITE;
/*!40000 ALTER TABLE `proveedor` DISABLE KEYS */;
INSERT INTO `proveedor` VALUES (1,'Distribuidora Tecnológica S.A.','DTE920315ABC','Parque Industrial Norte, Querétaro','5598765001','ventas@disttec.com','Carlos Méndez','5598765002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',800000.00,0.00,NULL),(2,'Importadora de Electrónicos MX','IEM880720XYZ','Zona Industrial, Guadalajara','5587654001','compras@importaelec.com','Ana López','5587654002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',1000000.00,0.00,NULL),(3,'Alimentos del Centro S.A.','ACE750510DEF','Central de Abastos, CDMX','5576543001','pedidos@alimcentro.com','Jorge Ramírez','5576543002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',500000.00,0.00,NULL),(4,'Textiles y Confecciones Norte','TCN930825GHI','Zona Industrial, Monterrey','5565432001','ventas@texnorte.com','Patricia Ruiz','5565432002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',300000.00,0.00,NULL),(5,'Artículos Deportivos Pro','ADP880615JKL','Col. Industrial, León','5554321001','contacto@deportespro.com','Miguel Ángel Soto','5554321002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',250000.00,0.00,NULL),(6,'Juguetes y Diversiones S.A.','JDS910920MNO','Parque Industrial, Puebla','5543210001','ventas@jugdiver.com','Sandra Morales','5543210002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',350000.00,0.00,NULL),(7,'Ferretería Industrial del Bajío','FIB860405PQR','Zona Industrial, Celaya','5532109001','cotizaciones@ferrebajio.com','Roberto Martínez','5532109002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',400000.00,0.00,NULL),(8,'Papelería y Oficina Total','POT920730STU','Col. Centro, CDMX','5521098001','pedidos@papoftotal.com','Luisa Fernández','5521098002','2025-11-06 17:13:12','2025-11-06 17:13:12','ACTIVO',200000.00,0.00,NULL);
/*!40000 ALTER TABLE `proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puesto`
--

DROP TABLE IF EXISTS `puesto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `puesto` (
  `id_puesto` int NOT NULL AUTO_INCREMENT,
  `codigo_puesto` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_puesto` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `nivel_jerarquico` int DEFAULT NULL,
  `salario_minimo` decimal(12,2) DEFAULT '0.00',
  `salario_maximo` decimal(12,2) DEFAULT '0.00',
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_puesto`),
  UNIQUE KEY `codigo_puesto` (`codigo_puesto`),
  KEY `idx_nombre` (`nombre_puesto`),
  KEY `idx_estado` (`estado`),
  CONSTRAINT `chk_salarios` CHECK ((`salario_maximo` >= `salario_minimo`))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puesto`
--

LOCK TABLES `puesto` WRITE;
/*!40000 ALTER TABLE `puesto` DISABLE KEYS */;
INSERT INTO `puesto` VALUES (1,'PUE-001','Director General','Máxima autoridad de la empresa',1,50000.00,100000.00,'ACTIVO','2025-11-06 17:06:08'),(2,'PUE-002','Gerente de Área','Responsable de un departamento',2,40000.00,60000.00,'ACTIVO','2025-11-06 17:06:08'),(3,'PUE-003','Supervisor','Supervisión de equipos de trabajo',3,20000.00,35000.00,'ACTIVO','2025-11-06 17:06:08'),(4,'PUE-004','Vendedor','Atención a clientes y ventas',4,10000.00,18000.00,'ACTIVO','2025-11-06 17:06:08'),(5,'PUE-005','Cajero','Operación de caja y cobros',4,9000.00,15000.00,'ACTIVO','2025-11-06 17:06:08'),(6,'PUE-006','Almacenista','Control de inventarios',4,11000.00,16000.00,'ACTIVO','2025-11-06 17:06:08'),(7,'PUE-007','Contador','Contabilidad y finanzas',3,25000.00,45000.00,'ACTIVO','2025-11-06 17:06:08'),(8,'PUE-008','Auxiliar Contable','Apoyo en contabilidad',4,12000.00,20000.00,'ACTIVO','2025-11-06 17:06:08'),(9,'PUE-009','Comprador','Gestión de compras',4,15000.00,25000.00,'ACTIVO','2025-11-06 17:06:08'),(10,'PUE-010','Chofer Repartidor','Entrega de mercancía',4,10000.00,16000.00,'ACTIVO','2025-11-06 17:06:08');
/*!40000 ALTER TABLE `puesto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ruta`
--

DROP TABLE IF EXISTS `ruta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ruta` (
  `id_ruta` int NOT NULL AUTO_INCREMENT,
  `codigo_ruta` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_ruta` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `origen` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `destino` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `distancia_km` decimal(10,2) DEFAULT '0.00',
  `tiempo_estimado_min` int DEFAULT '0',
  `costo_estimado` decimal(10,2) DEFAULT '0.00',
  `estado` enum('ACTIVA','INACTIVA') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVA',
  `referencias` text COLLATE utf8mb4_unicode_ci,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_ruta`),
  UNIQUE KEY `codigo_ruta` (`codigo_ruta`),
  KEY `idx_codigo` (`codigo_ruta`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ruta`
--

LOCK TABLES `ruta` WRITE;
/*!40000 ALTER TABLE `ruta` DISABLE KEYS */;
INSERT INTO `ruta` VALUES (1,'RUT-001','CDMX Centro - Norte','Almacén Principal CDMX','Zona Norte CDMX',15.50,45,150.00,'ACTIVA',NULL,'2025-11-06 17:13:12'),(2,'RUT-002','CDMX Centro - Sur','Almacén Principal CDMX','Zona Sur CDMX',22.30,60,200.00,'ACTIVA',NULL,'2025-11-06 17:13:12'),(3,'RUT-003','CDMX - Estado de México','Almacén Principal CDMX','Naucalpan, EdoMex',18.80,90,250.00,'ACTIVA',NULL,'2025-11-06 17:13:12'),(4,'RUT-004','CDMX - Querétaro','Almacén Principal CDMX','Querétaro Centro',220.00,180,1500.00,'ACTIVA',NULL,'2025-11-06 17:13:12'),(5,'RUT-005','CDMX - Puebla','Almacén Principal CDMX','Puebla Centro',130.00,120,900.00,'ACTIVA',NULL,'2025-11-06 17:13:12');
/*!40000 ALTER TABLE `ruta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seguimiento_envio`
--

DROP TABLE IF EXISTS `seguimiento_envio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seguimiento_envio` (
  `id_seguimiento` int NOT NULL AUTO_INCREMENT,
  `id_envio` int NOT NULL,
  `fecha_hora` datetime DEFAULT CURRENT_TIMESTAMP,
  `ubicacion_actual` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitud` decimal(10,6) DEFAULT NULL,
  `longitud` decimal(10,6) DEFAULT NULL,
  `estado_envio` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `id_usuario` int DEFAULT NULL,
  PRIMARY KEY (`id_seguimiento`),
  KEY `id_usuario` (`id_usuario`),
  KEY `idx_envio` (`id_envio`),
  KEY `idx_fecha` (`fecha_hora`),
  CONSTRAINT `seguimiento_envio_ibfk_1` FOREIGN KEY (`id_envio`) REFERENCES `envio` (`id_envio`) ON DELETE CASCADE,
  CONSTRAINT `seguimiento_envio_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seguimiento_envio`
--

LOCK TABLES `seguimiento_envio` WRITE;
/*!40000 ALTER TABLE `seguimiento_envio` DISABLE KEYS */;
INSERT INTO `seguimiento_envio` VALUES (1,1,'2024-11-01 11:00:00','Almacén Principal',NULL,NULL,'PREPARANDO','Pedido en preparación',1),(2,1,'2024-11-01 15:30:00','Centro de Distribución Norte',NULL,NULL,'EN_TRANSITO','Pedido en camino',NULL),(3,1,'2024-11-02 10:45:00','Calle Reforma 456',NULL,NULL,'ENTREGADO','Pedido entregado exitosamente',NULL),(4,2,'2024-11-02 10:00:00','Almacén Principal',NULL,NULL,'PREPARANDO','Pedido en preparación',1),(5,2,'2024-11-03 09:00:00','Centro de Distribución Sur',NULL,NULL,'EN_TRANSITO','Pedido en ruta de entrega',NULL),(6,3,'2024-11-02 17:00:00','Almacén Principal',NULL,NULL,'PREPARANDO','Pedido grande en preparación',4),(7,3,'2025-12-02 00:58:40',NULL,NULL,NULL,'ENTREGADO','Estado actualizado desde la interfaz a ENTREGADO',NULL),(8,3,'2025-12-02 00:58:44',NULL,NULL,NULL,'PREPARANDO','Estado actualizado desde la interfaz a PREPARANDO',NULL);
/*!40000 ALTER TABLE `seguimiento_envio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_comprobante`
--

DROP TABLE IF EXISTS `tipo_comprobante`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipo_comprobante` (
  `id_tipo_comprobante` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_unicode_ci,
  `requiere_serie` tinyint(1) DEFAULT '0',
  `requiere_folio` tinyint(1) DEFAULT '0',
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_tipo_comprobante`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_comprobante`
--

LOCK TABLES `tipo_comprobante` WRITE;
/*!40000 ALTER TABLE `tipo_comprobante` DISABLE KEYS */;
INSERT INTO `tipo_comprobante` VALUES (1,'FAC','Factura','Factura fiscal electrónica',1,1,'ACTIVO','2025-11-06 17:13:12'),(2,'REM','Remisión','Remisión sin valor fiscal',1,1,'ACTIVO','2025-11-06 17:13:12'),(3,'TIC','Ticket','Ticket de venta',0,1,'ACTIVO','2025-11-06 17:13:12'),(4,'NCR','Nota de Crédito','Nota de crédito',1,1,'ACTIVO','2025-11-06 17:13:12'),(5,'NDB','Nota de Débito','Nota de débito',1,1,'ACTIVO','2025-11-06 17:13:12');
/*!40000 ALTER TABLE `tipo_comprobante` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transportista`
--

DROP TABLE IF EXISTS `transportista`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `transportista` (
  `id_transportista` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `empresa` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `tipo_vehiculo` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `placa_vehiculo` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `licencia` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVO',
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_transportista`),
  KEY `idx_nombre` (`nombre`),
  KEY `idx_estado` (`estado`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transportista`
--

LOCK TABLES `transportista` WRITE;
/*!40000 ALTER TABLE `transportista` DISABLE KEYS */;
INSERT INTO `transportista` VALUES (1,'Manuel Ortiz Vega','Transportes Rápidos','5567890111','manuel.ortiz@transrap.com','Camioneta 3.5 Ton','ABC-123-X','B-12345678','ACTIVO','2025-11-06 17:13:12'),(2,'Fernando Salas Ruiz','Envíos del Norte','5578901111','fernando.salas@envinorlte.com','Van Carga','XYZ-456-Y','B-23456789','ACTIVO','2025-11-06 17:13:12'),(3,'Alberto Mendoza Cruz','Logística Integral','5589012111','alberto.mendoza@logintegral.com','Camión 5 Ton','DEF-789-Z','C-34567890','ACTIVO','2025-11-06 17:13:12'),(4,'Ricardo Flores Pérez','Distribuciones MX','5590123111','ricardo.flores@distmx.com','Camioneta Pickup','GHI-012-W','B-45678901','ACTIVO','2025-11-06 17:13:12');
/*!40000 ALTER TABLE `transportista` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `logout` timestamp NULL DEFAULT NULL,
  `cambiar_password` tinyint(1) DEFAULT '0',
  `id_empleado` int DEFAULT NULL,
  `fecha_creacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `CambiarPassword` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_email` (`email`),
  KEY `idx_activo` (`activo`),
  KEY `fk_usuario_empleado` (`id_empleado`),
  CONSTRAINT `fk_usuario_empleado` FOREIGN KEY (`id_empleado`) REFERENCES `empleado` (`id_empleado`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Haziel','Haziel2702@gmail.com','$2a$10$8Dn35Zib8u2.JW1agkBee.lsaroatXgMatvLDIGW19kI4FQTRU09e',1,'2025-12-10 04:26:20',0,1,'2025-11-06 16:59:30','2025-12-10 04:26:20',0),(2,'María González','maria.gonzalez@empresa.com','$2a$10$4T63dpr/yWf.80t6o0q7o.HkN4Ng1PmVv.Iz1x16Sy2mNm4xeS7mW',1,NULL,0,2,'2025-11-06 17:13:12','2025-11-06 23:34:01',0),(3,'Carlos Rodríguez','carlos.rodriguez@empresa.com','$2a$10$me24FPBbLYuYqVdDrMUqD.gGTtT1R12m0mbPD41JvzF9gTda.vSz6',1,'2025-12-18 04:27:07',0,3,'2025-11-06 17:13:12','2025-12-18 04:27:07',0),(4,'Ana Martínez','ana.martinez@empresa.com','$2a$10$VEknEnE0rp81RT.cGDVplOrRsErH2ALQU7yRFQ5fRY5NTRhk7sdgO',1,'2025-12-05 05:34:43',0,4,'2025-11-06 17:13:12','2025-12-05 05:34:43',0),(5,'Luis Hernández','luis.hernandez@empresa.com','$2a$10$xDgGYo48C2jqUEw1fxrkveGvOSRvgA0rcCpupj8BytqG3.TtHy3SS',1,'2025-12-18 07:06:21',0,6,'2025-11-06 17:13:12','2025-12-18 07:06:21',0),(7,'Alexa Cortes','AlexaCort@gmail.com','$2a$10$D5qqZ25WOy44f5Wmzs2J1.34ZpX31Ml8OYAGDylyFEZeFlIEPLqMO',1,NULL,0,19,'2025-12-18 10:48:59','2025-12-18 10:48:59',0),(8,'Eduardo Castro','Edu@gmail.com','$2a$10$ya8oZOJ6cwqTMi9Iw44itO0fitQlEqgBmsiGRskrWgr0aWPzker5G',1,NULL,0,20,'2025-12-19 00:05:26','2025-12-19 00:05:26',0);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_cuentas_por_cobrar`
--

DROP TABLE IF EXISTS `v_cuentas_por_cobrar`;
/*!50001 DROP VIEW IF EXISTS `v_cuentas_por_cobrar`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_cuentas_por_cobrar` AS SELECT 
 1 AS `id_cliente`,
 1 AS `nombre_razon_social`,
 1 AS `telefono`,
 1 AS `email`,
 1 AS `limite_credito`,
 1 AS `saldo_actual`,
 1 AS `ventas_pendientes`,
 1 AS `total_pendiente`,
 1 AS `venta_mas_antigua`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_cuentas_por_pagar`
--

DROP TABLE IF EXISTS `v_cuentas_por_pagar`;
/*!50001 DROP VIEW IF EXISTS `v_cuentas_por_pagar`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_cuentas_por_pagar` AS SELECT 
 1 AS `id_proveedor`,
 1 AS `nombre_empresa`,
 1 AS `telefono`,
 1 AS `email`,
 1 AS `limite_credito`,
 1 AS `saldo_actual`,
 1 AS `compras_pendientes`,
 1 AS `total_pendiente`,
 1 AS `compra_mas_antigua`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_empleados_activos`
--

DROP TABLE IF EXISTS `v_empleados_activos`;
/*!50001 DROP VIEW IF EXISTS `v_empleados_activos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_empleados_activos` AS SELECT 
 1 AS `id_empleado`,
 1 AS `codigo_empleado`,
 1 AS `nombre_completo`,
 1 AS `correo`,
 1 AS `telefono`,
 1 AS `nombre_departamento`,
 1 AS `nombre_puesto`,
 1 AS `salario_base`,
 1 AS `fecha_contratacion`,
 1 AS `antiguedad_anos`,
 1 AS `rol`,
 1 AS `estado`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_inventario_critico`
--

DROP TABLE IF EXISTS `v_inventario_critico`;
/*!50001 DROP VIEW IF EXISTS `v_inventario_critico`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_inventario_critico` AS SELECT 
 1 AS `id_producto`,
 1 AS `codigo_producto`,
 1 AS `nombre_producto`,
 1 AS `nombre_almacen`,
 1 AS `stock_actual`,
 1 AS `stock_reservado`,
 1 AS `disponible`,
 1 AS `stock_minimo`,
 1 AS `punto_reorden`,
 1 AS `precio_compra`,
 1 AS `cantidad_sugerida_compra`,
 1 AS `costo_estimado_compra`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_productos_mas_vendidos`
--

DROP TABLE IF EXISTS `v_productos_mas_vendidos`;
/*!50001 DROP VIEW IF EXISTS `v_productos_mas_vendidos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_productos_mas_vendidos` AS SELECT 
 1 AS `id_producto`,
 1 AS `codigo_producto`,
 1 AS `nombre_producto`,
 1 AS `precio_venta`,
 1 AS `num_ventas`,
 1 AS `cantidad_total_vendida`,
 1 AS `ingreso_total`,
 1 AS `precio_promedio`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_stock_productos`
--

DROP TABLE IF EXISTS `v_stock_productos`;
/*!50001 DROP VIEW IF EXISTS `v_stock_productos`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_stock_productos` AS SELECT 
 1 AS `id_producto`,
 1 AS `codigo_producto`,
 1 AS `nombre_producto`,
 1 AS `precio_venta`,
 1 AS `stock_minimo`,
 1 AS `punto_reorden`,
 1 AS `estado_producto`,
 1 AS `stock_total`,
 1 AS `reservado_total`,
 1 AS `disponible_total`,
 1 AS `estado_stock`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_ventas_hoy`
--

DROP TABLE IF EXISTS `v_ventas_hoy`;
/*!50001 DROP VIEW IF EXISTS `v_ventas_hoy`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_ventas_hoy` AS SELECT 
 1 AS `id_venta`,
 1 AS `numero_venta`,
 1 AS `fecha_venta`,
 1 AS `cliente`,
 1 AS `vendedor`,
 1 AS `subtotal`,
 1 AS `impuestos`,
 1 AS `descuento`,
 1 AS `total_venta`,
 1 AS `estado`,
 1 AS `tipo_pago`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `venta`
--

DROP TABLE IF EXISTS `venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `numero_venta` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_venta` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_cliente` int NOT NULL,
  `id_usuario` int NOT NULL,
  `subtotal` double DEFAULT NULL,
  `impuestos` double DEFAULT NULL,
  `descuento` double DEFAULT NULL,
  `total_venta` double DEFAULT NULL,
  `estado` enum('PENDIENTE','PAGADA','ANULADA') COLLATE utf8mb4_unicode_ci DEFAULT 'PENDIENTE',
  `tipo_pago` enum('EFECTIVO','TARJETA','TRANSFERENCIA','CREDITO','MIXTO') COLLATE utf8mb4_unicode_ci DEFAULT 'EFECTIVO',
  `monto_recibido` double DEFAULT NULL,
  `cambio` double DEFAULT NULL,
  `observaciones` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id_venta`),
  UNIQUE KEY `numero_venta` (`numero_venta`),
  KEY `id_usuario` (`id_usuario`),
  KEY `idx_fecha` (`fecha_venta`),
  KEY `idx_cliente` (`id_cliente`),
  KEY `idx_estado` (`estado`),
  KEY `idx_numero` (`numero_venta`),
  CONSTRAINT `venta_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT,
  CONSTRAINT `venta_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `chk_venta_monto` CHECK ((((`estado` = _utf8mb4'PAGADA') and (`monto_recibido` >= `total_venta`)) or (`estado` <> _utf8mb4'PAGADA'))),
  CONSTRAINT `chk_venta_total` CHECK ((`total_venta` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `venta`
--

LOCK TABLES `venta` WRITE;
/*!40000 ALTER TABLE `venta` DISABLE KEYS */;
INSERT INTO `venta` VALUES (1,'VEN-2024-001','2024-11-01 16:30:00',2,1,12500,2000,0,14500,'PAGADA','EFECTIVO',15000,500,NULL),(2,'VEN-2024-002','2024-11-01 17:45:00',3,4,1397,223.52,0,1620.52,'PAGADA','TARJETA',1621.68,0,NULL),(3,'VEN-2024-003','2024-11-01 20:20:00',1,4,699,111.84,0,810.84,'PAGADA','EFECTIVO',900,33.48,NULL),(4,'VEN-2024-004','2024-11-02 15:15:00',5,1,447,71.52,0,518.52,'PAGADA','TRANSFERENCIA',1447,0,NULL),(5,'VEN-2024-005','2024-11-02 22:30:00',4,4,25000,4000,1000,28000,'PENDIENTE','CREDITO',0,0,NULL),(6,'VEN-2024-006','2024-11-03 18:00:00',6,4,548,87.68,0,635.68,'PAGADA','EFECTIVO',650,14.32,NULL),(7,'VENTA-NUEVA-001','2025-11-06 17:20:38',1,1,6398,1023.68,0,7421.68,'PAGADA','EFECTIVO',8421.68,1000,NULL),(8,'VTA-1764029169973','2025-11-25 06:06:10',1,1,129,20.64,0,149.64,'PAGADA','EFECTIVO',149.64,0,NULL),(9,'VTA-1764029213314','2025-11-25 06:06:53',1,1,129,20.64,0,149.64,'PAGADA','EFECTIVO',149.64,0,NULL),(10,'VTA-1764029365314','2025-11-25 06:09:25',1,1,903,144.48,0,1047.48,'PAGADA','EFECTIVO',1047.48,0,NULL);
/*!40000 ALTER TABLE `venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Final view structure for view `v_cuentas_por_cobrar`
--

/*!50001 DROP VIEW IF EXISTS `v_cuentas_por_cobrar`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_cuentas_por_cobrar` AS select `c`.`id_cliente` AS `id_cliente`,`c`.`nombre_razon_social` AS `nombre_razon_social`,`c`.`telefono` AS `telefono`,`c`.`email` AS `email`,`c`.`limite_credito` AS `limite_credito`,`c`.`saldo_actual` AS `saldo_actual`,count(`v`.`id_venta`) AS `ventas_pendientes`,sum(`v`.`total_venta`) AS `total_pendiente`,min(`v`.`fecha_venta`) AS `venta_mas_antigua` from (`cliente` `c` join `venta` `v` on((`c`.`id_cliente` = `v`.`id_cliente`))) where ((`v`.`estado` = 'PENDIENTE') and (`v`.`tipo_pago` = 'CREDITO')) group by `c`.`id_cliente` having (`total_pendiente` > 0) order by `total_pendiente` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_cuentas_por_pagar`
--

/*!50001 DROP VIEW IF EXISTS `v_cuentas_por_pagar`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_cuentas_por_pagar` AS select `p`.`id_proveedor` AS `id_proveedor`,`p`.`nombre_empresa` AS `nombre_empresa`,`p`.`telefono` AS `telefono`,`p`.`email` AS `email`,`p`.`limite_credito` AS `limite_credito`,`p`.`saldo_actual` AS `saldo_actual`,count(`c`.`id_compra`) AS `compras_pendientes`,sum(`c`.`total_compra`) AS `total_pendiente`,min(`c`.`fecha_compra`) AS `compra_mas_antigua` from (`proveedor` `p` join `compra` `c` on((`p`.`id_proveedor` = `c`.`id_proveedor`))) where ((`c`.`estado` = 'PENDIENTE') and (`c`.`tipo_pago` = 'CREDITO')) group by `p`.`id_proveedor` having (`total_pendiente` > 0) order by `total_pendiente` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_empleados_activos`
--

/*!50001 DROP VIEW IF EXISTS `v_empleados_activos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_empleados_activos` AS select `e`.`id_empleado` AS `id_empleado`,`e`.`codigo_empleado` AS `codigo_empleado`,concat(`e`.`nombre`,' ',coalesce(`e`.`apellido`,'')) AS `nombre_completo`,`e`.`correo` AS `correo`,`e`.`telefono` AS `telefono`,`d`.`nombre_departamento` AS `nombre_departamento`,`p`.`nombre_puesto` AS `nombre_puesto`,`e`.`salario_base` AS `salario_base`,`e`.`fecha_contratacion` AS `fecha_contratacion`,timestampdiff(YEAR,`e`.`fecha_contratacion`,curdate()) AS `antiguedad_anos`,`e`.`rol` AS `rol`,`e`.`estado` AS `estado` from ((`empleado` `e` left join `departamento` `d` on((`e`.`departamento_id` = `d`.`id_departamento`))) left join `puesto` `p` on((`e`.`puesto_id` = `p`.`id_puesto`))) where (`e`.`estado` = 'ACTIVO') order by `e`.`fecha_contratacion` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_inventario_critico`
--

/*!50001 DROP VIEW IF EXISTS `v_inventario_critico`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_inventario_critico` AS select `p`.`id_producto` AS `id_producto`,`p`.`codigo_producto` AS `codigo_producto`,`p`.`nombre_producto` AS `nombre_producto`,`a`.`nombre_almacen` AS `nombre_almacen`,`i`.`stock_actual` AS `stock_actual`,`i`.`stock_reservado` AS `stock_reservado`,(`i`.`stock_actual` - `i`.`stock_reservado`) AS `disponible`,`p`.`stock_minimo` AS `stock_minimo`,`p`.`punto_reorden` AS `punto_reorden`,`p`.`precio_compra` AS `precio_compra`,(`p`.`punto_reorden` - `i`.`stock_actual`) AS `cantidad_sugerida_compra`,((`p`.`punto_reorden` - `i`.`stock_actual`) * `p`.`precio_compra`) AS `costo_estimado_compra` from ((`inventario` `i` join `producto` `p` on((`i`.`producto_id` = `p`.`id_producto`))) join `almacen` `a` on((`i`.`almacen_id` = `a`.`id_almacen`))) where ((`p`.`estado` = 'ACTIVO') and (`i`.`stock_actual` <= `p`.`punto_reorden`)) order by `i`.`stock_actual` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_productos_mas_vendidos`
--

/*!50001 DROP VIEW IF EXISTS `v_productos_mas_vendidos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_productos_mas_vendidos` AS select `p`.`id_producto` AS `id_producto`,`p`.`codigo_producto` AS `codigo_producto`,`p`.`nombre_producto` AS `nombre_producto`,`p`.`precio_venta` AS `precio_venta`,count(distinct `dv`.`id_venta`) AS `num_ventas`,sum(`dv`.`cantidad`) AS `cantidad_total_vendida`,sum(`dv`.`total`) AS `ingreso_total`,avg(`dv`.`precio_unitario`) AS `precio_promedio` from ((`detalle_venta` `dv` join `producto` `p` on((`dv`.`id_producto` = `p`.`id_producto`))) join `venta` `v` on((`dv`.`id_venta` = `v`.`id_venta`))) where ((`v`.`estado` = 'PAGADA') and (`v`.`fecha_venta` >= (curdate() - interval 30 day))) group by `p`.`id_producto` order by `cantidad_total_vendida` desc limit 20 */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_stock_productos`
--

/*!50001 DROP VIEW IF EXISTS `v_stock_productos`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_stock_productos` AS select `p`.`id_producto` AS `id_producto`,`p`.`codigo_producto` AS `codigo_producto`,`p`.`nombre_producto` AS `nombre_producto`,`p`.`precio_venta` AS `precio_venta`,`p`.`stock_minimo` AS `stock_minimo`,`p`.`punto_reorden` AS `punto_reorden`,`p`.`estado` AS `estado_producto`,coalesce(sum(`i`.`stock_actual`),0) AS `stock_total`,coalesce(sum(`i`.`stock_reservado`),0) AS `reservado_total`,coalesce(sum((`i`.`stock_actual` - `i`.`stock_reservado`)),0) AS `disponible_total`,(case when (coalesce(sum(`i`.`stock_actual`),0) = 0) then 'SIN_STOCK' when (coalesce(sum(`i`.`stock_actual`),0) <= `p`.`stock_minimo`) then 'CRÍTICO' when (coalesce(sum(`i`.`stock_actual`),0) <= `p`.`punto_reorden`) then 'BAJO' else 'NORMAL' end) AS `estado_stock` from (`producto` `p` left join `inventario` `i` on((`p`.`id_producto` = `i`.`producto_id`))) where (`p`.`estado` = 'ACTIVO') group by `p`.`id_producto` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_ventas_hoy`
--

/*!50001 DROP VIEW IF EXISTS `v_ventas_hoy`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_ventas_hoy` AS select `v`.`id_venta` AS `id_venta`,`v`.`numero_venta` AS `numero_venta`,`v`.`fecha_venta` AS `fecha_venta`,`c`.`nombre_razon_social` AS `cliente`,`u`.`nombre` AS `vendedor`,`v`.`subtotal` AS `subtotal`,`v`.`impuestos` AS `impuestos`,`v`.`descuento` AS `descuento`,`v`.`total_venta` AS `total_venta`,`v`.`estado` AS `estado`,`v`.`tipo_pago` AS `tipo_pago` from ((`venta` `v` join `cliente` `c` on((`v`.`id_cliente` = `c`.`id_cliente`))) join `usuario` `u` on((`v`.`id_usuario` = `u`.`id`))) where (cast(`v`.`fecha_venta` as date) = curdate()) order by `v`.`fecha_venta` desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-18 12:18:57
