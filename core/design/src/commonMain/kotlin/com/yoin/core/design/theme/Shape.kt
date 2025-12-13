package com.yoin.core.design.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Yoinアプリのシェイプ（角丸）設定
 *
 * Material 3のシェイプシステムに基づく
 */
val YoinShapes = Shapes(
    // Extra small components (e.g., chips, buttons)
    extraSmall = RoundedCornerShape(4.dp),

    // Small components (e.g., cards, buttons)
    small = RoundedCornerShape(8.dp),

    // Medium components (e.g., dialogs, modals)
    medium = RoundedCornerShape(12.dp),

    // Large components (e.g., bottom sheets, large cards)
    large = RoundedCornerShape(16.dp),

    // Extra large components (e.g., full-screen dialogs)
    extraLarge = RoundedCornerShape(24.dp),
)
