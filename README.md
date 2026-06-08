# FutbolApp - Cliente Móvil

Aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para la gestión técnica de clubes de fútbol. Permite a entrenadores y cuerpo técnico llevar un registro detallado de jugadores, entrenamientos y evaluaciones de rendimiento en tiempo real, sincronizando los datos con un backend mediante **Retrofit** y autenticación **JWT**.

## Repositorio

Código fuente:

https://github.com/EdisonLudena/ludena-futbolapp-kotlin.git

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
<img width="464" height="667" alt="image" src="https://github.com/user-attachments/assets/e91130b0-8607-40c0-9819-a47033987dd3" />

![Dashboard](screenshots/dashboard.png)
![Jugadores](screenshots/jugadores.png)
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

### Asistencia

Control de asistencia a entrenamientos y partidos.

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
