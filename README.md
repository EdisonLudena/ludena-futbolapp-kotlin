# FutbolApp - Cliente Móvil

Aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para la gestión técnica de clubes de fútbol. Permite a entrenadores y cuerpo técnico llevar un registro detallado de jugadores, entrenamientos y evaluaciones de rendimiento en tiempo real, sincronizando los datos con un backend mediante **Retrofit** y autenticación **JWT**.

## Repositorio

Código fuente:

https://github.com/EdisonLudena/ludena-futbolapp-kotlin.git

## Backend API

La aplicación consume los servicios REST del backend Django a través de la siguiente URL base:

```text
https://rubdown-relearn-latter.ngrok-free.dev/api/
```

### Configuración

Actualiza la constante `BASE_URL` en tu proyecto:

```kotlin
const val BASE_URL = "https://rubdown-relearn-latter.ngrok-free.dev/api/"
```

---

## Descripción de la Aplicación

FutbolApp es una herramienta integral que centraliza la información deportiva de un club de fútbol. Facilita el seguimiento físico y técnico de los jugadores, la planificación de entrenamientos y partidos, así como el análisis de métricas de rendimiento para apoyar la toma de decisiones del cuerpo técnico.

---

## Requisitos de Instalación

* **Android Studio:** Iguana o superior.
* **Lenguaje:** Kotlin 1.9+
* **SDK mínimo:** Android 8.0 (API 26)
* **Dependencias principales:**

  * Jetpack Compose
  * Hilt (Inyección de Dependencias)
  * Retrofit (Consumo de APIs REST)
  * Coil (Carga de imágenes)

---

## Configuración de la URL Base

Ubica el archivo de constantes de la aplicación (por ejemplo, `Constants.kt`) y configura la URL de tu backend:

```kotlin
// Emulador Android Studio
const val BASE_URL = "http://10.0.2.2:8000/api/"

// Dispositivo físico
// const val BASE_URL = "http://TU_IP_LOCAL:8000/api/"
```

> **Nota:** `10.0.2.2` permite acceder al localhost de tu máquina desde el emulador Android.

---

## Usuario de Prueba

Para entornos de desarrollo puedes utilizar las siguientes credenciales:

**Usuario:** `coach`

**Contraseña:** `coach123`

**Usuario:** `scout`

**Contraseña:** `scout123`

---

## Capturas de Pantalla

Agrega aquí las capturas de la aplicación:

```markdown
<img width="335" height="455" alt="image" src="https://github.com/user-attachments/assets/0455cc9d-f181-4fa7-a872-11530b6ae4de" />
<img width="192" height="339" alt="image" src="https://github.com/user-attachments/assets/52f8449f-7133-4fc9-8d4e-be5e81a5b483" />
<img width="191" height="341" alt="image" src="https://github.com/user-attachments/assets/c6e04741-68a1-444f-8c92-7c7c3d760ec0" />
<img width="259" height="412" alt="image" src="https://github.com/user-attachments/assets/e693e8da-70de-4c40-9774-35a2fcf1e992" />
<img width="296" height="415" alt="image" src="https://github.com/user-attachments/assets/72547704-fab9-4b45-8301-58ce9366428c" />
<img width="299" height="418" alt="image" src="https://github.com/user-attachments/assets/606023c9-79cb-4e22-b9d3-e2bb926f2cd9" />
<img width="259" height="421" alt="image" src="https://github.com/user-attachments/assets/f0ca3d27-789a-448d-9b2b-df79834c5a33" />
<img width="293" height="232" alt="image" src="https://github.com/user-attachments/assets/81836e2d-6494-4985-ba95-e277f73cbb8b" />
<img width="204" height="230" alt="image" src="https://github.com/user-attachments/assets/87c7ea7c-83ff-498d-a85f-977829a6590c" />
<img width="186" height="232" alt="image" src="https://github.com/user-attachments/assets/2370c079-0aa2-4cd0-9ed7-7f259e8b3806" />
<img width="329" height="346" alt="image" src="https://github.com/user-attachments/assets/be4f1619-ef70-4cd1-866f-2e55b25d8a76" />
<img width="256" height="346" alt="image" src="https://github.com/user-attachments/assets/ec250c65-1940-434c-a442-aeba496a06da" />
<img width="186" height="351" alt="image" src="https://github.com/user-attachments/assets/97164b97-ab2c-4ef6-bfda-6d2731ec34ff" />
<img width="163" height="322" alt="image" src="https://github.com/user-attachments/assets/929cb8b9-d853-40cb-a4f7-8cffc05b51c4" />
<img width="199" height="330" alt="image" src="https://github.com/user-attachments/assets/787aa0bb-f2bb-4e9b-80ee-e3c9fb95014a" />
<img width="338" height="331" alt="image" src="https://github.com/user-attachments/assets/9c767a1a-7813-4002-833c-8ee825f1d850" />
<img width="307" height="331" alt="image" src="https://github.com/user-attachments/assets/7840ff1e-5906-4739-a3f8-1acc46133546" />
<img width="311" height="218" alt="image" src="https://github.com/user-attachments/assets/3a4572e9-5944-4707-b4d4-4f9724b60929" />
<img width="448" height="219" alt="image" src="https://github.com/user-attachments/assets/8fdaf9c6-254e-4065-a3b5-625b20d62284" />
<img width="414" height="292" alt="image" src="https://github.com/user-attachments/assets/2cc0869b-bfef-4c76-b070-515193e39c84" />
<img width="294" height="640" alt="image" src="https://github.com/user-attachments/assets/0ffdda47-a0f6-40cf-b0c1-30c31f92a6fb" />
<img width="464" height="667" alt="image" src="https://github.com/user-attachments/assets/dc44b8fa-eab7-4564-b1a5-15a6019c69e7" />




```

---

## Entidades Implementadas

### User

Gestión de usuarios y roles del sistema:

* Coach
* Scout
* Administrador

### Jugador

Información personal, posición y asociación con equipos.

### Equipo

Organización de planteles y categorías deportivas.

### Entrenamiento

Registro de sesiones técnicas, tácticas y objetivos.

### Partido

Gestión del calendario, rivales y resultados.

### Evaluación de Rendimiento

Seguimiento de métricas físicas y técnicas:

* Peso
* Altura
* Velocidad
* Calificaciones


---

## Pantallas Implementadas

### AuthScreen

Inicio de sesión y gestión de autenticación JWT.

### HomeScreen

Dashboard principal con estadísticas y accesos rápidos.

### JugadoresListScreen

Listado y búsqueda de jugadores.

### JugadorDetalleScreen

Ficha técnica y métricas históricas del jugador.

### EvaluacionesScreen

Registro y consulta de evaluaciones deportivas.

### PartidosScreen

Calendario de encuentros y resultados.

### PerfilScreen

Configuración del usuario y cierre de sesión.

---

## Ejemplo de Consumo de API

La aplicación utiliza Retrofit junto con autenticación JWT.

```kotlin
@GET("evaluaciones/")
suspend fun getEvaluaciones(
    @Header("Authorization") token: String
): Response<List<EvaluacionDto>>
```

Además, la app implementa un interceptor para adjuntar automáticamente el token de autenticación a cada solicitud.

---

## Arquitectura y Tecnologías

* Kotlin
* Jetpack Compose
* MVVM
* Retrofit
* Hilt
* Coroutines
* JWT Authentication
* Coil

---

## Instrucciones de Ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/EdisonLudena/ludena-futbolapp-kotlin.git
```

### 2. Abrir en Android Studio

Importa el proyecto como proyecto Gradle.

### 3. Configurar Backend

Asegúrate de tener el servidor Django ejecutándose y la URL base correctamente configurada.

### 4. Ejecutar la Aplicación

1. Sincroniza las dependencias Gradle.
2. Selecciona un dispositivo físico o emulador.
3. Presiona **Run**.

---

## Autor

**Edison Ludena**

Proyecto desarrollado para la gestión y seguimiento del rendimiento deportivo en clubes de fútbol.
