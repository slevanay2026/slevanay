package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.i18n.AppLanguage
import com.example.ui.theme.GlassBorder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LanguageFlagIcon(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    width: Dp = 30.dp,
    height: Dp = 20.dp
) {
    Surface(
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, GlassBorder.copy(alpha = 0.7f)),
        color = Color.Transparent,
        modifier = modifier
            .size(width = width, height = height)
            .semantics { contentDescription = language.nativeName }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (language) {
                AppLanguage.TURKISH -> drawTurkishFlag()
                AppLanguage.ENGLISH -> drawUkFlag()
                AppLanguage.ARABIC -> drawArabicFlag()
                AppLanguage.KURDISH_SORANI,
                AppLanguage.KURDISH_BADINI -> drawKurdistanFlag()
            }
        }
    }
}

private fun DrawScope.drawTurkishFlag() {
    val red = Color(0xFFE30A17)
    drawRect(color = red, size = size)

    val outerRadius = size.height * 0.25f
    val outerCenter = Offset(x = size.width * 0.38f, y = size.height * 0.5f)
    drawCircle(color = Color.White, radius = outerRadius, center = outerCenter)

    val innerRadius = size.height * 0.20f
    val innerCenter = Offset(x = size.width * 0.43f, y = size.height * 0.5f)
    drawCircle(color = red, radius = innerRadius, center = innerCenter)

    val starCenter = Offset(x = size.width * 0.62f, y = size.height * 0.5f)
    drawStar(
        center = starCenter,
        outerRadius = size.height * 0.135f,
        innerRadius = size.height * 0.055f,
        points = 5,
        color = Color.White,
        startAngleDeg = -18.0
    )
}

private fun DrawScope.drawUkFlag() {
    val navy = Color(0xFF012169)
    val crimson = Color(0xFFC8102E)

    drawRect(color = navy, size = size)

    // White diagonals
    val diagWhiteStroke = size.height * 0.22f
    drawLine(
        color = Color.White,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
        strokeWidth = diagWhiteStroke
    )
    drawLine(
        color = Color.White,
        start = Offset(0f, size.height),
        end = Offset(size.width, 0f),
        strokeWidth = diagWhiteStroke
    )

    // Red diagonals
    val diagRedStroke = size.height * 0.10f
    drawLine(
        color = crimson,
        start = Offset.Zero,
        end = Offset(size.width, size.height),
        strokeWidth = diagRedStroke
    )
    drawLine(
        color = crimson,
        start = Offset(0f, size.height),
        end = Offset(size.width, 0f),
        strokeWidth = diagRedStroke
    )

    // White cross
    val crossWhite = size.height * 0.34f
    drawRect(
        color = Color.White,
        topLeft = Offset(0f, (size.height - crossWhite) / 2f),
        size = Size(size.width, crossWhite)
    )
    drawRect(
        color = Color.White,
        topLeft = Offset((size.width - crossWhite) / 2f, 0f),
        size = Size(crossWhite, size.height)
    )

    // Red St. George's cross
    val crossRed = size.height * 0.20f
    drawRect(
        color = crimson,
        topLeft = Offset(0f, (size.height - crossRed) / 2f),
        size = Size(size.width, crossRed)
    )
    drawRect(
        color = crimson,
        topLeft = Offset((size.width - crossRed) / 2f, 0f),
        size = Size(crossRed, size.height)
    )
}

private fun DrawScope.drawArabicFlag() {
    val green = Color(0xFF006C35)
    drawRect(color = green, size = size)

    // Stylized white calligraphy motif
    val scriptPath = Path().apply {
        val yMid = size.height * 0.38f
        moveTo(size.width * 0.22f, yMid)
        lineTo(size.width * 0.78f, yMid)
        moveTo(size.width * 0.28f, yMid - size.height * 0.09f)
        lineTo(size.width * 0.36f, yMid - size.height * 0.09f)
        moveTo(size.width * 0.44f, yMid - size.height * 0.11f)
        lineTo(size.width * 0.56f, yMid - size.height * 0.11f)
        moveTo(size.width * 0.64f, yMid - size.height * 0.08f)
        lineTo(size.width * 0.74f, yMid - size.height * 0.08f)
    }
    drawPath(
        path = scriptPath,
        color = Color.White,
        style = Stroke(width = size.height * 0.075f, cap = StrokeCap.Round)
    )

    // Sword beneath the calligraphy
    val swordY = size.height * 0.68f
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(size.width * 0.24f, swordY),
        size = Size(size.width * 0.50f, size.height * 0.08f),
        cornerRadius = CornerRadius(size.height * 0.04f, size.height * 0.04f)
    )
    drawRect(
        color = Color.White,
        topLeft = Offset(size.width * 0.68f, swordY - size.height * 0.06f),
        size = Size(size.width * 0.04f, size.height * 0.20f)
    )
}

private fun DrawScope.drawKurdistanFlag() {
    val red = Color(0xFFD91E2A)
    val green = Color(0xFF278E43)
    val sunGold = Color(0xFFFDB913)

    val stripeHeight = size.height / 3f
    drawRect(color = red, topLeft = Offset.Zero, size = Size(size.width, stripeHeight))
    drawRect(color = Color.White, topLeft = Offset(0f, stripeHeight), size = Size(size.width, stripeHeight))
    drawRect(color = green, topLeft = Offset(0f, stripeHeight * 2f), size = Size(size.width, stripeHeight))

    val center = Offset(size.width / 2f, size.height / 2f)
    drawStar(
        center = center,
        outerRadius = size.height * 0.28f,
        innerRadius = size.height * 0.145f,
        points = 21,
        color = sunGold,
        startAngleDeg = -90.0
    )
    drawCircle(
        color = sunGold,
        radius = size.height * 0.14f,
        center = center
    )
}

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    points: Int,
    color: Color,
    startAngleDeg: Double = -90.0
) {
    val path = Path()
    val step = PI / points
    var angle = Math.toRadians(startAngleDeg)
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + (cos(angle) * r).toFloat()
        val y = center.y + (sin(angle) * r).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        angle += step
    }
    path.close()
    drawPath(path = path, color = color)
}
