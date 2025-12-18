# 📋 Guía de Despliegue - Sistema ERP

Esta guía te ayudará a preparar y desplegar el sistema ERP en un servidor de producción.

## 🔧 Requisitos Previos

- Java 17 o superior
- MySQL 8.0 o superior
- Maven 3.6+
- Servidor web (Tomcat, Nginx, Apache, etc.) - Opcional

## 📦 Configuración para Producción

### 1. Configurar Variables de Entorno

El sistema ahora usa variables de entorno para credenciales sensibles. Configura las siguientes variables antes de desplegar:

#### Linux/Mac:
```bash
export DB_URL="jdbc:mysql://tu-servidor-db:3306/erp?useSSL=true&serverTimezone=UTC"
export DB_USERNAME="tu_usuario_db"
export DB_PASSWORD="tu_password_db"
export SERVER_PORT=8080
export CORS_ORIGINS="https://tudominio.com,https://www.tudominio.com"
```

#### Windows:
```cmd
set DB_URL=jdbc:mysql://tu-servidor-db:3306/erp?useSSL=true&serverTimezone=UTC
set DB_USERNAME=tu_usuario_db
set DB_PASSWORD=tu_password_db
set SERVER_PORT=8080
set CORS_ORIGINS=https://tudominio.com,https://www.tudominio.com
```

### 2. Configurar Base de Datos

1. Crea la base de datos en MySQL:
```sql
CREATE DATABASE erp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Importa el esquema de la base de datos (si tienes un script SQL)

3. Verifica que el usuario de la base de datos tenga los permisos necesarios

### 3. Compilar el Proyecto

```bash
# Compilar con perfil de producción
mvn clean package -Pprod

# O si no tienes perfiles configurados en pom.xml:
mvn clean package -Dspring.profiles.active=prod
```

### 4. Configurar application.properties

El archivo `application-prod.properties` ya está configurado para usar variables de entorno. Solo asegúrate de que:

- Las variables de entorno estén configuradas correctamente
- El perfil activo sea `prod` al ejecutar la aplicación

### 5. Ejecutar en Producción

#### Opción A: JAR Ejecutable
```bash
java -jar -Dspring.profiles.active=prod target/erp-0.0.1-SNAPSHOT.jar
```

#### Opción B: Con Variables de Entorno
```bash
export SPRING_PROFILES_ACTIVE=prod
java -jar target/erp-0.0.1-SNAPSHOT.jar
```

#### Opción C: Servicio Systemd (Linux)
Crea un archivo `/etc/systemd/system/erp.service`:

```ini
[Unit]
Description=Sistema ERP
After=network.target mysql.service

[Service]
Type=simple
User=tu_usuario
WorkingDirectory=/ruta/a/tu/proyecto
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:mysql://localhost:3306/erp"
Environment="DB_USERNAME=root"
Environment="DB_PASSWORD=tu_password"
ExecStart=/usr/bin/java -jar /ruta/a/tu/proyecto/target/erp-0.0.1-SNAPSHOT.jar
Restart=always

[Install]
WantedBy=multi-user.target
```

Luego:
```bash
sudo systemctl daemon-reload
sudo systemctl enable erp
sudo systemctl start erp
```

## 🌐 Configuración de CORS

El sistema detecta automáticamente el ambiente y configura CORS. Para producción, asegúrate de:

1. Configurar `CORS_ORIGINS` con los dominios exactos de tu aplicación
2. No usar `*` en producción si usas credenciales (cookies/sesiones)

Ejemplo en `application-prod.properties`:
```properties
cors.allowed.origins=${CORS_ORIGINS:https://tudominio.com,https://www.tudominio.com}
```

## 🔒 Seguridad

### ✅ Cambios Implementados:

1. **Credenciales en Variables de Entorno**: Las contraseñas ya no están hardcodeadas
2. **Configuración Centralizada**: Todas las URLs se detectan automáticamente
3. **CORS Configurable**: Se adapta al ambiente (desarrollo/producción)
4. **Perfiles de Spring**: Separación clara entre dev y prod

### ⚠️ Recomendaciones Adicionales:

1. **HTTPS**: Configura SSL/TLS en producción
2. **Firewall**: Restringe acceso a puertos no necesarios
3. **Backups**: Configura backups automáticos de la base de datos
4. **Logs**: Revisa los logs regularmente
5. **Actualizaciones**: Mantén Java y MySQL actualizados

## 📝 Configuración del Frontend

El frontend ahora usa `config.js` que detecta automáticamente el ambiente:

- **Desarrollo**: Usa `http://localhost:8082/api`
- **Producción**: Usa la misma URL del servidor (`window.location.origin + '/api'`)

No necesitas cambiar nada en los archivos HTML al desplegar.

## 🚀 Despliegue en Servidores Populares

### Heroku
```bash
heroku config:set SPRING_PROFILES_ACTIVE=prod
heroku config:set DB_URL=jdbc:mysql://...
heroku config:set DB_USERNAME=...
heroku config:set DB_PASSWORD=...
git push heroku main
```

### AWS EC2
1. Sube el JAR a tu instancia EC2
2. Configura las variables de entorno
3. Ejecuta con systemd o PM2
4. Configura Security Groups para permitir tráfico HTTP/HTTPS

### DigitalOcean
Similar a AWS EC2, pero con App Platform puedes configurar variables de entorno en el panel.

## 🐛 Troubleshooting

### Error de Conexión a Base de Datos
- Verifica que MySQL esté corriendo
- Confirma las credenciales en las variables de entorno
- Revisa que el firewall permita conexiones al puerto 3306

### Error de CORS
- Verifica que `CORS_ORIGINS` incluya el dominio exacto (con http/https)
- No uses `*` si `allowCredentials` está en `true`

### Puerto ya en Uso
- Cambia `SERVER_PORT` o detén el proceso que usa el puerto
- En Linux: `sudo lsof -i :8080` para ver qué usa el puerto

## 📞 Soporte

Para más ayuda, revisa los logs de la aplicación:
```bash
tail -f logs/application.log
```

---

**Última actualización**: Diciembre 2024
**Versión**: 1.0.0
