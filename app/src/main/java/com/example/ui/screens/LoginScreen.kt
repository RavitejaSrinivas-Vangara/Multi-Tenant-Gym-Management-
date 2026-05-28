package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GymButton
import com.example.components.GymInput
import com.example.components.LoadingOverlay
import com.example.model.UserRole
import com.example.viewmodel.GymViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: GymViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val branches by viewModel.branches.collectAsState()

    var selectedRole by remember { mutableStateOf(UserRole.GYM_OWNER) }
    var selectedBranchId by remember { mutableStateOf("B1") }
    var inputName by remember { mutableStateOf("James Gold") }
    var inputCode by remember { mutableStateOf("TENANT-1092") }

    var isRoleExpanded by remember { mutableStateOf(false) }
    var isBranchExpanded by remember { mutableStateOf(false) }

    // Auto update name options based on role selection for realistic simulation
    LaunchedEffect(selectedRole) {
        when (selectedRole) {
            UserRole.SUPER_ADMIN -> {
                inputName = "Lord Administrator"
                inputCode = "GLOBAL-99"
            }
            UserRole.GYM_OWNER -> {
                inputName = "James Gold"
                inputCode = "TENANT-101"
            }
            UserRole.TRAINER -> {
                inputName = "Arnold Strong"
                inputCode = "TRAIN-Arnold"
            }
            UserRole.MEMBER -> {
                inputName = "Marcus Aurelius"
                inputCode = "MEM-Marcus"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Gym branding halo header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Apex Arena Portal",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Simulated Unified Multi-Tenant Management",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Sign In Workspace",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 1. Selector Dropdown for Role Selection
                    Text(
                        text = "Select Persona / Role",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = isRoleExpanded,
                        onExpandedChange = { isRoleExpanded = !isRoleExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedRole.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("role_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = isRoleExpanded,
                            onDismissRequest = { isRoleExpanded = false }
                        ) {
                            UserRole.values().forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(role.displayName) },
                                    onClick = {
                                        selectedRole = role
                                        isRoleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 2. Tenant Gym Selection (Only applicable to non-Super Admin roles)
                    if (selectedRole != UserRole.SUPER_ADMIN) {
                        Text(
                            text = "Branch Scope / Tenant",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = isBranchExpanded,
                            onExpandedChange = { isBranchExpanded = !isBranchExpanded }
                        ) {
                            val activeBranchName = branches.find { it.id == selectedBranchId }?.name ?: selectedBranchId
                            OutlinedTextField(
                                value = activeBranchName,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isBranchExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("tenant_dropdown"),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = isBranchExpanded,
                                onDismissRequest = { isBranchExpanded = false }
                            ) {
                                branches.forEach { branch ->
                                    DropdownMenuItem(
                                        text = { Text(branch.name) },
                                        onClick = {
                                            selectedBranchId = branch.id
                                            isBranchExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. Simulated Full Name Textfield
                    GymInput(
                        value = inputName,
                        onValueChange = { inputName = it },
                        label = "Display Username",
                        placeholder = "E.g., Coach Arnold",
                        icon = Icons.Default.Person,
                        testTag = "username_input"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4. Simulated Tenant/Manager Credentials (Passcode)
                    GymInput(
                        value = inputCode,
                        onValueChange = { inputCode = it },
                        label = "Tenant Code / Passkey",
                        placeholder = "Enter 4 digit branch key",
                        icon = Icons.Default.Lock,
                        testTag = "passkey_input"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GymButton(
                        text = "Access Console",
                        onClick = {
                            viewModel.login(
                                role = selectedRole,
                                name = inputName,
                                branchId = if (selectedRole == UserRole.SUPER_ADMIN) "" else selectedBranchId,
                                onFinished = onNavigateToDashboard
                            )
                        },
                        icon = Icons.Default.Login,
                        testTag = "login_button"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Apex simulated services run entirely on Client VM state.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        LoadingOverlay(isLoading = isLoading)
    }
}
