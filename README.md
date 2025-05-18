# 🚀 Microservicio de Notificaciones (notifications-ms)

Este microservicio es responsable de gestionar las notificaciones del sistema, como el envío de correos electrónicos para recuperación de contraseñas.

## 📋 Funcionalidades Implementadas

### Recuperación de Contraseña

El microservicio implementa la funcionalidad para enviar correos electrónicos de recuperación de contraseña a los usuarios. Esta funcionalidad incluye:

1. **Recepción de solicitudes vía gRPC**: El microservicio recibe solicitudes desde el servicio de autenticación (auth-ms) mediante gRPC.
2. **Generación de correos HTML responsive**: Crea correos con diseño adaptativo para diferentes dispositivos y clientes de correo.
3. **Envío de correos vía SMTP**: Utiliza JavaMail para enviar los correos a través de servidores SMTP configurables.
4. **Confirmación de estado**: Retorna el estado del envío al servicio solicitante.

## 🏗️ Arquitectura

El microservicio sigue una arquitectura hexagonal (puertos y adaptadores) que separa claramente:

- **Dominio**: Modelos y lógica de negocio central
- **Aplicación**: Casos de uso y puertos
- **Infraestructura**: Adaptadores para tecnologías externas (gRPC, SMTP)

### Componentes Principales

#### Dominio
- `Email`: Modelo que representa un correo electrónico con destinatario, asunto y cuerpo.

#### Aplicación
- `PasswordResetNotificationUseCase`: Puerto de entrada para solicitar el envío de correos de recuperación de contraseña.
- `EmailSenderPort`: Puerto de salida para enviar correos electrónicos.
- `PasswordResetNotificationService`: Implementación del caso de uso de recuperación de contraseña.

#### Infraestructura
- `NotificationGrpcService`: Adaptador de entrada que implementa el servidor gRPC.
- `JavaMailEmailSender`: Adaptador de salida que implementa el envío de correos mediante JavaMail.
- `MailConfig`: Configuración de JavaMail con propiedades del servidor SMTP.

## 🔧 Configuración

La configuración del servicio se realiza mediante variables de entorno o valores por defecto:

```yaml
spring:
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:tu-correo@gmail.com}
    password: ${MAIL_PASSWORD:tu-password}
    properties:
      mail:
        smtp:
          auth: ${MAIL_AUTH:true}
          starttls:
            enable: ${MAIL_STARTTLS:true}
```

## 🚀 Pasos de Implementación

1. **Definición del protocolo gRPC**:
   - Se creó el archivo `notifications.proto` con la definición del servicio y mensajes.

2. **Implementación del modelo de dominio**:
   - Se definió la clase `Email` con los atributos requeridos.

3. **Creación de los puertos**:
   - `PasswordResetNotificationUseCase`: Puerto de entrada para la funcionalidad.
   - `EmailSenderPort`: Puerto de salida para el envío de correos.

4. **Implementación del servicio de aplicación**:
   - `PasswordResetNotificationService`: Implementa la lógica para la generación y envío de correos.

5. **Implementación de los adaptadores**:
   - `NotificationGrpcService`: Adaptador de entrada gRPC.
   - `JavaMailEmailSender`: Adaptador de salida para SMTP.

6. **Configuración**:
   - `MailConfig`: Configuración de JavaMail.
   - Actualización de `application.yml` con propiedades de SMTP.

## 📨 Diseño del Correo

El correo electrónico de recuperación de contraseña incluye:

- **Cabecera con branding**: Logo y título de la plataforma.
- **Cuerpo con instrucciones**: Texto explicativo sobre la recuperación de contraseña.
- **Código de verificación**: Destacado visualmente para facilitar su lectura.
- **Pie de página**: Información legal y de ayuda.

El diseño es **completamente responsive** adaptándose a diferentes dispositivos y clientes de correo.

## 🔍 Flujo de Trabajo

1. El usuario solicita restablecer su contraseña en el frontend.
2. El frontend envía la solicitud al servicio auth-ms.
3. auth-ms genera un código de verificación y lo almacena.
4. auth-ms envía una solicitud gRPC al notifications-ms.
5. notifications-ms genera el correo HTML con el código.
6. notifications-ms envía el correo mediante el adaptador SMTP.
7. notifications-ms retorna el estado del envío a auth-ms.
8. auth-ms informa al frontend del resultado.

---

> _Desarrollado para la plataforma de cursos online Uguimar._
