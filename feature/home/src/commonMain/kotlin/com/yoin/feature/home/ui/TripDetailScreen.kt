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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.feature.home.viewmodel.TripDetailContract
import com.yoin.feature.home.viewmodel.TripDetailViewModel
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

                        Spacer(modifier = Modifier.height(32.dp))

                        // カウントダウンセクション
                        CountdownSection(
                            daysUntil = trip.daysUntilDevelopment,
                            developmentDateTime = trip.developmentDateTime
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // 今日の撮影セクション
                        TodayPhotosSection(
                            currentPhotos = trip.todayPhotos,
                            maxPhotos = trip.maxPhotos,
                            progress = trip.photoProgress,
                            remainingPhotos = trip.remainingPhotos
                        )

                        Spacer(modifier = Modifier.height(32.dp))

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

                            Spacer(modifier = Modifier.width(32.dp))

                            // 撮影ボタン
                            CameraButton(
                                onClick = {
                                    viewModel.onIntent(TripDetailContract.Intent.OnCameraPressed)
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(32.dp))
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
 * 旅行詳細ヘッダー
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
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // ステータスバー領域
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "9:41",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = YoinColors.TextPrimary
                )
            }

            // ヘッダーコンテンツ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 戻るボタン
                Text(
                    text = "←",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier.clickable(onClick = onBackPressed)
                )

                // タイトルと日付
                Column(
                    modifier = Modifier.weight(1f).padding(start = 16.dp)
                ) {
                    Text(
                        text = trip.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Text(
                        text = "${trip.dateRange} • ${trip.location}",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                // 設定ボタン
                Text(
                    text = "⚙",
                    fontSize = 20.sp,
                    color = YoinColors.TextPrimary,
                    modifier = Modifier
                        .clickable(onClick = onSettingsPressed)
                        .padding(end = 12.dp)
                )

                // 招待ボタン
                Button(
                    onClick = onInvitePressed,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YoinColors.Primary
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "招待",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.OnPrimary
                    )
                }
            }

            HorizontalDivider(
                color = YoinColors.SurfaceVariant,
                thickness = 0.65.dp
            )
        }
    }
}

/**
 * メンバーセクション
 */
@Composable
private fun MembersSection(
    members: List<TripDetailContract.Member>,
    onMembersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YoinColors.Background)
            .padding(vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onMembersClick)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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

            Text(
                text = "›",
                fontSize = 16.sp,
                color = YoinColors.TextSecondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

/**
 * メンバーチップ
 */
@Composable
private fun MemberChip(
    name: String,
    isSelected: Boolean
) {
    val backgroundColor = if (isSelected) YoinColors.AccentCoral else YoinColors.AccentPeach
    val textColor = if (isSelected) YoinColors.Primary else YoinColors.TextPrimary

    Box(
        modifier = Modifier
            .background(backgroundColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}

/**
 * カウントダウンセクション
 */
@Composable
private fun CountdownSection(
    daysUntil: Int,
    developmentDateTime: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // フィルムアイコン
        Text(
            text = "🎞",
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 現像まであと
        Text(
            text = "現像まであと",
            fontSize = 16.sp,
            color = YoinColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 日数表示
        Text(
            text = "${daysUntil}日",
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = YoinColors.Primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 現像日時
        Text(
            text = developmentDateTime,
            fontSize = 14.sp,
            color = YoinColors.TextSecondary
        )
    }
}

/**
 * 今日の撮影セクション
 */
@Composable
private fun TodayPhotosSection(
    currentPhotos: Int,
    maxPhotos: Int,
    progress: Float,
    remainingPhotos: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        HorizontalDivider(
            color = YoinColors.SurfaceVariant,
            thickness = 0.65.dp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // セクションタイトル
        Text(
            text = "今日の撮影",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = YoinColors.TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 撮影枚数カード
        Surface(
            color = YoinColors.Surface,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, YoinColors.SurfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "📸",
                        fontSize = 24.sp
                    )

                    Text(
                        text = currentPhotos.toString(),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = YoinColors.TextPrimary
                    )

                    Text(
                        text = "/ $maxPhotos 枚",
                        fontSize = 14.sp,
                        color = YoinColors.TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // プログレスバー
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(100.dp)),
                    color = YoinColors.Primary,
                    trackColor = YoinColors.SurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "残り${remainingPhotos}枚撮影できます",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
            }
        }
    }
}

/**
 * アクションボタン（地図、撮影など）
 */
@Composable
private fun ActionButton(
    icon: String,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = YoinColors.Primary
        ),
        shape = CircleShape,
        modifier = Modifier.size(80.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = YoinColors.OnPrimary
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
        icon = "📷",
        label = "撮影",
        onClick = onClick
    )
}
