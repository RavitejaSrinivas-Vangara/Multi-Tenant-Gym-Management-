package com.example.model

enum class UserRole(val displayName: String) {
    SUPER_ADMIN("Super Admin"),
    GYM_OWNER("Gym Owner"),
    TRAINER("Trainer"),
    MEMBER("Member")
}

data class GymBranch(
    val id: String,
    val name: String,
    val location: String,
    val ownerId: String,
    val activeMembersCount: Int
)

data class Member(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val joinsDate: String,
    val status: String, // "Active", "Inactive"
    val planId: String,
    val gymId: String,
    val attendanceRate: Float, // scale 0 to 1
    val pendingDues: Double = 0.0
)

data class Trainer(
    val id: String,
    val name: String,
    val email: String,
    val specialty: String,
    val gymId: String,
    val rating: Float,
    val activeClients: Int,
    val phone: String
)

data class MembershipPlan(
    val id: String,
    val name: String,
    val price: Double,
    val durationMonths: Int,
    val features: List<String>,
    val description: String
)

data class Payment(
    val id: String,
    val memberName: String,
    val planName: String,
    val amount: Double,
    val status: String, // "Paid", "Pending", "Overdue"
    val date: String,
    val gymId: String
)

data class AttendanceRecord(
    val id: String,
    val memberId: String,
    val memberName: String,
    val date: String,
    val timeIn: String,
    val status: String // "Present", "Absent"
)
