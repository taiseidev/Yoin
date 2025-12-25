package com.yoin.domain.common.model

/**
 * オンボーディングページ情報
 */
data class OnboardingPage(
    val title: String,
    val description: String,
    val imageResName: String, // 画像リソース名（後でDrawableリソースと対応付け）
)

/**
 * オンボーディングページのプリセット
 */
object OnboardingPages {
    val pages = listOf(
        OnboardingPage(
            title = "旅の写真を、見えない状態で撮る",
            description = "撮った写真はすぐには見られません。\n翌朝の「現像」まで、余韻を楽しみましょう。",
            imageResName = "onboarding_1"
        ),
        OnboardingPage(
            title = "仲間と一緒に思い出をシェア",
            description = "招待コードでルームを作成。\nみんなで撮った写真が一箇所に集まります。",
            imageResName = "onboarding_2"
        ),
        OnboardingPage(
            title = "翌朝9時、一斉に現像",
            description = "旅行最終日の翌朝、すべての写真が現像されます。\n仲間と一緒に、旅の余韻を味わいましょう。",
            imageResName = "onboarding_3"
        ),
        OnboardingPage(
            title = "フィルムカメラのような体験",
            description = "限られた枚数、選べるフィルター。\nデジタルでありながら、アナログな楽しさを。",
            imageResName = "onboarding_4"
        )
    )
}
