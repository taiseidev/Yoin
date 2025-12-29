package com.yoin.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraRoll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.auth.viewmodel.WelcomeContract
import com.yoin.feature.auth.viewmodel.WelcomeViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Welcome画面 - Modern Cinematic Design
 *
 * デザインコンセプト:
 * - 旅行写真背景 + 黒グラデーションオーバーレイ
 * - シネマティックなアンバーアクセント
 * - Material Icons使用（絵文字排除）
 * - グラデーションボタン
 * - フォトアプリらしいスタイリッシュなUI
 *
 * @param viewModel WelcomeViewModel
 * @param onNavigateToEmailLogin メールログイン画面への遷移
 * @param onNavigateToRegister 登録方法選択画面への遷移
 * @param onNavigateToHome ホーム画面への遷移
 */
@Composable
fun WelcomeScreen(
    viewModel: WelcomeViewModel,
    onNavigateToEmailLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effect監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is WelcomeContract.Effect.NavigateToEmailLogin -> onNavigateToEmailLogin()
                is WelcomeContract.Effect.NavigateToRegister -> onNavigateToRegister()
                is WelcomeContract.Effect.NavigateToHome -> onNavigateToHome()
                is WelcomeContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is WelcomeContract.Effect.ShowSuccess -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 背景写真（旅行イメージ）
        AsyncImage(
            model = "https://images.unsplash.com/photo-1488646953014-85cb44e25828?w=1200", // 旅行のスーツケース
            contentDescription = "Travel Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // グラデーションオーバーレイ（黒 + アンバーアクセント）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.7f),
                            Color.Black.copy(alpha = 0.85f),
                            YoinColors.Primary.copy(alpha = 0.2f),
                            Color.Black.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // ロゴとタイトルセクション
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // ロゴアイコン（グラデーション背景）
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // ラジアルグラデーションオーバーレイ
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                ),
                                CircleShape
                            )
                    )

                    Icon(
                        imageVector = Icons.Filled.CameraRoll,
                        contentDescription = "Yoin Logo",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // タイトル
                Text(
                    text = "Yoin.",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    letterSpacing = (-1).sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // サブタイトル
                Text(
                    text = "余韻を残す旅の記録",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                    letterSpacing = 1.sp
                )
            }

            // ボタンセクション
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Googleでログイン
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(
                            width = 2.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {
                    Button(
                        onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnGoogleSignInPressed) },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !state.isLoading
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(Color(0xFFEA4335), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "Googleでログイン",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Appleでログイン
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White)
                ) {
                    Button(
                        onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnAppleSignInPressed) },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !state.isLoading
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "",
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Appleでログイン",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black
                            )
                        }
                    }
                }

                // メールでログイン（グラデーション）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            )
                        )
                ) {
                    Button(
                        onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnEmailLoginPressed) },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !state.isLoading
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Email,
                                contentDescription = "Email",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "メールでログイン",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 区切り線
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                    Text(
                        text = "または",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color.White.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 新規登録ボタン（ボーダー）
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .border(
                            width = 2.dp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        )
                ) {
                    Button(
                        onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnRegisterPressed) },
                        modifier = Modifier.fillMaxSize(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !state.isLoading
                    ) {
                        Text(
                            text = "新規登録",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                // ゲストで始める
                TextButton(
                    onClick = { viewModel.handleIntent(WelcomeContract.Intent.OnGuestPressed) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "ゲストで始める",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // ローディング
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = YoinColors.Primary)
            }
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(
            viewModel = WelcomeViewModel(),
            onNavigateToEmailLogin = {},
            onNavigateToRegister = {},
            onNavigateToHome = {}
        )
    }
}
