package com.example.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.components.GymTopAppBar
import com.example.model.UserRole
import com.example.ui.screens.*
import com.example.viewmodel.GymViewModel

@Composable
fun GymAppNavigation(
    viewModel: GymViewModel,
    navController: NavHostController = rememberNavController()
) {
    val role by viewModel.currentUserRole.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val currentBranchId by viewModel.currentBranchId.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide layouts on entry portals
    val showScaffold = currentRoute != Screen.Splash.route && currentRoute != Screen.Login.route

    // Dynamic Bottom Menu Navigation depending on role
    val tabs = remember(role) {
        when (role) {
            UserRole.SUPER_ADMIN -> listOf(
                NavigationTab(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
                NavigationTab(Screen.Members, "Members", Icons.Default.Group, Icons.Outlined.Group),
                NavigationTab(Screen.Payments, "Payments", Icons.Default.AttachMoney, Icons.Outlined.AttachMoney),
                NavigationTab(Screen.Reports, "Reports", Icons.Default.Assessment, Icons.Outlined.Assessment),
                NavigationTab(Screen.Settings, "Settings", Icons.Default.Settings, Icons.Outlined.Settings)
            )
            UserRole.GYM_OWNER -> listOf(
                NavigationTab(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
                NavigationTab(Screen.Members, "Members", Icons.Default.Group, Icons.Outlined.Group),
                NavigationTab(Screen.Trainers, "Trainers", Icons.Default.Sports, Icons.Outlined.Sports),
                NavigationTab(Screen.Payments, "Payments", Icons.Default.Payments, Icons.Outlined.Payments),
                NavigationTab(Screen.Settings, "Settings", Icons.Default.Settings, Icons.Outlined.Settings)
            )
            UserRole.TRAINER -> listOf(
                NavigationTab(Screen.Dashboard, "Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
                NavigationTab(Screen.Members, "Clients", Icons.Default.Group, Icons.Outlined.Group),
                NavigationTab(Screen.Attendance, "Check-In", Icons.Default.QrCodeScanner, Icons.Outlined.QrCodeScanner),
                NavigationTab(Screen.Settings, "Settings", Icons.Default.Settings, Icons.Outlined.Settings)
            )
            UserRole.MEMBER -> listOf(
                NavigationTab(Screen.Dashboard, "Home", Icons.Default.Dashboard, Icons.Outlined.Dashboard),
                NavigationTab(Screen.Plans, "Plans", Icons.Default.CardMembership, Icons.Outlined.CardMembership),
                NavigationTab(Screen.Attendance, "My Logs", Icons.Default.History, Icons.Outlined.History),
                NavigationTab(Screen.Settings, "Settings", Icons.Default.Settings, Icons.Outlined.Settings)
            )
            else -> emptyList()
        }
    }

    val appTitle = remember(currentRoute) {
        when {
            currentRoute?.startsWith(Screen.Dashboard.route) == true -> "Command Center"
            currentRoute?.startsWith(Screen.Members.route) == true -> "Member Database"
            currentRoute?.startsWith("member_add_edit") == true -> "Profile Configuration"
            currentRoute?.startsWith(Screen.Trainers.route) == true -> "Trainers Workspace"
            currentRoute?.startsWith(Screen.Plans.route) == true -> "Plans Catalogue"
            currentRoute?.startsWith(Screen.Payments.route) == true -> "Financial Registry"
            currentRoute?.startsWith(Screen.Attendance.route) == true -> "Attendance Check-In"
            currentRoute?.startsWith(Screen.Reports.route) == true -> "Aggregated Summary Reports"
            currentRoute?.startsWith(Screen.Settings.route) == true -> "Console Settings"
            else -> "APEX ARENA"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showScaffold) {
                GymTopAppBar(
                    title = appTitle,
                    role = role,
                    branches = branches,
                    selectedBranchId = currentBranchId,
                    onBranchSelected = { viewModel.setBranch(it) },
                    onLogoutClick = {
                        viewModel.logout {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0)
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (showScaffold && tabs.isNotEmpty()) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    tabs.forEach { tab ->
                        val isSelected = currentRoute == tab.screen.route
                        NavigationBarItem(
                            selected = isSelected,
                            label = { Text(tab.label) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.label
                                )
                            },
                            onClick = {
                                if (currentRoute != tab.screen.route) {
                                    navController.navigate(tab.screen.route) {
                                        popUpTo(Screen.Dashboard.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            modifier = Modifier.testTag("nav_item_${tab.screen.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            }

            composable(Screen.Login.route) {
                LoginScreen(viewModel) {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            composable(Screen.Dashboard.route) {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToMembers = { navController.navigate(Screen.Members.route) },
                    onNavigateToAttendance = { navController.navigate(Screen.Attendance.route) }
                )
            }

            composable(Screen.Members.route) {
                MembersListScreen(
                    viewModel = viewModel,
                    onNavigateToAddMember = { navController.navigate(Screen.MemberAddEdit.createRoute()) },
                    onNavigateToEditMember = { id -> navController.navigate(Screen.MemberAddEdit.createRoute(id)) }
                )
            }

            composable(
                route = Screen.MemberAddEdit.route,
                arguments = listOf(
                    navArgument("memberId") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val mId = backStackEntry.arguments?.getString("memberId")
                AddEditMemberScreen(viewModel = viewModel, memberId = mId) {
                    navController.popBackStack()
                }
            }

            composable(Screen.Trainers.route) {
                TrainersScreen(viewModel)
            }

            composable(Screen.Plans.route) {
                PlansScreen(viewModel)
            }

            composable(Screen.Payments.route) {
                PaymentsScreen(viewModel)
            }

            composable(Screen.Attendance.route) {
                AttendanceScreen(viewModel)
            }

            composable(Screen.Reports.route) {
                ReportsScreen(viewModel)
            }

            composable(Screen.Settings.route) {
                SettingsScreen(viewModel) {
                    viewModel.logout {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
