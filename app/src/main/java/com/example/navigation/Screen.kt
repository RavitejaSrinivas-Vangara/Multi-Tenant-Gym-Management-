package com.example.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String) {
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")
    object Dashboard : Screen("dashboard", "Dashboard")
    object Members : Screen("members", "Members")
    object MemberAddEdit : Screen("member_add_edit?memberId={memberId}", "Member Editor") {
        fun createRoute(memberId: String? = null): String {
            return if (memberId != null) "member_add_edit?memberId=$memberId" else "member_add_edit"
        }
    }
    object Trainers : Screen("trainers", "Trainers")
    object Plans : Screen("plans", "Plans")
    object Payments : Screen("payments", "Payments")
    object Attendance : Screen("attendance", "Attendance")
    object Reports : Screen("reports", "Reports")
    object Settings : Screen("settings", "Settings")
}

data class NavigationTab(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
