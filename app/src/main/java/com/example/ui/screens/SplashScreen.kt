package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale = infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteTransitionSpec(),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(1500)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // High-fidelity custom glowing Gym Logo drawn using Canvas
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(pulseScale.value)
                    .testTag("splash_logo"),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    
                    // Draw Dumbbell Barbell Center
                    drawLine(
                        color = Color.LightGray,
                        start = Offset(w * 0.2f, h * 0.5f),
                        end = Offset(w * 0.8f, h * 0.5f),
                        strokeWidth = 14f
                    )
                    
                    // Draw Dumbbell Plates Left
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(w * 0.2f - 20f, h * 0.35f),
                        size = Size(20f, h * 0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = secondaryColor,
                        topLeft = Offset(w * 0.2f - 45f, h * 0.3f),
                        size = Size(20f, h * 0.4f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )
                    
                    // Draw Dumbbell Plates Right
                    drawRoundRect(
                        color = primaryColor,
                        topLeft = Offset(w * 0.8f, h * 0.35f),
                        size = Size(20f, h * 0.3f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
                    )
                    drawRoundRect(
                        color = secondaryColor,
                        topLeft = Offset(w * 0.8f + 25f, h * 0.3f),
                        size = Size(20f, h * 0.4f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                    )
                    
                    // Draw Sportive Gym Halo Ring
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.3f),
                        radius = w * 0.45f,
                        style = Stroke(width = 4f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "APEX ARENA",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "MULTI-TENANT GYM MANAGEMENT RESOURCE",
                style = MaterialTheme.typography.labelSmall,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun infiniteTransitionSpec(): InfiniteRepeatableSpec<Float> {
    return infiniteRepeatable(
        animation = tween(durationMillis = 1000, easing = LinearEasing),
        repeatMode = RepeatMode.Reverse
    )
}
