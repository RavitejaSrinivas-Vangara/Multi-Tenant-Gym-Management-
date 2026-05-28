package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.mock.MockDataProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GymViewModel : ViewModel() {

    // Authentication and Session State
    private val _currentUserRole = MutableStateFlow<UserRole?>(null)
    val currentUserRole: StateFlow<UserRole?> = _currentUserRole.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _currentBranchId = MutableStateFlow("B1")
    val currentBranchId: StateFlow<String> = _currentBranchId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // App Preferences
    private val _isDarkMode = MutableStateFlow(true) // Start in dark theme for elite luxury look!
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Gym Database Store (State Flows for reactivity)
    private val _members = MutableStateFlow<List<Member>>(emptyList())
    val members: StateFlow<List<Member>> = _members.asStateFlow()

    private val _trainers = MutableStateFlow<List<Trainer>>(emptyList())
    val trainers: StateFlow<List<Trainer>> = _trainers.asStateFlow()

    private val _plans = MutableStateFlow<List<MembershipPlan>>(emptyList())
    val plans: StateFlow<List<MembershipPlan>> = _plans.asStateFlow()

    private val _payments = MutableStateFlow<List<Payment>>(emptyList())
    val payments: StateFlow<List<Payment>> = _payments.asStateFlow()

    private val _attendance = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendance: StateFlow<List<AttendanceRecord>> = _attendance.asStateFlow()

    private val _branches = MutableStateFlow<List<GymBranch>>(emptyList())
    val branches: StateFlow<List<GymBranch>> = _branches.asStateFlow()

    // UI Toast or State Notifications
    private val _uiNotification = MutableStateFlow<String?>(null)
    val uiNotification: StateFlow<String?> = _uiNotification.asStateFlow()

    init {
        // Hydrate data from mock source
        _members.value = MockDataProvider.members
        _trainers.value = MockDataProvider.trainers
        _plans.value = MockDataProvider.plans
        _payments.value = MockDataProvider.payments
        _attendance.value = MockDataProvider.attendance
        _branches.value = MockDataProvider.branches
    }

    fun clearNotification() {
        _uiNotification.value = null
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    // Role simulated Login
    fun login(role: UserRole, name: String, branchId: String, onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(1000) // Simulated network handshake
            _currentUserRole.value = role
            _userName.value = name
            _currentBranchId.value = branchId
            _isLoading.value = false
            _uiNotification.value = "Logged in as $name (${role.displayName})"
            onFinished()
        }
    }

    // Logout Helper
    fun logout(onFinished: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(600)
            _currentUserRole.value = null
            _userName.value = ""
            _isLoading.value = false
            _uiNotification.value = "Logged out successfully"
            onFinished()
        }
    }

    // Select Active Branch (for tenant-specific operations)
    fun setBranch(branchId: String) {
        _currentBranchId.value = branchId
        val branchName = _branches.value.find { it.id == branchId }?.name ?: branchId
        _uiNotification.value = "Switched tenant scope to $branchName"
    }

    // ==========================================
    // MEMBER ACTIONS
    // ==========================================
    fun addMember(member: Member) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(400)
            _members.update { current -> current + member }
            _isLoading.value = false
            _uiNotification.value = "Member '${member.name}' added successfully!"
        }
    }

    fun updateMember(updated: Member) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(400)
            _members.update { list ->
                list.map { if (it.id == updated.id) updated else it }
            }
            _isLoading.value = false
            _uiNotification.value = "Member profiles updated!"
        }
    }

    fun deleteMember(memberId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(300)
            val name = _members.value.find { it.id == memberId }?.name ?: "Member"
            _members.update { list -> list.filter { it.id != memberId } }
            _isLoading.value = false
            _uiNotification.value = "$name registration terminated."
        }
    }

    // ==========================================
    // TRAINER ACTIONS
    // ==========================================
    fun addTrainer(trainer: Trainer) {
        _trainers.update { current -> current + trainer }
        _uiNotification.value = "Trainer '${trainer.name}' hired successfully!"
    }

    // ==========================================
    // ATTENDANCE ACTIONS
    // ==========================================
    fun checkInMember(memberId: String, statusFlag: String = "Present") {
        val member = _members.value.find { it.id == memberId } ?: return
        val existing = _attendance.value.find { it.memberId == memberId }
        
        if (existing == null) {
            val record = AttendanceRecord(
                id = "A${System.currentTimeMillis()}",
                memberId = memberId,
                memberName = member.name,
                date = "2026-05-28", // Today in our 2026 simulation
                timeIn = "08:15 AM",
                status = statusFlag
            )
            _attendance.update { current -> current + record }
            _uiNotification.value = "${member.name} checked in!"
        } else {
            // Remove check-in list record (toggle out)
            _attendance.update { current -> current.filter { it.memberId != memberId } }
            _uiNotification.value = "${member.name} check-in canceled."
        }
    }

    // ==========================================
    // PAYMENT SETUP
    // ==========================================
    fun addPayment(payment: Payment) {
        _payments.update { current -> current + payment }
        
        // Also update any overdue / outstanding dues for that member if name matches
        _members.update { list ->
            list.map { m ->
                if (m.name.equals(payment.memberName, ignoreCase = true)) {
                    val remainingDues = (m.pendingDues - payment.amount).coerceAtLeast(0.0)
                    m.copy(pendingDues = remainingDues)
                } else {
                    m
                }
            }
        }
        _uiNotification.value = "Received $${payment.amount} from ${payment.memberName}."
    }

    // ==========================================
    // PLAN SETUP
    // ==========================================
    fun addPlan(plan: MembershipPlan) {
        _plans.update { current -> current + plan }
        _uiNotification.value = "New plan '${plan.name}' launched!"
    }
}
