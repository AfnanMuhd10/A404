package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BentoAmber
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoEmerald
import com.example.ui.theme.BentoEmeraldContainer
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary

@Composable
fun FakeCalculatorView(
    onUnlockMaster: () -> Unit,
    onExitDisguise: () -> Unit
) {
    var displayValue by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }

    val buttons = listOf(
        listOf("C", "±", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=", "🔓")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        // Disguise status hint
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CALC v4.1 (STANDARD)",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoTextSecondary.copy(alpha = 0.5f),
                    fontFamily = FontFamily.Monospace
                )
            )
            Text(
                text = "Type '7777=' to unlock",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = BentoPrimary.copy(alpha = 0.8f),
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(BentoSurface)
                .padding(20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                if (expression.isNotEmpty()) {
                    Text(
                        text = expression,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BentoTextSecondary,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary,
                        fontSize = 38.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    maxLines = 1,
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Keypad Grid
        buttons.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { label ->
                    val isOp = label in listOf("÷", "×", "-", "+", "=")
                    val isSpecial = label in listOf("C", "±", "%")
                    val isUnlock = label == "🔓"

                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isUnlock -> BentoEmeraldContainer
                                    isOp -> BentoAmber
                                    isSpecial -> BentoSurfaceVariant
                                    else -> BentoSurface
                                }
                            )
                            .clickable {
                                when (label) {
                                    "C" -> {
                                        displayValue = "0"
                                        expression = ""
                                    }
                                    "=" -> {
                                        if (displayValue == "7777" || expression.contains("7777")) {
                                            onUnlockMaster()
                                        } else {
                                            displayValue = try {
                                                val res = evaluateSimple(expression + displayValue)
                                                res.toString()
                                            } catch (e: Exception) {
                                                "Error"
                                            }
                                            expression = ""
                                        }
                                    }
                                    "🔓" -> {
                                        onUnlockMaster()
                                    }
                                    "+", "-", "×", "÷" -> {
                                        expression = "$displayValue $label "
                                        displayValue = "0"
                                    }
                                    else -> {
                                        if (displayValue == "0") {
                                            displayValue = label
                                        } else {
                                            displayValue += label
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUnlock) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = "Unlock Vault",
                                tint = BentoEmerald,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 22.sp,
                                    color = if (isOp) Color.White else BentoTextPrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onExitDisguise,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("exit_disguise_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = BentoSurfaceVariant,
                contentColor = BentoTextSecondary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(text = "Exit Disguise Mode", style = MaterialTheme.typography.labelMedium)
        }
    }
}

private fun evaluateSimple(expr: String): Long {
    val tokens = expr.split(" ").filter { it.isNotBlank() }
    if (tokens.size >= 3) {
        val a = tokens[0].toDoubleOrNull() ?: 0.0
        val op = tokens[1]
        val b = tokens[2].toDoubleOrNull() ?: 0.0
        val result = when (op) {
            "+" -> a + b
            "-" -> a - b
            "×" -> a * b
            "÷" -> if (b != 0.0) a / b else 0.0
            else -> a
        }
        return result.toLong()
    }
    return expr.filter { it.isDigit() }.toLongOrNull() ?: 0L
}

