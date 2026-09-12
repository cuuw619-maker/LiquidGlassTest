package com.cuuw619.liquidglasstest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { LiquidGlassApp() }
    }
}

@Composable
private fun LiquidGlassApp() {
    Box(Modifier.fillMaxSize()) {
        LiquidBackground()
        Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            androidx.compose.material3.Text("Liquid Glass", color = Color.White.copy(.94f), fontSize = 31.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            androidx.compose.material3.Text("Depth • refraction • light", color = Color.White.copy(.55f), fontSize = 14.sp)
            Spacer(Modifier.height(34.dp))
            LiquidGlassButton()
            Spacer(Modifier.height(22.dp))
            GlassCard()
        }
    }
}

@Composable
private fun LiquidBackground() {
    val transition = rememberInfiniteTransition(label = "background")
    val phase by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(12000), RepeatMode.Reverse), label = "phase")
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Brush.linearGradient(listOf(Color(0xFF070A20), Color(0xFF211044), Color(0xFF07102A))))
        listOf(
            Triple(Offset(size.width * (.16f + phase * .14f), size.height * .22f), 210f, Color(0xFF5B4BFF)),
            Triple(Offset(size.width * (.80f - phase * .12f), size.height * .30f), 240f, Color(0xFFB52FFF)),
            Triple(Offset(size.width * .50f, size.height * (.80f - phase * .10f)), 290f, Color(0xFF007AFF)),
            Triple(Offset(size.width * .10f, size.height * .78f), 160f, Color(0xFF00CDBB))
        ).forEach { (center, radius, color) ->
            drawCircle(Brush.radialGradient(listOf(color.copy(.60f), color.copy(alpha = 0f)), radius), center, radius)
        }
        repeat(8) { i ->
            val y = size.height * (i + 1) / 9f
            drawLine(Color.White.copy(.018f), Offset(0f, y), Offset(size.width, y - 90f), 1f)
        }
    }
}

@Composable
private fun LiquidGlassButton() {
    var pressed by remember { mutableStateOf(false) }
    val scale = remember { Animatable(1f) }
    val shine = remember { Animatable(0f) }
    LaunchedEffect(pressed) {
        launch { scale.animateTo(if (pressed) .955f else 1f, tween(180, easing = FastOutSlowInEasing)) }
        launch { shine.animateTo(if (pressed) 1f else 0f, tween(240)) }
    }
    Box(
        Modifier.size(290.dp, 82.dp)
            .graphicsLayer { scaleX = scale.value; scaleY = scale.value }
            .shadow(18.dp, RoundedCornerShape(41.dp), ambientColor = Color.Black.copy(.38f), spotColor = Color.Black.copy(.50f))
            .clip(RoundedCornerShape(41.dp))
            .pointerInput(Unit) { detectTapGestures(onPress = { pressed = true; tryAwaitRelease(); pressed = false }) }
            .liquidGlass()
    ) {
        Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
            androidx.compose.material3.Text("▶", color = Color.White.copy(.94f), fontSize = 25.sp)
            Spacer(Modifier.width(13.dp))
            androidx.compose.material3.Text("Liquid Glass", color = Color.White.copy(.96f), fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        if (shine.value > 0f) Canvas(Modifier.fillMaxSize()) {
            drawRoundRect(Color.White.copy(.30f * shine.value), style = Stroke(2.dp.toPx()), cornerRadius = CornerRadius(size.height / 2))
        }
    }
}

@Composable
private fun GlassCard() {
    Box(Modifier.size(290.dp, 132.dp).shadow(16.dp, RoundedCornerShape(30.dp)).clip(RoundedCornerShape(30.dp)).liquidGlass(stronger = true)) {
        Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.Center) {
            androidx.compose.material3.Text("Depth surface", color = Color.White.copy(.90f), fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(7.dp))
            androidx.compose.material3.Text("Soft refraction, edge highlight and layered illumination.", color = Color.White.copy(.58f), fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}

private fun Modifier.liquidGlass(stronger: Boolean = false): Modifier = drawWithCache {
    val r = size.minDimension / 2f
    val fill = Brush.linearGradient(listOf(Color.White.copy(if (stronger) .30f else .24f), Color.White.copy(.10f), Color.White.copy(if (stronger) .11f else .055f)), Offset.Zero, Offset(size.width, size.height))
    val edge = Brush.linearGradient(listOf(Color.White.copy(.78f), Color.White.copy(.20f), Color.Black.copy(.18f)), Offset.Zero, Offset(0f, size.height))
    onDrawWithContent {
        drawContent()
        drawRoundRect(fill, cornerRadius = CornerRadius(r))
        val sheen = Path().apply {
            moveTo(size.width * .10f, size.height * .27f)
            quadraticBezierTo(size.width * .35f, size.height * .04f, size.width * .72f, size.height * .12f)
        }
        drawPath(sheen, Color.White.copy(.22f), style = Stroke(1.2.dp.toPx()))
        drawRoundRect(edge, cornerRadius = CornerRadius(r), style = Stroke(1.35.dp.toPx()))
    }
}
