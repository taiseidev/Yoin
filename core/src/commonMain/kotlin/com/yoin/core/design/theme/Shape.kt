package com.yoin.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Yoinアプリのシェイプ（角丸）設定
 *
 * sassyアプリのデザインに基づく、より丸みのあるシェイプ
 */
val YoinShapes = Shapes(
    // Extra small components (e.g., chips, small buttons)
    extraSmall = RoundedCornerShape(8.dp),

    // Small components (e.g., cards, buttons)
    small = RoundedCornerShape(12.dp),

    // Medium components (e.g., dialogs, modals)
    medium = RoundedCornerShape(16.dp),

    // Large components (e.g., bottom sheets, large cards)
    large = RoundedCornerShape(24.dp),

    // Extra large components (e.g., full-screen dialogs)
    extraLarge = RoundedCornerShape(32.dp),
)
