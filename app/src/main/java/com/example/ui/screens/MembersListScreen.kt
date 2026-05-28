package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.components.GymButton
import com.example.components.GymInput
import com.example.components.StatusBadge
import com.example.model.Member
import com.example.model.UserRole
import com.example.viewmodel.GymViewModel
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryOrange
import com.example.ui.theme.SecondaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersListScreen(
    viewModel: GymViewModel,
    onNavigateToAddMember: () -> Unit,
    onNavigateToEditMember: (String) -> Unit
) {
    val role by viewModel.currentUserRole.collectAsState()
    val members by viewModel.members.collectAsState()
    val branches by viewModel.branches.collectAsState()
    val plans by viewModel.plans.collectAsState()
    val selectedBranchId by viewModel.currentBranchId.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf("All") } // "All", "Active", "Inactive"
    var selectedMemberForDetail by remember { mutableStateOf<Member?>(null) }

    // Multi-tenant filtering context
    val currentRoster = remember(members, selectedBranchId, role, searchQuery, statusFilter) {
        members.filter {
            val matchesBranch = (role == UserRole.SUPER_ADMIN) || (it.gymId == selectedBranchId)
            val matchesSearch = it.name.contains(searchQuery, ignoreCase = true) || it.email.contains(searchQuery, ignoreCase = true)
            val matchesStatus = when (statusFilter) {
                "Active" -> it.status == "Active"
                "Inactive" -> it.status == "Inactive"
                else -> true
            }
            matchesBranch && matchesSearch && matchesStatus
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("members_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Search Input Row
            GymInput(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = "Search Members",
                placeholder = "Search by name or email...",
                icon = Icons.Default.Search,
                testTag = "member_search_input"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Roster Status Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Active", "Inactive").forEach { filterLabel ->
                    val isSelected = statusFilter == filterLabel
                    FilterChip(
                        selected = isSelected,
                        onClick = { statusFilter = filterLabel },
                        label = { Text(filterLabel) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Header counters info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Matching Members (${currentRoster.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (role == UserRole.GYM_OWNER || role == UserRole.SUPER_ADMIN) {
                    TextButton(
                        onClick = onNavigateToAddMember,
                        modifier = Modifier.testTag("add_member_text_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Member")
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Members Lazy Roster list
            if (currentRoster.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.GroupOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No members found in catalog.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentRoster) { member ->
                        MemberRowCard(
                            member = member,
                            planName = plans.find { it.id == member.planId }?.name ?: "Basic",
                            branchName = branches.find { it.id == member.gymId }?.name ?: "Apex",
                            onCardClick = { selectedMemberForDetail = member }
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Member (Only for Admin/Owner)
        if (role == UserRole.GYM_OWNER || role == UserRole.SUPER_ADMIN) {
            FloatingActionButton(
                onClick = onNavigateToAddMember,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("add_member_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Member")
            }
        }

        // Interactive member profile display sheet overlay (simulating detail screen modal)
        selectedMemberForDetail?.let { member ->
            ModalBottomSheet(
                onDismissRequest = { selectedMemberForDetail = null },
                modifier = Modifier.testTag("member_profile_bottom_sheet")
            ) {
                MemberProfileSheetContent(
                    member = member,
                    plan = plans.find { it.id == member.planId },
                    branchName = branches.find { it.id == member.gymId }?.name ?: "Apex Studio",
                    role = role,
                    onEditClick = {
                        selectedMemberForDetail = null
                        onNavigateToEditMember(member.id)
                    },
                    onDeleteClick = {
                        viewModel.deleteMember(member.id)
                        selectedMemberForDetail = null
                    },
                    onCheckInTrigger = {
                        viewModel.checkInMember(member.id)
                        selectedMemberForDetail = null
                    }
                )
            }
        }
    }
}

@Composable
fun MemberRowCard(
    member: Member,
    planName: String,
    branchName: String,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("member_row_${member.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simple avatar icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${member.phone} • $planName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = branchName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                StatusBadge(label = member.status, statusType = member.status)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Attn: ${(member.attendanceRate * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MemberProfileSheetContent(
    member: Member,
    plan: com.example.model.MembershipPlan?,
    branchName: String,
    role: UserRole?,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCheckInTrigger: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(label = member.status, statusType = member.status)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Joined: ${member.joinsDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Detail list credentials
        Text(
            text = "Profile Parameters",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        DetailRow(label = "Email Address", value = member.email, icon = Icons.Default.Email)
        DetailRow(label = "Phone Number", value = member.phone, icon = Icons.Default.Phone)
        DetailRow(label = "Gym Branch (Tenant)", value = branchName, icon = Icons.Default.Business)
        DetailRow(
            label = "Subscription Tier", 
            value = "${plan?.name ?: "Basic Access"} - $${plan?.price ?: 29.99}/mo", 
            icon = Icons.Default.CardMembership
        )
        DetailRow(label = "Simulated Attendance Consistency", value = "${(member.attendanceRate * 100).toInt()}% check-in record", icon = Icons.Default.DoneAll)
        if (member.pendingDues > 0) {
            DetailRow(
                label = "Outstanding Balance DUE", 
                value = "$${String.format("%.2f", member.pendingDues)} unpaid", 
                icon = Icons.Default.Warning,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Actions grid row for editable metrics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Rapid attendance trigger
            Button(
                onClick = onCheckInTrigger,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Check")
            }

            if (role == UserRole.GYM_OWNER || role == UserRole.SUPER_ADMIN) {
                Button(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("sheet_edit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile")
                }

                Button(
                    onClick = onDeleteClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("sheet_delete_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Expel")
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
