# FutbolApp - Cliente Móvil

Aplicación móvil desarrollada en **Kotlin** con **Jetpack Compose**, diseñada para la gestión técnica de clubes de fútbol. Permite a entrenadores y cuerpo técnico llevar un registro detallado de sus jugadores, entrenamientos y evaluaciones de rendimiento en tiempo real, sincronizando los datos con un backend robusto mediante **Retrofit** y autenticación **JWT**.

## Repositorio
Puedes encontrar el código fuente aquí: [https://github.com/EdisonLudena/ludena-futbolapp-kotlin.git](https://github.com/EdisonLudena/ludena-futbolapp-kotlin.git)

---

## 1. Requisitos de Instalación
* **Entorno de desarrollo:** Android Studio Iguana o superior.
* **Lenguaje:** Kotlin 1.9+.
* **SDK Mínimo:** Android 8.0 (API 26).
* **Dependencias principales:** Jetpack Compose, Hilt (Inyección de dependencias), Retrofit (Consumo de API), Coil (Carga de imágenes).

---

## 2. Configuración de la URL Base
La URL se gestiona centralizada para facilitar el cambio entre entorno de desarrollo y producción.

1. Navegar a: `app/src/main/java/com/futbolapp/data/remote/Constants.kt` (o donde hayas definido tus constantes).
2. Modificar la variable `BASE_URL`:
   ```kotlin
   const val BASE_URL = "[http://10.0.2.2:8000/api/](http://10.0.2.2:8000/api/)" // 10.0.2.2 es el localhost del emulador
