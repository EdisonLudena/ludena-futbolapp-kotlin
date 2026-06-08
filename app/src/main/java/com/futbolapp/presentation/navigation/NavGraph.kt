// presentation/navigation/NavGraph.kt
package com.futbolapp.presentation.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.futbolapp.presentation.components.LoadingScreen
import com.futbolapp.presentation.ui.auth.LoginScreen
import com.futbolapp.presentation.ui.auth.RegisterScreen
import com.futbolapp.presentation.ui.home.HomeScreen
import com.futbolapp.presentation.ui.jugadores.JugadoresScreen
import com.futbolapp.presentation.ui.jugadores.JugadorDetalleScreen
import com.futbolapp.presentation.ui.partidos.PartidosScreen
import com.futbolapp.presentation.ui.partidos.PartidoDetalleScreen
import com.futbolapp.presentation.ui.evaluaciones.EvaluacionesScreen
import com.futbolapp.presentation.ui.evaluaciones.EvaluacionDetalleScreen
import com.futbolapp.presentation.ui.evaluaciones.CrearEvaluacionScreen
import com.futbolapp.presentation.ui.perfil.PerfilScreen
import com.futbolapp.presentation.ui.coach.CoachScaffold
import com.futbolapp.presentation.ui.coach.dashboard.DashboardScreen
import com.futbolapp.presentation.ui.coach.jugadores.JugadoresCoachScreen
import com.futbolapp.presentation.ui.coach.partidos.PartidosCoachScreen
import com.futbolapp.presentation.ui.evaluaciones.EvaluacionesCoachScreen
import com.futbolapp.presentation.viewmodel.AuthViewModel
import com.futbolapp.theme.Surface
import com.futbolapp.presentation.ui.coach.evaluaciones.EditarEvaluacionScreen

@Composable
fun NavGraph(authViewModel: AuthViewModel) {
    val isCheckingSession by authViewModel.isCheckingSession.collectAsState()

    if (isCheckingSession) {
        LoadingScreen("Iniciando FutbolApp...")
        return
    }

    NavGraphContent(authViewModel = authViewModel)
}

@Composable
private fun NavGraphContent(authViewModel: AuthViewModel) {
    val navController   = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val isCoach         by authViewModel.isCoach.collectAsState()
    val currentUser     by authViewModel.currentUser.collectAsState()

    val startDestination = remember {
        when {
            !isAuthenticated -> Screen.Login.route
            isCoach          -> Screen.CoachDashboard.route
            else             -> Screen.Home.route
        }
    }

    LaunchedEffect(isAuthenticated) {
        if (!isAuthenticated) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val showBottomBar = !isCoach && currentRoute in listOf(
        Screen.Home.route,
        Screen.Jugadores.route,
        Screen.Partidos.route,
        Screen.Evaluaciones.route,
        Screen.Perfil.route,
    )

    Scaffold(
        containerColor = Surface,
        bottomBar = {
            if (showBottomBar) BottomNavBar(navController = navController)
        },
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding),
        ) {

            // ── AUTH ────────────────────────────────────────────
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { isUserCoach ->
                        val dest = if (isUserCoach) Screen.CoachDashboard.route
                        else Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    viewModel            = authViewModel,
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = { isUserCoach ->
                        val dest = if (isUserCoach) Screen.CoachDashboard.route
                        else Screen.Home.route
                        navController.navigate(dest) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                    viewModel         = authViewModel,
                )
            }

            // ── COACH DASHBOARD ─────────────────────────────────
            composable(Screen.CoachDashboard.route) {
                if (!isCoach) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Screen.Home.route) { popUpTo(0) }
                    }
                    return@composable
                }
                CoachScaffold(
                    currentRoute = Screen.CoachDashboard.route,
                    user         = currentUser,
                    title        = "Dashboard",
                    onNavClick   = { route ->
                        navController.navigate(route) {
                            launchSingleTop = true
                            restoreState    = true
                        }
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                ) { padding ->
                    Box(modifier = Modifier.padding(padding)) {
                        DashboardScreen(onNavigate = { route -> navController.navigate(route) })
                    }
                }
            }

            // ── HOME ────────────────────────────────────────────
            composable(Screen.Home.route) {
                HomeScreen(
                    isCoach           = isCoach, // Pasamos el booleano que ya tienes
                    onVerJugadores    = { navController.navigate(Screen.Jugadores.route) },
                    onVerPartidos     = { navController.navigate(Screen.Partidos.route) },
                    onVerEvaluaciones = { navController.navigate(Screen.Evaluaciones.route) },
                    onIrAlDashboard   = {
                        navController.navigate(Screen.CoachDashboard.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            // ── JUGADORES ───────────────────────────────────────
            composable(Screen.Jugadores.route) {
                if (isCoach) {
                    CoachScaffold(
                        currentRoute = Screen.Jugadores.route,
                        user         = currentUser,
                        title        = "Jugadores",
                        onNavClick   = { route -> navController.navigate(route) { launchSingleTop = true } },
                        onLogout     = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                        },
                    ) { padding ->
                        Box(Modifier.padding(padding)) {
                            // AQUÍ ES DONDE ESTABA EL ERROR:
                            // Debes pasarle la misma lógica de navegación que al bloque 'else'
                            JugadoresCoachScreen(
                                onJugadorClick = { id ->
                                    navController.navigate(Screen.JugadorDetalle(id).createRoute(id))
                                }
                            )
                        }
                    }
                } else {
                    JugadoresScreen(
                        onJugadorClick = { id ->
                            navController.navigate(Screen.JugadorDetalle(id).createRoute(id))
                        },
                    )
                }
            }

            composable(
                route     = Screen.JugadorDetalle().route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                JugadorDetalleScreen(
                    jugadorId = id,
                    onBack    = { navController.popBackStack() },
                    isCoach   = isCoach,
                )
            }

            // ── PARTIDOS ────────────────────────────────────────
            // ── PARTIDOS ────────────────────────────────────────
            composable(Screen.Partidos.route) {
                if (isCoach) {
                    CoachScaffold(
                        currentRoute = Screen.Partidos.route,
                        user         = currentUser,
                        title        = "Partidos",
                        onNavClick   = { route -> navController.navigate(route) { launchSingleTop = true } },
                        onLogout     = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                        },
                    ) { padding ->
                        Box(Modifier.padding(padding)) {
                            // AQUÍ PASAMOS LA LÓGICA DE NAVEGACIÓN A LA PANTALLA DE COACH
                            PartidosCoachScreen(
                                onNavigateToDetail = { id ->
                                    navController.navigate(Screen.PartidoDetalle(id).createRoute(id))
                                }
                            )
                        }
                    }
                } else {
                    PartidosScreen(
                        onPartidoClick = { id ->
                            navController.navigate(Screen.PartidoDetalle(id).createRoute(id))
                        },
                    )
                }
            }

            composable(
                route     = Screen.PartidoDetalle().route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                PartidoDetalleScreen(
                    partidoId = id,
                    onBack    = { navController.popBackStack() },
                    isCoach   = isCoach,
                )
            }

            // ── EVALUACIONES ────────────────────────────────────
            composable(Screen.Evaluaciones.route) {
                if (isCoach) {
                    CoachScaffold(
                        currentRoute = Screen.Evaluaciones.route,
                        user         = currentUser,
                        title        = "Evaluaciones",
                        onNavClick   = { route -> navController.navigate(route) { launchSingleTop = true } },
                        onLogout     = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                        },
                    ) { padding ->
                        Box(Modifier.padding(padding)) {
                            EvaluacionesCoachScreen(
                                onCrearClick      = { navController.navigate(Screen.CrearEvaluacion.route) },
                                onEditClick       = { id ->
                                    // Aquí navegas a la pantalla de edición pasando el ID
                                    navController.navigate(Screen.EditarEvaluacion(id).createRoute(id))
                                },
                                onEvaluacionClick = { id ->
                                    navController.navigate(Screen.EvaluacionDetalle(id).createRoute(id))
                                },
                            )
                        }
                    }
                } else {
                    EvaluacionesScreen(
                        onEvaluacionClick = { id ->
                            navController.navigate(Screen.EvaluacionDetalle(id).createRoute(id))
                        },
                        onCrearClick = {},
                        isCoach      = false,
                    )
                }
            }

            composable(Screen.CrearEvaluacion.route) {
                CrearEvaluacionScreen(
                    onBack   = { navController.popBackStack() },
                    onCreado = { navController.popBackStack() },
                )
            }

            composable(
                route     = Screen.EvaluacionDetalle().route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                EvaluacionDetalleScreen(
                    evaluacionId = id,
                    onBack       = { navController.popBackStack() },
                    isCoach      = isCoach,
                )
            }

            // ── PERFIL ──────────────────────────────────────────
            composable(Screen.Perfil.route) {
                if (isCoach) {
                    CoachScaffold(
                        currentRoute = Screen.Perfil.route,
                        user         = currentUser,
                        title        = "Mi perfil",
                        onNavClick   = { route -> navController.navigate(route) { launchSingleTop = true } },
                        onLogout     = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                        },
                    ) { padding ->
                        Box(Modifier.padding(padding)) {
                            PerfilScreen(
                                authViewModel = authViewModel,
                                onLogout      = {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                            )
                        }
                    }
                } else {
                    PerfilScreen(
                        authViewModel = authViewModel,
                        onLogout      = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                    )
                }
            }

            composable(
                route     = Screen.EditarEvaluacion().route,
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id") ?: return@composable
                // Aquí invocas a tu pantalla de edición
                EditarEvaluacionScreen(
                    evaluacionId = id,
                    onBack = { navController.popBackStack() },
                    onEditado = { navController.popBackStack() }
                )
            }
        }
    }
}