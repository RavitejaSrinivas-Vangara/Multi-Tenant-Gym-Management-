package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GymButton
import com.example.components.GymInput
import com.example.components.StatusBadge
import com.example.model.*
import com.example.viewmodel.GymViewModel
import com.example.ui.theme.*

// ==========================================
// 1. TRAINERS SCREEN
// ==========================================
@Composable
fun TrainersScreen(viewModel: GymViewModel) {
    val role by viewModel.currentUserRole.collectAsState()
    val trainers by viewModel.trainers.collectAsState()
    val selectedBranchId by viewModel.currentBranchId.collectAsState()

    val displayTrainers = remember(trainers, selectedBranchId, role) {
        if (role == UserRole.SUPER_ADMIN) trainers else trainers.filter { it.gymId == selectedBranchId }
    }

    var showHireDialog by remember { mutableStateOf(false) }
    var trainerName by remember { mutableStateOf("") }
    var trainerSpecialty by remember { mutableStateOf("") }
    var trainerPhone by remember { mutableStateOf("") }
    var trainerEmail by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("trainers_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Professional Coaching Staff (${displayTrainers.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (role == UserRole.GYM_OWNER || role == UserRole.SUPER_ADMIN) {
                    IconButton(
                        onClick = { showHireDialog = true },
                        modifier = Modifier.testTag("hire_trainer_icon_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Hire Staff", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (displayTrainers.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No trainers hired yet at this branch.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayTrainers) { trainer ->
                        TrainerCard(trainer)
                    }
                }
            }
        }

        if (showHireDialog) {
            AlertDialog(
                onDismissRequest = { showHireDialog = false },
                title = { Text("Hire New Trainer Profile", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        GymInput(trainerName, { trainerName = it }, "Full Name", icon = Icons.Default.Person, testTag = "add_trainer_name")
                        GymInput(trainerSpecialty, { trainerSpecialty = it }, "Coaching Specialty", icon = Icons.Default.FitnessCenter, testTag = "add_trainer_specialty")
                        GymInput(trainerEmail, { trainerEmail = it }, "Email Address", icon = Icons.Default.Email, testTag = "add_trainer_email")
                        GymInput(trainerPhone, { trainerPhone = it }, "Mobile Line", icon = Icons.Default.Phone, testTag = "add_trainer_phone")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (trainerName.isNotEmpty()) {
                                viewModel.addTrainer(
                                    Trainer(
                                        id = "T${System.currentTimeMillis()}",
                                        name = trainerName,
                                        specialty = trainerSpecialty.ifEmpty { "General Strength" },
                                        email = trainerEmail.ifEmpty { "newcoach@arena.com" },
                                        phone = trainerPhone.ifEmpty { "+1 (555) 000-0000" },
                                        gymId = if (selectedBranchId.isNotEmpty()) selectedBranchId else "B1",
                                        rating = 4.8f,
                                        activeClients = 0
                                    )
                                )
                                // Clear inputs & dismiss
                                trainerName = ""
                                trainerSpecialty = ""
                                trainerEmail = ""
                                trainerPhone = ""
                                showHireDialog = false
                            }
                        }
                    ) {
                        Text("Add Staff")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showHireDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun TrainerCard(trainer: Trainer) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Sports, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(trainer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(trainer.specialty, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
                }
                Surface(
                    color = WarningYellow.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(trainer.rating.toString(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = WarningYellow)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Clients Enrolled: ${trainer.activeClients}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = trainer.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// ==========================================
// 2. MEMBERSHIP PLANS SCREEN
// ==========================================
@Composable
fun PlansScreen(viewModel: GymViewModel) {
    val role by viewModel.currentUserRole.collectAsState()
    val plans by viewModel.plans.collectAsState()

    var showAddPlanDialog by remember { mutableStateOf(false) }
    var planName by remember { mutableStateOf("") }
    var planPrice by remember { mutableStateOf("") }
    var planDuration by remember { mutableStateOf("") }
    var planDesc by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("plans_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Subscription Plans (${plans.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (role == UserRole.GYM_OWNER || role == UserRole.SUPER_ADMIN) {
                    IconButton(onClick = { showAddPlanDialog = true }) {
                        Icon(Icons.Default.AddCircle, contentDescription = "Add Tier", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(plans) { plan ->
                    PlanCard(plan)
                }
            }
        }

        if (showAddPlanDialog) {
            AlertDialog(
                onDismissRequest = { showAddPlanDialog = false },
                title = { Text("Add Membership Plan Tier", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        GymInput(planName, { planName = it }, "Plan Name", placeholder = "E.g., Platinum Premium", testTag = "add_plan_name")
                        GymInput(planPrice, { planPrice = it }, "Price ($ / month)", placeholder = "E.g., 99.99", testTag = "add_plan_price")
                        GymInput(planDuration, { planDuration = it }, "Duration Months", placeholder = "E.g., 12", testTag = "add_plan_duration")
                        GymInput(planDesc, { planDesc = it }, "Short Descriptor", placeholder = "Access to pilates vs yoga", testTag = "add_plan_desc")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (planName.isNotEmpty() && planPrice.isNotEmpty()) {
                                viewModel.addPlan(
                                    MembershipPlan(
                                        id = "P${System.currentTimeMillis()}",
                                        name = planName,
                                        price = planPrice.toDoubleOrNull() ?: 39.99,
                                        durationMonths = planDuration.toIntOrNull() ?: 1,
                                        features = listOf("Gym entry", planDesc.ifEmpty { "Standard amenities access" }),
                                        description = planDesc.ifEmpty { "Simulated tier plan" }
                                    )
                                )
                                planName = ""
                                planPrice = ""
                                planDuration = ""
                                planDesc = ""
                                showAddPlanDialog = false
                            }
                        }
                    ) {
                        Text("Publish Plan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddPlanDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PlanCard(plan: MembershipPlan) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(plan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("${plan.durationMonths} Month Duration Scope", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("$${plan.price}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                    Text("per month", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(plan.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Included Features:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(8.dp))
            plan.features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(feature, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// ==========================================
// 3. PAYMENTS SCREEN
// ==========================================
@Composable
fun PaymentsScreen(viewModel: GymViewModel) {
    val role by viewModel.currentUserRole.collectAsState()
    val payments by viewModel.payments.collectAsState()
    val members by viewModel.members.collectAsState()
    val selectedBranchId by viewModel.currentBranchId.collectAsState()

    val displayPayments = remember(payments, selectedBranchId, role) {
        if (role == UserRole.SUPER_ADMIN) payments else payments.filter { it.gymId == selectedBranchId }
    }

    // Calculators
    val totalRevenue = displayPayments.filter { it.status == "Paid" }.sumOf { it.amount }
    val outstandingOverdue = if (role == UserRole.SUPER_ADMIN) members.sumOf { it.pendingDues } else members.filter { it.gymId == selectedBranchId }.sumOf { it.pendingDues }

    var showReceiptDialog by remember { mutableStateOf(false) }
    var payAmount by remember { mutableStateOf("") }
    var selectedMemberIdForPay by remember { mutableStateOf("") }
    var memberExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("payments_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Metrics top row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Gross Payments Recv.", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("$${String.format("%.2f", totalRevenue)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AccentGreen)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Outstanding Debits", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Text("$${String.format("%.2f", outstandingOverdue)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ErrorRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Financial Log / Audit History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (role == UserRole.GYM_OWNER || role == UserRole.SUPER_ADMIN) {
                    IconButton(onClick = { showReceiptDialog = true }) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = "Add Payment", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (displayPayments.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No payment transactions recorded.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(displayPayments) { payment ->
                        PaymentCard(payment)
                    }
                }
            }
        }

        if (showReceiptDialog) {
            val eligibleMembers = members.filter { role == UserRole.SUPER_ADMIN || it.gymId == selectedBranchId }
            AlertDialog(
                onDismissRequest = { showReceiptDialog = false },
                title = { Text("Log Cash / Bill Payment", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Dropdown member selector
                        Column {
                            Text("Select Debtor Member", style = MaterialTheme.typography.labelSmall)
                            Box {
                                val currentLabel = eligibleMembers.find { it.id == selectedMemberIdForPay }?.name ?: "Select Member..."
                                OutlinedButton(
                                    onClick = { memberExpanded = true },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(currentLabel)
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                                DropdownMenu(expanded = memberExpanded, onDismissRequest = { memberExpanded = false }) {
                                    eligibleMembers.forEach { m ->
                                        DropdownMenuItem(
                                            text = { Text("${m.name} (Due: $${m.pendingDues})") },
                                            onClick = {
                                                selectedMemberIdForPay = m.id
                                                payAmount = m.pendingDues.toString()
                                                memberExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        GymInput(payAmount, { payAmount = it }, "Amount ($ Received)", icon = Icons.Default.AttachMoney, testTag = "add_payment_price")
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val selectedMember = eligibleMembers.find { it.id == selectedMemberIdForPay }
                            if (selectedMember != null && payAmount.isNotEmpty()) {
                                viewModel.addPayment(
                                    Payment(
                                        id = "F${System.currentTimeMillis()}",
                                        memberName = selectedMember.name,
                                        planName = "Direct Credit",
                                        amount = payAmount.toDoubleOrNull() ?: 0.0,
                                        status = "Paid",
                                        date = "2026-05-28",
                                        gymId = selectedMember.gymId
                                    )
                                )
                                payAmount = ""
                                selectedMemberIdForPay = ""
                                showReceiptDialog = false
                            }
                        }
                    ) {
                        Text("Commit Payment")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReceiptDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun PaymentCard(payment: Payment) {
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
                        .size(36.dp)
                        .background(
                            color = if (payment.status == "Paid") AccentGreen.copy(alpha = 0.12f) else ErrorRed.copy(alpha = 0.12f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (payment.status == "Paid") Icons.Default.Check else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (payment.status == "Paid") AccentGreen else ErrorRed,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(payment.memberName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text("${payment.planName} • ${payment.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$${String.format("%.2f", payment.amount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                StatusBadge(label = payment.status, statusType = payment.status)
            }
        }
    }
}

// ==========================================
// 4. ATTENDANCE CHECK-IN SCREEN
// ==========================================
@Composable
fun AttendanceScreen(viewModel: GymViewModel) {
    val role by viewModel.currentUserRole.collectAsState()
    val members by viewModel.members.collectAsState()
    val attendance by viewModel.attendance.collectAsState()
    val selectedBranchId by viewModel.currentBranchId.collectAsState()

    val applicableMembers = remember(members, selectedBranchId, role) {
        if (role == UserRole.SUPER_ADMIN) members else members.filter { it.gymId == selectedBranchId }
    }

    var textSearch by remember { mutableStateOf("") }
    val filteredMembers = remember(applicableMembers, textSearch) {
        applicableMembers.filter { it.name.contains(textSearch, ignoreCase = true) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("attendance_screen")
    ) {
        Text(
            text = "Checked In Station Terminal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Quick check-in desk console setup. Tap switches to toggle membership attendance logs.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        GymInput(
            value = textSearch,
            onValueChange = { textSearch = it },
            label = "Scan / Search Member Name",
            icon = Icons.Default.QrCodeScanner,
            testTag = "attendance_search"
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredMembers.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("No matching members found.", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredMembers) { member ->
                    val isCheckedIn = attendance.any { it.memberId == member.id }
                    AttendanceTerminalRow(
                        member = member,
                        isCheckedIn = isCheckedIn,
                        onToggle = { viewModel.checkInMember(member.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceTerminalRow(member: Member, isCheckedIn: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("attendance_member_card_${member.id}"),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = if (isCheckedIn) AccentGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isCheckedIn) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (isCheckedIn) AccentGreen else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(member.name, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    Text(member.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Switch(
                checked = isCheckedIn,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("attendance_switch_${member.id}")
            )
        }
    }
}

// ==========================================
// 5. SETTINGS SCREEN
// ==========================================
@Composable
fun SettingsScreen(viewModel: GymViewModel, onLogout: () -> Unit) {
    val name by viewModel.userName.collectAsState()
    val role by viewModel.currentUserRole.collectAsState()
    val isDark by viewModel.isDarkMode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "User Preference Configurations",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Session Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(name.ifEmpty { "Registered User" }, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    role?.let {
                        Text(it.displayName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // Toggles Preferences Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Aesthetic settings",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setDarkMode(!isDark) }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DarkMode, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Dark Theme Visual Mode", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("High contrast athletic contrast layout", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(
                        checked = isDark,
                        onCheckedChange = { viewModel.setDarkMode(it) },
                        modifier = Modifier.testTag("dark_mode_switch")
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CloudSync, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Simulate Cloud Storage", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text("Disable for local cache test bench", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Switch(checked = true, onCheckedChange = {})
                }
            }
        }

        // About / System
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "System Metadata",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                SystemRow(Icons.Default.Devices, "Build Core SDK", "API level 36")
                SystemRow(Icons.Default.AccountBalance, "Cloud Host", "Local Simulated Sandboxed Client Sandbox")
                SystemRow(Icons.Default.Security, "Encryption Scope", "AES-256 Symmetric Client Layer")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Log out action button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Logout, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("End Administrator Session", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SystemRow(icon: ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}
