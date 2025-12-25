package com.yoin.feature.room.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.room.model.MemberRole
import com.yoin.feature.room.model.RoomMember
import com.yoin.feature.room.viewmodel.MemberListContract
import com.yoin.feature.room.viewmodel.MemberListViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * メンバー一覧画面
 *
 * ルームのメンバー情報を表示し、管理するための画面です。
 *
 * @param viewModel MemberListViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 */
@Composable
fun MemberListScreen(
    viewModel: MemberListViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MemberListContract.Effect.NavigateBack -> onNavigateBack()
                is MemberListContract.Effect.NavigateToMemberProfile -> {
                    // TODO: メンバープロフィール画面へ遷移
                }
                is MemberListContract.Effect.ShowRemoveMemberConfirmation -> {
                    // TODO: 削除確認ダイアログを表示
                }
                is MemberListContract.Effect.ShowChangeRoleDialog -> {
                    // TODO: ロール変更ダイアログを表示
                }
                is MemberListContract.Effect.NavigateToInvite -> {
                    // TODO: 招待画面へ遷移
                }
                is MemberListContract.Effect.ShowError -> {
                    // TODO: エラーメッセージを表示
                }
                is MemberListContract.Effect.ShowSuccess -> {
                    // TODO: 成功メッセージを表示
                }
            }
        }
    }

    MemberListContent(
        state = state,
        onIntent = viewModel::handleIntent,
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun MemberListContent(
    state: MemberListContract.State,
    onIntent: (MemberListContract.Intent) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YoinColors.Background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ヘッダー
            YoinAppBar(
                title = "メンバー一覧",
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                },
                actions = {
                    // 招待ボタン
                    IconButton(
                        onClick = { onIntent(MemberListContract.Intent.OnInviteMemberPressed) }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Invite Member",
                            tint = YoinColors.Primary
                        )
                    }
                }
            )

            // コンテンツ
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YoinColors.Primary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ルーム情報ヘッダー
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = YoinColors.Primary.copy(alpha = 0.1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = YoinColors.Primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = state.roomName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = YoinColors.TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${state.members.size}人のメンバー",
                                        fontSize = 14.sp,
                                        color = YoinColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // メンバーリスト
                    items(state.members) { member ->
                        MemberListItem(
                            member = member,
                            isOwner = state.isOwner,
                            onMemberClicked = {
                                onIntent(MemberListContract.Intent.OnMemberClicked(member))
                            },
                            onRemoveClicked = {
                                onIntent(MemberListContract.Intent.OnRemoveMemberClicked(member.id))
                            },
                            onChangeRoleClicked = {
                                onIntent(MemberListContract.Intent.OnChangeRoleClicked(member.id))
                            }
                        )
                    }

                    // 下部スペース
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // ホームインジケーター
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .width(134.dp)
                .height(5.dp)
                .background(Color.Black, RoundedCornerShape(100.dp))
        )
    }
}

@Composable
private fun MemberListItem(
    member: RoomMember,
    isOwner: Boolean,
    onMemberClicked: () -> Unit,
    onRemoveClicked: () -> Unit,
    onChangeRoleClicked: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onMemberClicked),
        shape = RoundedCornerShape(12.dp),
        color = YoinColors.Surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // アバター
            Surface(
                shape = CircleShape,
                color = YoinColors.Primary,
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // メンバー情報
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = member.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // ロールバッジ
                    RoleBadge(role = member.role)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "参加日: ${member.joinedDate}",
                    fontSize = 12.sp,
                    color = YoinColors.TextSecondary
                )
            }

            // メニューボタン（オーナーのみ表示、かつオーナー自身には表示しない）
            if (isOwner && member.role != MemberRole.OWNER) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Menu",
                            tint = YoinColors.TextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("ロールを変更") },
                            onClick = {
                                showMenu = false
                                onChangeRoleClicked()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("メンバーを削除", color = Color.Red) },
                            onClick = {
                                showMenu = false
                                onRemoveClicked()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: MemberRole) {
    val (text, color) = when (role) {
        MemberRole.OWNER -> "オーナー" to YoinColors.Primary
        MemberRole.ADMIN -> "管理者" to Color(0xFF9C27B0)
        MemberRole.MEMBER -> return // 一般メンバーはバッジを表示しない
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun MemberListScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            MemberListContent(
                state = MemberListContract.State(
                    roomId = "room123",
                    roomName = "北海道旅行2025",
                    members = listOf(
                        RoomMember(
                            id = "1",
                            name = "田中太郎",
                            avatar = "",
                            role = MemberRole.OWNER,
                            joinedDate = "2025/1/1"
                        ),
                        RoomMember(
                            id = "2",
                            name = "佐藤花子",
                            avatar = "",
                            role = MemberRole.ADMIN,
                            joinedDate = "2025/1/2"
                        ),
                        RoomMember(
                            id = "3",
                            name = "鈴木一郎",
                            avatar = "",
                            role = MemberRole.MEMBER,
                            joinedDate = "2025/1/3"
                        ),
                    ),
                    isOwner = true
                )
            )
        }
    }
}
