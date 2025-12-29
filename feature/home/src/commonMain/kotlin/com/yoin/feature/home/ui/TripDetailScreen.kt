package com.yoin.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.design.theme.YoinSizes
import com.yoin.core.design.theme.YoinFontSizes
import com.yoin.feature.home.viewmodel.TripDetailContract
import com.yoin.feature.home.viewmodel.TripDetailViewModel
import com.yoin.core.ui.preview.PhonePreview
import kotlinx.coroutines.flow.collectLatest

/**
 * 旅行詳細画面
 *
 * @param tripId 旅行ID
 * @param viewModel TripDetailViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToCamera カメラ画面への遷移コールバック
 */
@Composable
fun TripDetailScreen(
    tripId: String,
    viewModel: TripDetailViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCamera: (String) -> Unit = {},
    onNavigateToSettings: (String) -> Unit = {},
    onNavigateToMap: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is TripDetailContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is TripDetailContract.Effect.NavigateBack -> {
                    onNavigateBack()
                }
                is TripDetailContract.Effect.NavigateToInvite -> {
                    // TODO: 招待画面への遷移
                }
                is TripDetailContract.Effect.NavigateToMembers -> {
                    // TODO: メンバー一覧画面への遷移
                }
                is TripDetailContract.Effect.NavigateToCamera -> {
                    onNavigateToCamera(effect.tripId)
                }
                is TripDetailContract.Effect.NavigateToSettings -> {
                    onNavigateToSettings(effect.tripId)
                }
                is TripDetailContract.Effect.NavigateToMap -> {
                    onNavigateToMap(effect.tripId)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(tripId) {
        viewModel.onIntent(TripDetailContract.Intent.OnScreenDisplayed(tripId))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Surface)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = YoinColors.Primary
            )
        } else {
            state.tripDetail?.let { trip ->
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // ヘッダー
                    TripDetailHeader(
                        trip = trip,
                        onBackPressed = {
                            viewModel.onIntent(TripDetailContract.Intent.OnBackPressed)
                        },
                        onInvitePressed = {
                            viewModel.onIntent(TripDetailContract.Intent.OnInvitePressed)
                        },
                        onSettingsPressed = {
                            viewModel.onIntent(TripDetailContract.Intent.OnSettingsPressed)
                        }
                    )

                    // メインコンテンツ
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 96.dp) // ボトムナビゲーション + FABのスペース確保
                    ) {
                        // メンバー一覧
                        MembersSection(
                            members = trip.members,
                            onMembersClick = {
                                viewModel.onIntent(TripDetailContract.Intent.OnMembersPressed)
                            }
                        )

                        Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

                        // カウントダウンセクション
                        CountdownSection(
                            daysUntil = trip.daysUntilDevelopment,
                            developmentDateTime = trip.developmentDateTime
                        )

                        Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

                        // 今日の撮影セクション
                        TodayPhotosSection(
                            currentPhotos = trip.todayPhotos,
                            maxPhotos = trip.maxPhotos,
                            progress = trip.photoProgress,
                            remainingPhotos = trip.remainingPhotos
                        )

                        Spacer(modifier = Modifier.height(YoinSpacing.xxxl))

                        // 撮影ボタンと地図ボタン
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 地図ボタン
                            ActionButton(
                                icon = "🗺",
                                label = "地図",
                                onClick = {
                                    viewModel.onIntent(TripDetailContract.Intent.OnMapPressed)
                                }
                            )

                            Spacer(modifier = Modifier.width(YoinSpacing.xxxl))

                            // 撮影ボタン
                            CameraButton(
                                onClick = {
                                    viewModel.onIntent(TripDetailContract.Intent.OnCameraPressed)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(YoinSpacing.xxxl))
                    }
                }
            }
        }

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 旅行詳細ヘッダー - Modern Cinematic Design
 */
@Composable
private fun TripDetailHeader(
    trip: TripDetailContract.TripDetail,
    onBackPressed: () -> Unit,
    onInvitePressed: () -> Unit,
    onSettingsPressed: () -> Unit
) {
    Surface(
        color = YoinColors.Surface,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                IconButton(
                    onClick = onBackPressed,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "戻る",
                        tint = YoinColors.TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // タイトルと日付
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = trip.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "${trip.dateRange} • ${trip.location}",
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // アクションボタン
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 設定ボタン
                    IconButton(
                        onClick = onSettingsPressed,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "設定",
                            tint = YoinColors.TextPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // 招待ボタン
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        YoinColors.Primary,
                                        YoinColors.PrimaryVariant
                                    )
                                )
                            )
                            .clickable(onClick = onInvitePressed)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PersonAdd,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "招待",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * メンバーセクション - Modern Cinematic Design
 */
@Composable
private fun MembersSection(
    members: List<TripDetailContract.Member>,
    onMembersClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onMembersClick),
        color = YoinColors.Surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // アイコン
            Icon(
                imageVector = Icons.Filled.People,
                contentDescription = null,
                tint = YoinColors.Primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // メンバーチップ
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(members.take(5)) { member ->
                    MemberChip(
                        name = member.name,
                        isSelected = member.isCurrentUser
                    )
                }

                // 追加メンバー表示
                if (members.size > 5) {
                    item {
                        MemberChip(
                            name = "+${members.size - 5}",
                            isSelected = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 矢印アイコン
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = YoinColors.TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * メンバーチップ - Modern Cinematic Design
 */
@Composable
private fun MemberChip(
    name: String,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isSelected) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.Primary,
                            YoinColors.PrimaryVariant
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            YoinColors.SurfaceVariant,
                            YoinColors.SurfaceVariant
                        )
                    )
                }
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else YoinColors.TextPrimary
        )
    }
}

/**
 * カウントダウンセクション - Modern Cinematic Design
 */
@Composable
private fun CountdownSection(
    daysUntil: Int,
    developmentDateTime: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = YoinColors.Surface,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            YoinColors.Surface,
                            YoinColors.SurfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // フィルムアイコン → Material Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    YoinColors.Primary.copy(alpha = 0.2f),
                                    YoinColors.PrimaryVariant.copy(alpha = 0.2f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 現像まであと
                Text(
                    text = "現像まであと",
                    fontSize = 14.sp,
                    color = YoinColors.TextSecondary,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 日数表示（少し小さく）
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = daysUntil.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.Primary,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "日",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.Primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 現像日時
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CalendarMonth,
                        contentDescription = null,
                        tint = YoinColors.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = developmentDateTime,
                        fontSize = 13.sp,
                        color = YoinColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * 今日の撮影セクション - Modern Cinematic Design
 */
@Composable
private fun TodayPhotosSection(
    currentPhotos: Int,
    maxPhotos: Int,
    progress: Float,
    remainingPhotos: Int
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        color = YoinColors.Surface,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // ヘッダー
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // カメラアイコン
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(YoinColors.Primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = YoinColors.Primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "今日の撮影",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary,
                        letterSpacing = (-0.2).sp
                    )
                    Text(
                        text = "残り${remainingPhotos}枚撮影できます",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // 撮影枚数
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = currentPhotos.toString(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.Primary,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "/ $maxPhotos",
                        fontSize = 16.sp,
                        color = YoinColors.TextSecondary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // プログレスバー
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(YoinColors.SurfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    YoinColors.Primary,
                                    YoinColors.PrimaryVariant
                                )
                            )
                        )
                )
            }
        }
    }
}

/**
 * アクションボタン（地図、撮影など）- Modern Cinematic Design
 */
@Composable
private fun ActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        YoinColors.Primary,
                        YoinColors.PrimaryVariant
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Material Icon に置き換え
            Icon(
                imageVector = if (label == "地図") Icons.Filled.Map else Icons.Filled.CameraAlt,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * 撮影ボタン
 */
@Composable
private fun CameraButton(
    onClick: () -> Unit
) {
    ActionButton(
        icon = "",
        label = "撮影",
        onClick = onClick
    )
}

/**
 * プレビュー: メンバーチップ
 */
@PhonePreview
@Composable
private fun MemberChipPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
                .padding(16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MemberChip(name = "太郎", isSelected = true)
                MemberChip(name = "花子", isSelected = false)
            }
        }
    }
}

/**
 * プレビュー: メンバーセクション
 */
@PhonePreview
@Composable
private fun MembersSectionPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
        ) {
            MembersSection(
                members = listOf(
                    TripDetailContract.Member(id = "1", name = "太郎", isCurrentUser = true),
                    TripDetailContract.Member(id = "2", name = "花子", isCurrentUser = false),
                    TripDetailContract.Member(id = "3", name = "次郎", isCurrentUser = false)
                ),
                onMembersClick = {}
            )
        }
    }
}

/**
 * プレビュー: カウントダウンセクション
 */
@PhonePreview
@Composable
private fun CountdownSectionPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp)
        ) {
            CountdownSection(
                daysUntil = 5,
                developmentDateTime = "2024/01/01 10:00"
            )
        }
    }
}

/**
 * プレビュー: 今日の撮影セクション
 */
@PhonePreview
@Composable
private fun TodayPhotosSectionPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
        ) {
            TodayPhotosSection(
                currentPhotos = 8,
                maxPhotos = 10,
                progress = 0.8f,
                remainingPhotos = 2
            )
        }
    }
}

/**
 * プレビュー: アクションボタン
 */
@PhonePreview
@Composable
private fun ActionButtonPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Surface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ActionButton(icon = "🗺", label = "地図", onClick = {})
                CameraButton(onClick = {})
            }
        }
    }
}
