package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.MagentaPrimary
import com.example.ui.theme.VioletGlow

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barColor: Color = ElectricPurple
) {
    val barBaseHeights = listOf(
        0.25f, 0.58f, 0.92f, 0.45f, 0.83f, 0.35f, 0.67f, 1.0f,
        0.55f, 0.90f, 0.40f, 0.75f, 1.0f, 0.48f, 0.80f, 0.32f,
        0.65f, 0.90f, 0.42f, 0.72f, 0.85f, 0.50f, 0.30f
    )

    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    val anim1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 420, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_1"
    )

    val anim2 by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 580, delayMillis = 90, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_2"
    )

    val anim3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 360, delayMillis = 180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave_3"
    )

    val activeBrush = Brush.verticalGradient(
        colors = listOf(
            MagentaPrimary,
            ElectricPurple,
            VioletGlow
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.3f)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        barBaseHeights.forEachIndexed { index, baseFactor ->
            val dynamicScale = when (index % 3) {
                0 -> anim1
                1 -> anim2
                else -> anim3
            }
            val heightFraction = if (isPlaying) {
                (baseFactor * (0.35f + dynamicScale * 0.65f)).coerceIn(0.15f, 1.0f)
            } else {
                (baseFactor * 0.22f).coerceIn(0.12f, 0.28f)
            }

            Box(
                modifier = Modifier
                    .width(4.5.dp)
                    .height((50 * heightFraction).dp)
                    .clip(CircleShape)
                    .then(
                        if (isPlaying) {
                            Modifier.background(activeBrush)
                        } else {
                            Modifier.background(barColor.copy(alpha = 0.3f))
                        }
                    )
            )
        }
    }
}
