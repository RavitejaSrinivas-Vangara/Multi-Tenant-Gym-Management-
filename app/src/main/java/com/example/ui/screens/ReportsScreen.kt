package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.PrimaryOrange
import com.example.ui.theme.SecondaryTeal
import com.example.viewmodel.GymViewModel

@Composable
fun ReportsScreen(viewModel: GymViewModel) {
    var animationTriggered by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationTriggered) 1f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "charts_reveal"
    )

    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("reports_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Analytics,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Analytical Summary Reports",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "Aggregated business metrics drawn dynamically using GPU performance Canvas rendering.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        // 1. CHART: Revenue Growth (Bar Chart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Consolidated Monthly Revenue (CY2026)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gross billings in USD (\$K)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                RevenueBarChart(progress = animatedProgress)
            }
        }

        // 2. CHART: Weekly Check-In Distribution (Area Line Chart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Check-In Load Density",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Check-ins frequency percentage",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                WeeklyAttendanceLineChart(progress = animatedProgress)
            }
        }

        // 3. CHART: Branch Member Share (Pie Stack Segment)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Tenant Registration Distribution",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                BranchDistributionPieSegment()
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun RevenueBarChart(progress: Float) {
    val barValues = listOf(12.4f, 15.8f, 18.2f, 21.5f, 24.3f) // in Thousands
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May")
    val barGradient = Brush.verticalGradient(
        colors = listOf(PrimaryOrange, SecondaryTeal)
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag("revenue_canvas")
    ) {
        val w = size.width
        val h = size.height

        val paddingLeft = 40f
        val paddingBottom = 40f
        val chartWidth = w - paddingLeft
        val chartHeight = h - paddingBottom

        // Draw axis lines
        drawLine(
            color = Color.LightGray.copy(alpha = 0.5f),
            start = Offset(paddingLeft, 0f),
            end = Offset(paddingLeft, chartHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.LightGray.copy(alpha = 0.5f),
            start = Offset(paddingLeft, chartHeight),
            end = Offset(w, chartHeight),
            strokeWidth = 2f
        )

        // Draw horizontal grid helpers
        val gridLines = 4
        for (i in 1..gridLines) {
            val y = chartHeight * (i.toFloat() / gridLines)
            drawLine(
                color = Color.LightGray.copy(alpha = 0.2f),
                start = Offset(paddingLeft, y),
                end = Offset(w, y),
                strokeWidth = 1f
            )
        }

        // Draw columns
        val barCount = barValues.size
        val barWidth = (chartWidth / barCount) * 0.5f
        val gap = (chartWidth / barCount) * 0.5f

        val maxValue = 30f // upper bounds scale

        for (i in 0 until barCount) {
            val value = barValues[i]
            val animatedHeight = (chartHeight * (value / maxValue)) * progress
            val x = paddingLeft + (i * (barWidth + gap)) + (gap / 2)
            val y = chartHeight - animatedHeight

            drawRoundRect(
                brush = barGradient,
                topLeft = Offset(x, y),
                size = Size(barWidth, animatedHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )
        }
    }

    // Graphic Legend names
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        months.forEach { month ->
            Text(
                text = month,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(36.dp)
            )
        }
    }
}

@Composable
fun WeeklyAttendanceLineChart(progress: Float) {
    val checkRates = listOf(0.40f, 0.55f, 0.48f, 0.68f, 0.72f, 0.85f, 0.50f) // S M T W T F S
    val days = listOf("M", "T", "W", "T", "F", "S", "S")

    val accentTeal = SecondaryTeal
    val limeAlpha = AccentGreen.copy(alpha = 0.15f)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .testTag("attendance_canvas")
    ) {
        val w = size.width
        val h = size.height

        val paddingLeft = 40f
        val paddingBottom = 40f
        val chartWidth = w - paddingLeft
        val chartHeight = h - paddingBottom

        // Draw Axis helper lines
        drawLine(
            color = Color.LightGray.copy(alpha = 0.4f),
            start = Offset(paddingLeft, chartHeight),
            end = Offset(w, chartHeight),
            strokeWidth = 2f
        )

        // Generate spline vector coordinates nodes
        val count = checkRates.size
        val stepX = chartWidth / (count - 1)

        val points = mutableListOf<Offset>()
        for (i in 0 until count) {
            val px = paddingLeft + (i * stepX)
            val py = chartHeight - (chartHeight * checkRates[i] * progress)
            points.add(Offset(px, py))
        }

        // Draw Spline Line path
        val path = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }
        }

        // Draw filling area underneath Spline
        val areaPath = Path().apply {
            if (points.isNotEmpty()) {
                moveTo(paddingLeft, chartHeight)
                for (i in 0 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
                lineTo(points.last().x, chartHeight)
                close()
            }
        }

        // Clip/fill canvas area
        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(limeAlpha, Color.Transparent)
            )
        )

        drawPath(
            path = path,
            color = accentTeal,
            style = Stroke(width = 6f)
        )

        // Draw dot nodes on top
        points.forEach { pt ->
            drawCircle(
                color = PrimaryOrange,
                radius = 10f,
                center = pt
            )
            drawCircle(
                color = Color.White,
                radius = 5f,
                center = pt
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        days.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.width(18.dp)
            )
        }
    }
}

@Composable
fun BranchDistributionPieSegment() {
    val distributions = listOf(
        Triple("Iron Arena (NYC)", 0.45f, PrimaryOrange),
        Triple("Apex Peak (LA)", 0.35f, SecondaryTeal),
        Triple("Titan Forge (IL)", 0.20f, AccentGreen)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Horizontal stacked distribution line segment
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            distributions.forEach { (_, fraction, color) ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(fraction)
                        .background(color)
                )
            }
        }

        // Custom Legend rows
        distributions.forEach { (name, fraction, color) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(color, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${(fraction * 100).toInt()}% share",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}
