package com.example.mock

import com.example.model.*

object MockDataProvider {
    val branches = listOf(
        GymBranch("B1", "Iron Arena Gym", "Brooklyn, NY", "Owner1", 145),
        GymBranch("B2", "Apex Peak Fitness", "Los Angeles, CA", "Owner1", 112),
        GymBranch("B3", "Titan Forge Gym", "Chicago, IL", "Owner2", 84)
    )

    val plans = listOf(
        MembershipPlan("P1", "Elite Access", 79.99, 12, listOf("All locations", "24/7 access", "1x Trainer consultation", "Spa & Sauna acces"), "Full featured yearly membership"),
        MembershipPlan("P2", "Power Builder", 49.99, 6, listOf("Single location", "Standard hours", "Free locker"), "Great for lifters seeking solid standard access"),
        MembershipPlan("P3", "Basic Active", 29.99, 1, listOf("Single location", "Limited hours (9am - 4pm)"), "Budget-friendly plan for local regulars")
    )

    val members = listOf(
        Member("M1", "Marcus Aurelius", "marcus@rome.org", "+1 (555) 019-2831", "2025-01-10", "Active", "P1", "B1", 0.94f, 0.0),
        Member("M2", "Seneca Younger", "seneca@stoic.com", "+1 (555) 021-3941", "2025-02-14", "Active", "P1", "B1", 0.88f, 0.0),
        Member("M3", "Epictetus Disciple", "epictetus@freedom.net", "+1 (555) 038-7261", "2025-03-01", "Active", "P2", "B2", 0.72f, 49.99),
        Member("M4", "Alexander Great", "alex@macedon.io", "+1 (555) 443-8822", "2024-11-15", "Active", "P1", "B2", 0.98f, 0.0),
        Member("M5", "Cleopatra Queen", "cleo@alexandrian.eg", "+1 (555) 777-1234", "2025-04-20", "Active", "P2", "B1", 0.82f, 0.0),
        Member("M6", "Diogenes Barrel", "diogenes@cynic.org", "+1 (555) 993-4567", "2025-05-12", "Inactive", "P3", "B3", 0.15f, 29.99),
        Member("M7", "Hypatia Math", "hypatia@museum.edu", "+1 (555) 881-2244", "2025-05-01", "Active", "P3", "B3", 0.91f, 0.0),
        Member("M8", "Leonidas Sparta", "leonidas@300.gr", "+1 (555) 300-3000", "2024-09-10", "Active", "P1", "B2", 0.99f, 0.0)
    )

    val trainers = listOf(
        Trainer("T1", "Arnold Strong", "arnold@iron.com", "Hypertrophy & Contest Prep", "B1", 4.9f, 15, "+1 (555) 321-4567"),
        Trainer("T2", "Serena Cardio", "serena@apex.fit", "Cardiorespiratory Conditioning", "B2", 4.8f, 12, "+1 (555) 987-6543"),
        Trainer("T3", "Bruce Way", "bruce@gotham.fit", "Martial Arts & Agility", "B1", 5.0f, 8, "+1 (555) 777-7777"),
        Trainer("T4", "Diana Shield", "diana@themyscira.com", "Strength & Functional Power", "B3", 4.9f, 10, "+1 (555) 828-9191")
    )

    val payments = listOf(
        Payment("F1", "Marcus Aurelius", "Elite Access", 79.99, "Paid", "2026-05-10", "B1"),
        Payment("F2", "Seneca Younger", "Elite Access", 79.99, "Paid", "2026-05-14", "B1"),
        Payment("F3", "Epictetus Disciple", "Power Builder", 49.99, "Overdue", "2026-04-01", "B2"),
        Payment("F4", "Alexander Great", "Elite Access", 79.99, "Paid", "2026-05-15", "B2"),
        Payment("F5", "Cleopatra Queen", "Power Builder", 49.99, "Paid", "2026-04-20", "B1"),
        Payment("F6", "Diogenes Barrel", "Basic Active", 29.99, "Pending", "2026-05-12", "B3")
    )

    val attendance = listOf(
        AttendanceRecord("A1", "M1", "Marcus Aurelius", "2026-05-27", "06:15 AM", "Present"),
        AttendanceRecord("A2", "M2", "Seneca Younger", "2026-05-27", "07:30 AM", "Present"),
        AttendanceRecord("A3", "M4", "Alexander Great", "2026-05-27", "05:00 PM", "Present"),
        AttendanceRecord("A4", "M5", "Cleopatra Queen", "2026-05-27", "09:00 AM", "Present"),
        AttendanceRecord("A5", "M7", "Hypatia Math", "2026-05-27", "10:15 AM", "Present"),
        AttendanceRecord("A6", "M8", "Leonidas Sparta", "2026-05-27", "04:30 AM", "Present")
    )
}
