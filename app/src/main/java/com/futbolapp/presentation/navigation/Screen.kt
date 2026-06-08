// presentation/navigation/Screen.kt
package com.futbolapp.presentation.navigation

sealed class Screen(val route: String) {
    // Auth
    data object Login    : Screen("login")
    data object Register : Screen("register")

    // Principal (todos los usuarios)
    data object Home     : Screen("home")

    // Jugadores
    data object Jugadores : Screen("jugadores")
    data class  JugadorDetalle(val id: Int = 0) : Screen("jugadores/{id}") {
        fun createRoute(id: Int) = "jugadores/$id"
    }

    // Partidos
    data object Partidos : Screen("partidos")
    data class  PartidoDetalle(val id: Int = 0) : Screen("partidos/{id}") {
        fun createRoute(id: Int) = "partidos/$id"
    }

    // Evaluaciones
    data object Evaluaciones : Screen("evaluaciones")
    data object CrearEvaluacion : Screen("evaluaciones/crear")
    data class  EvaluacionDetalle(val id: Int = 0) : Screen("evaluaciones/{id}") {
        fun createRoute(id: Int) = "evaluaciones/$id"
    }
    data class EditarEvaluacion(val id: Int = 0) : Screen("evaluaciones/editar/{id}") {
        fun createRoute(id: Int) = "evaluaciones/editar/$id"
    }


    // Perfil
    data object Perfil : Screen("perfil")
    data object CoachDashboard : Screen("coach/dashboard")
}