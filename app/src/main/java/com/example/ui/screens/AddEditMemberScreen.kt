package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.components.GymButton
import com.example.components.GymInput
import com.example.components.GymTopAppBar
import com.example.model.Member
import com.example.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMemberScreen(
    viewModel: GymViewModel,
    memberId: String?,
    onNavigateBack: () -> Unit
) {
    val members by viewModel.members.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val currentBranchId by viewModel.currentBranchId.collectAsState()

    // Determine Mode
    val isEditMode = !memberId.isNullOrEmpty()
    val targetMember = remember(memberId, members) {
        members.find { it.id == memberId }
    }

    // Form inputs state
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var statusValue by remember { mutableStateOf("Active") }
    var planId by remember { mutableStateOf("P1") }
    var gymId by remember { mutableStateOf("B1") }
    var attendanceRate by remember { mutableStateOf(0.85f) }
    var pendingDues by remember { mutableStateOf(0.0) }

    // Dropdown menus expand indicators
    var statusExpanded by remember { mutableStateOf(false) }
    var planExpanded by remember { mutableStateOf(false) }
    var branchExpanded by remember { mutableStateOf(false) }

    // Form fields validation
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    // Load initial values if in edit mode
    LaunchedEffect(targetMember) {
        if (isEditMode && targetMember != null) {
            fullName = targetMember.name
            email = targetMember.email
            phone = targetMember.phone
            statusValue = targetMember.status
            planId = targetMember.planId
            gymId = targetMember.gymId
            attendanceRate = targetMember.attendanceRate
            pendingDues = targetMember.pendingDues
        } else {
            // Register Mode - default branch allocation matching active tenant scope
            gymId = if (currentBranchId.isNotEmpty()) currentBranchId else "B1"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Modify Profile" else "Register Member",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Go Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Full name Textfield
                GymInput(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        nameError = false
                    },
                    label = "Full Display Name",
                    placeholder = "Enter member name...",
                    icon = Icons.Default.Person,
                    isError = nameError,
                    errorMessage = "Name is required to register",
                    testTag = "member_form_name"
                )

                // 2. Email Address Textfield
                GymInput(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = false
                    },
                    label = "Email Address",
                    placeholder = "Enter email address...",
                    icon = Icons.Default.Email,
                    isError = emailError,
                    errorMessage = "Enter a valid email address",
                    testTag = "member_form_email"
                )

                // 3. Phone Number Textfield
                GymInput(
                    value = phone,
                    onValueChange = {
                        phone = it
                        phoneError = false
                    },
                    label = "Mobile Contact",
                    placeholder = "Enter contact digit line...",
                    icon = Icons.Default.Phone,
                    isError = phoneError,
                    errorMessage = "Contact phone is critical",
                    testTag = "member_form_phone"
                )

                // 4. Branch Selector (Multi-Tenant Context)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tenant Gym Branch Assorted",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = branchExpanded,
                        onExpandedChange = { branchExpanded = !branchExpanded }
                    ) {
                        val activeBranchName = branches.find { it.id == gymId }?.name ?: gymId
                        OutlinedTextField(
                            value = activeBranchName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = branchExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("member_form_branch_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = branchExpanded,
                            onDismissRequest = { branchExpanded = false }
                        ) {
                            branches.forEach { branch ->
                                DropdownMenuItem(
                                    text = { Text(branch.name) },
                                    onClick = {
                                        gymId = branch.id
                                        branchExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Subscription Plan Dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Active Subscription Tier",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = planExpanded,
                        onExpandedChange = { planExpanded = !planExpanded }
                    ) {
                        val activePlanName = plans.find { it.id == planId }?.name ?: planId
                        OutlinedTextField(
                            value = activePlanName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("member_form_plan_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = planExpanded,
                            onDismissRequest = { planExpanded = false }
                        ) {
                            plans.forEach { plan ->
                                DropdownMenuItem(
                                    text = { Text("${plan.name} ($${plan.price})") },
                                    onClick = {
                                        planId = plan.id
                                        planExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 6. Roster Status Dropdown
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Roster Active Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded }
                    ) {
                        OutlinedTextField(
                            value = statusValue,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("member_form_status_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            listOf("Active", "Inactive").forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        statusValue = status
                                        statusExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Save Button
                GymButton(
                    text = if (isEditMode) "Save Configuration" else "Finalize Registration",
                    onClick = {
                        // Check validation
                        if (fullName.isBlank()) nameError = true
                        if (email.isBlank() || !email.contains("@")) emailError = true
                        if (phone.isBlank()) phoneError = true

                        if (!nameError && !emailError && !phoneError) {
                            if (isEditMode && targetMember != null) {
                                val edited = targetMember.copy(
                                    name = fullName,
                                    email = email,
                                    phone = phone,
                                    status = statusValue,
                                    planId = planId,
                                    gymId = gymId,
                                    pendingDues = pendingDues
                                )
                                viewModel.updateMember(edited)
                            } else {
                                val newlyRegistered = Member(
                                    id = "M${System.currentTimeMillis()}",
                                    name = fullName,
                                    email = email,
                                    phone = phone,
                                    joinsDate = "2026-05-28",
                                    status = statusValue,
                                    planId = planId,
                                    gymId = gymId,
                                    attendanceRate = attendanceRate,
                                    pendingDues = 0.0
                                )
                                viewModel.addMember(newlyRegistered)
                            }
                            onNavigateBack()
                        }
                    },
                    icon = Icons.Default.Save,
                    testTag = "member_form_save_button"
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
