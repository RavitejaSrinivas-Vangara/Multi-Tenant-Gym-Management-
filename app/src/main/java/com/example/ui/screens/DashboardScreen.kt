package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.MetricCard
import com.example.components.StatusBadge
import com.example.model.*
import com.example.viewmodel.GymViewModel
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.WarningYellow
import com.example.ui.theme.PrimaryOrange
import com.example.ui.theme.SecondaryTeal

@Composable
fun DashboardScreen(
    viewModel: GymViewModel,
    onNavigateToMembers: () -> Unit,
    onNavigateToAttendance: () -> Unit
) {
    val role by viewModel.currentUserRole.collectAsState()
    val members by viewModel.members.collectAsState()
    val trainers by viewModel.trainers.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val attendance by viewModel.attendance.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val selectedBranchId by viewModel.currentBranchId.collectAsState()

    // Filter data based on branch if not Super Admin
    val isSuperAdmin = role == UserRole.SUPER_ADMIN
    val branchMembers = if (isSuperAdmin) members else members.filter { it.gymId == selectedBranchId }
    val branchTrainers = if (isSuperAdmin) trainers else trainers.filter { it.gymId == selectedBranchId }
    val branchPayments = if (isSuperAdmin) payments else payments.filter { it.gymId == selectedBranchId }
    val branchAttendance = if (isSuperAdmin) attendance else attendance.filter {
        val member = members.find { m -> m.id == it.memberId }
        member?.gymId == selectedBranchId
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("dashboard_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome greeting banner
        item {
            WelcomeBanner(viewModel)
        }

        // Adaptive metrics grid
        item {
            when (role) {
                UserRole.SUPER_ADMIN -> SuperAdminMetrics(branches, members, payments)
                UserRole.GYM_OWNER -> OwnerMetrics(branchMembers, branchTrainers, branchPayments)
                UserRole.TRAINER -> TrainerMetrics(branchMembers, branchAttendance)
                UserRole.MEMBER -> MemberMetrics(members, plans, selectedBranchId)
                else -> Unit
            }
        }

        // Primary dynamic display lists depending on active user
        when (role) {
            UserRole.SUPER_ADMIN -> {
                item {
                    SectionHeader(title = "Sovereign Multi-Tenant Scope Status", actionText = "Refresh") {}
                }
                items(branches) { branch ->
                    BranchStatusCard(branch, members.filter { it.gymId == branch.id }.size)
                }
            }
            UserRole.GYM_OWNER -> {
                item {
                    SectionHeader(
                        title = "Recent Branch Attendance", 
                        actionText = "Manage"
                    ) { onNavigateToAttendance() }
                }
                if (branchAttendance.isEmpty()) {
                    item { EmptyStateBox("No check-ins today yet. Head to Attendance to check-in!") }
                } else {
                    items(branchAttendance.take(4)) { checkIn ->
                        SimpleAttendanceCard(checkIn)
                    }
                }
            }
            UserRole.TRAINER -> {
                item {
                    SectionHeader(title = "Your Client Roster (Active)", actionText = "See Roster") {
                        onNavigateToMembers()
                    }
                }
                val activeBranchClients = branchMembers.filter { it.status == "Active" }
                if (activeBranchClients.isEmpty()) {
                    item { EmptyStateBox("No clients in your current branch segment.") }
                } else {
                    items(activeBranchClients.take(4)) { client ->
                        CompactClientCard(client)
                    }
                }
            }
            UserRole.MEMBER -> {
                item {
                    SectionHeader(title = "My Fitness Check-in Log", actionText = "History") {}
                }
                // Filter current member's attendance
                val currentMember = members.find { it.name.contains("Marcus", ignoreCase = true) }
                val myLogs = attendance.filter { it.memberId == currentMember?.id }
                
                if (myLogs.isEmpty()) {
                    item { EmptyStateBox("You haven't checked in this week. Scan barcode on entry!") }
                } else {
                    items(myLogs) { log ->
                        SimpleAttendanceCard(log)
                    }
                }
            }
            else -> Unit
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun WelcomeBanner(viewModel: GymViewModel) {
    val name by viewModel.userName.collectAsState()
    val branchId by viewModel.currentBranchId.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val currentBranchName = branches.find { it.id == branchId }?.name ?: "Headquarters"
    val role by viewModel.currentUserRole.collectAsState()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Welcome back,",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
            Text(
                text = name.ifEmpty { "Administrator" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (role == UserRole.SUPER_ADMIN) "Global Cloud Console" else currentBranchName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        TextButton(onClick = onActionClick) {
            Text(text = actionText, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
fun EmptyStateBox(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ==== METRICS ADAPTERS ====

@Composable
fun SuperAdminMetrics(branches: List<GymBranch>, members: List<Member>, payments: List<Payment>) {
    val globalRevenue = payments.sumOf { it.amount }
    val avgCheckRate = 89f // simulated stats

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Total Tenants",
                value = branches.size.toString(),
                subText = "+1 Pending",
                icon = Icons.Default.Domain,
                accentColor = SecondaryTeal,
                modifier = Modifier.weight(1f),
                isHighlighted = true
            )
            MetricCard(
                title = "Global Members",
                value = members.size.toString(),
                subText = "Dynamic Growth",
                icon = Icons.Default.Group,
                accentColor = PrimaryOrange,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Gross Revenue",
                value = "$${String.format("%.2f", globalRevenue)}",
                subText = "Consolidated",
                icon = Icons.Default.TrendingUp,
                accentColor = AccentGreen,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Partner Satis.",
                value = "98%",
                subText = "KPI Excellent",
                icon = Icons.Default.Star,
                accentColor = WarningYellow,
                modifier = Modifier.weight(1f),
                usePrimaryBg = true
            )
        }
    }
}

@Composable
fun OwnerMetrics(members: List<Member>, trainers: List<Trainer>, payments: List<Payment>) {
    val branchRevenue = payments.filter { it.status == "Paid" }.sumOf { it.amount }
    val activeCount = members.filter { it.status == "Active" }.size

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Branch Members",
                value = members.size.toString(),
                subText = "$activeCount active status",
                icon = Icons.Default.Group,
                accentColor = SecondaryTeal,
                modifier = Modifier.weight(1f),
                isHighlighted = true
            )
            MetricCard(
                title = "Staff Trainers",
                value = trainers.size.toString(),
                subText = "Fully Qualified",
                icon = Icons.Default.FitnessCenter,
                accentColor = PrimaryOrange,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Monthly Revenue",
                value = "$${String.format("%.2f", branchRevenue)}",
                subText = "Current Cycle",
                icon = Icons.Default.Payments,
                accentColor = AccentGreen,
                modifier = Modifier.weight(1f)
            )
            val avgAttendance = if (members.isEmpty()) 0f else members.map { it.attendanceRate }.average().toFloat() * 100
            MetricCard(
                title = "Avg Check-In",
                value = "${String.format("%.1f", avgAttendance)}%",
                subText = "Streak High",
                icon = Icons.Default.CalendarMonth,
                accentColor = WarningYellow,
                modifier = Modifier.weight(1f),
                usePrimaryBg = true
            )
        }
    }
}

@Composable
fun TrainerMetrics(members: List<Member>, attendance: List<AttendanceRecord>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Branch Clients",
                value = members.size.toString(),
                subText = "In assigned scope",
                icon = Icons.Default.Group,
                accentColor = SecondaryTeal,
                modifier = Modifier.weight(1f),
                isHighlighted = true
            )
            MetricCard(
                title = "Client Attendance",
                value = "${attendance.size} Present",
                subText = "Checked in today",
                icon = Icons.Default.DoneAll,
                accentColor = AccentGreen,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Your Rating",
                value = "4.9★",
                subText = "Top 5% in Region",
                icon = Icons.Default.WorkspacePremium,
                accentColor = PrimaryOrange,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Active Sessions",
                value = "8 Daily",
                subText = "No Overlap",
                icon = Icons.Default.Timer,
                accentColor = WarningYellow,
                modifier = Modifier.weight(1f),
                usePrimaryBg = true
            )
        }
    }
}

@Composable
fun MemberMetrics(members: List<Member>, plans: List<MembershipPlan>, branchId: String) {
    // Simulated current logged in member: Marcus Aurelius
    val currentMember = members.find { it.name.contains("Marcus", ignoreCase = true) } ?: members.first()
    val plan = plans.find { it.id == currentMember.planId }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Active Plan",
                value = plan?.name ?: "No active plan",
                subText = plan?.description ?: "Free",
                icon = Icons.Default.WorkspacePremium,
                accentColor = PrimaryOrange,
                modifier = Modifier.weight(1f),
                isHighlighted = true
            )
            MetricCard(
                title = "Outstanding Balance",
                value = "$${String.format("%.2f", currentMember.pendingDues)}",
                subText = if (currentMember.pendingDues > 0) "Immediate payment needed" else "Fully Cleared",
                icon = Icons.Default.CreditCard,
                accentColor = if (currentMember.pendingDues > 0) WarningYellow else AccentGreen,
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "My Attendance",
                value = "${(currentMember.attendanceRate * 100).toInt()}%",
                subText = "Sought after standard",
                icon = Icons.Default.OfflineBolt,
                accentColor = SecondaryTeal,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Simulated QR Entry",
                value = "ID: ${currentMember.id}",
                subText = "Hold to scan",
                icon = Icons.Default.QrCode,
                accentColor = AccentGreen,
                modifier = Modifier.weight(1f),
                usePrimaryBg = true
            )
        }
    }
}

// ==== AUXILIARY VIEWS ====

@Composable
fun BranchStatusCard(branch: GymBranch, numMembers: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = branch.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = branch.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(label = "Active Branch", statusType = "Active")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$numMembers Active Members",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun SimpleAttendanceCard(record: AttendanceRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsRun,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = record.memberName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${record.date} @ ${record.timeIn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            StatusBadge(label = record.status, statusType = record.status)
        }
    }
}

@Composable
fun CompactClientCard(client: Member) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = client.email,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Attn: ${(client.attendanceRate * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                StatusBadge(label = client.status, statusType = client.status)
            }
        }
    }
}
