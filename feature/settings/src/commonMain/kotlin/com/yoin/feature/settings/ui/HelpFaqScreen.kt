package com.yoin.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoin.core.design.theme.YoinColors
import com.yoin.core.design.theme.YoinSpacing
import com.yoin.core.ui.component.YoinAppBar
import com.yoin.core.ui.preview.PhonePreview
import com.yoin.feature.settings.viewmodel.HelpFaqContract
import com.yoin.feature.settings.viewmodel.HelpFaqViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * ヘルプ・FAQ画面
 *
 * 機能:
 * - よくある質問の表示
 * - カテゴリから探す
 * - 検索機能
 * - お問い合わせへの遷移
 *
 * @param viewModel HelpFaqViewModel
 * @param onNavigateBack 戻るボタンのコールバック
 * @param onNavigateToFaqDetail FAQ詳細への遷移コールバック
 * @param onNavigateToCategoryDetail カテゴリ詳細への遷移コールバック
 * @param onNavigateToContactSupport お問い合わせへの遷移コールバック
 */
@Composable
fun HelpFaqScreen(
    viewModel: HelpFaqViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToFaqDetail: (HelpFaqContract.FaqItem) -> Unit = {},
    onNavigateToCategoryDetail: (HelpFaqContract.Category) -> Unit = {},
    onNavigateToContactSupport: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Effectの監視
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is HelpFaqContract.Effect.NavigateBack -> onNavigateBack()
                is HelpFaqContract.Effect.NavigateToFaqDetail -> {
                    onNavigateToFaqDetail(effect.faqItem)
                }
                is HelpFaqContract.Effect.NavigateToCategoryDetail -> {
                    onNavigateToCategoryDetail(effect.category)
                }
                is HelpFaqContract.Effect.NavigateToContactSupport -> {
                    onNavigateToContactSupport()
                }
                is HelpFaqContract.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    // 画面表示時の初期化
    LaunchedEffect(Unit) {
        viewModel.onIntent(HelpFaqContract.Intent.OnScreenDisplayed)
    }

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
                title = "ヘルプ",
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.onIntent(HelpFaqContract.Intent.OnBackPressed)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = YoinColors.TextPrimary
                        )
                    }
                }
            )

            // コンテンツ
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 検索バー
                item {
                    SearchBar(
                        searchQuery = state.searchQuery,
                        onSearchQueryChanged = { query ->
                            viewModel.onIntent(HelpFaqContract.Intent.OnSearchQueryChanged(query))
                        }
                    )
                }

                // よくある質問セクション
                item {
                    Text(
                        text = "よくある質問",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextSecondary
                    )
                }

                // FAQリスト
                items(state.faqItems) { faqItem ->
                    FaqItemCard(
                        faqItem = faqItem,
                        onClick = {
                            viewModel.onIntent(HelpFaqContract.Intent.OnFaqItemClicked(faqItem))
                        }
                    )
                }

                // カテゴリから探すセクション
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "カテゴリから探す",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextSecondary
                    )
                }

                // カテゴリグリッド
                item {
                    CategoryGrid(
                        categories = state.categories,
                        onCategoryClick = { category ->
                            viewModel.onIntent(HelpFaqContract.Intent.OnCategoryClicked(category))
                        }
                    )
                }

                // お問い合わせセクション
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactSupportCard(
                        onClick = {
                            viewModel.onIntent(HelpFaqContract.Intent.OnContactSupportPressed)
                        }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
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

        // スナックバー
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * 検索バー
 */
@Composable
private fun SearchBar(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "質問を検索",
                color = YoinColors.TextSecondary
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                tint = YoinColors.TextSecondary
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = YoinColors.SurfaceVariant,
            focusedBorderColor = YoinColors.TextSecondary,
            unfocusedContainerColor = YoinColors.Surface,
            focusedContainerColor = YoinColors.Surface
        )
    )
}

/**
 * FAQアイテムカード
 */
@Composable
private fun FaqItemCard(
    faqItem: HelpFaqContract.FaqItem,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = YoinColors.Surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // アイコン
                Text(
                    text = faqItem.icon,
                    fontSize = 18.sp
                )

                // 質問と回答
                Column {
                    Text(
                        text = faqItem.question,
                        fontSize = 14.sp,
                        color = YoinColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = faqItem.answer,
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            // 矢印
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = YoinColors.TextSecondary
            )
        }
    }
}

/**
 * カテゴリグリッド
 */
@Composable
private fun CategoryGrid(
    categories: List<HelpFaqContract.Category>,
    onCategoryClick: (HelpFaqContract.Category) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        categories.forEach { category ->
            CategoryCard(
                category = category,
                onClick = { onCategoryClick(category) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

/**
 * カテゴリカード
 */
@Composable
private fun CategoryCard(
    category: HelpFaqContract.Category,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        color = YoinColors.Surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // アイコン
            Text(
                text = category.icon,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // タイトル
            Text(
                text = category.title,
                fontSize = 12.sp,
                color = YoinColors.TextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * お問い合わせカード
 */
@Composable
private fun ContactSupportCard(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = YoinColors.AccentPeach,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // アイコン
                Icon(
                    imageVector = Icons.Filled.Message,
                    contentDescription = "Contact",
                    tint = YoinColors.Primary,
                    modifier = Modifier.size(24.dp)
                )

                // テキスト
                Column {
                    Text(
                        text = "お問い合わせ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = YoinColors.TextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "解決しない場合はこちら",
                        fontSize = 12.sp,
                        color = YoinColors.TextSecondary
                    )
                }
            }

            // 矢印
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Navigate",
                tint = YoinColors.Primary
            )
        }
    }
}

/**
 * プレビュー
 */
@PhonePreview
@Composable
private fun HelpFaqScreenPreview() {
    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YoinColors.Background)
        ) {
            Text("Help FAQ Screen Preview")
        }
    }
}
