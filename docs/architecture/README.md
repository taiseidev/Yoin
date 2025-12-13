# アーキテクチャ刷新提案

## 📋 概要

Yoinアプリのアーキテクチャを、現在の2モジュール構成から、**適切な粒度で機能分割されたfeature/core構成**に刷新する提案です。

---

## 🎯 刷新の目的

### 現在の課題

| 課題 | 影響 |
|---|---|
| **ビルド時間が長い** | 開発効率の低下 |
| **責任範囲が不明確** | コード品質の低下 |
| **チーム開発が困難** | コンフリクト頻発 |
| **テストが困難** | 品質保証の問題 |

### 期待される効果

| 効果 | 具体的なメリット |
|---|---|
| **ビルド時間の短縮** | 変更したfeatureのみ再ビルド → 50-70%短縮 |
| **明確な責任分離** | コードの可読性・保守性向上 |
| **並行開発の容易化** | コンフリクト減少、チーム拡大可能 |
| **テスト容易性** | モジュール単位でのテスト可能 |
| **スケーラビリティ** | 新機能追加が容易 |

---

## 🏗 新しいアーキテクチャ

### モジュール構成

```
yoin/
├── app/                      # アプリケーション統合 (1モジュール)
│
├── feature/                  # 機能モジュール (9モジュール)
│   ├── onboarding/          # スプラッシュ・オンボーディング
│   ├── auth/                # 認証
│   ├── home/                # ホーム画面
│   ├── room/                # ルーム管理
│   ├── camera/              # カメラ・撮影
│   ├── timeline/            # タイムライン・写真閲覧
│   ├── map/                 # マップ表示
│   ├── profile/             # プロフィール
│   └── settings/            # 設定
│
└── core/                     # コアモジュール (10モジュール)
    ├── ui/                  # 共通UIコンポーネント
    ├── design/              # デザインシステム
    ├── domain/              # ビジネスロジック
    ├── data/                # データ層
    ├── network/             # ネットワーク
    ├── database/            # ローカルDB
    ├── analytics/           # 分析
    ├── notification/        # 通知
    ├── camera/              # カメラハードウェア
    ├── image/               # 画像処理
    └── common/              # 共通ユーティリティ
```

**合計:** 20モジュール (app 1 + feature 9 + core 10)

---

## 📊 依存関係の方向

```
     app
      │
      ├─→ feature:onboarding ─┐
      ├─→ feature:auth        │
      ├─→ feature:home        │
      ├─→ feature:room        │
      ├─→ feature:camera      ├─→ core:ui
      ├─→ feature:timeline    │      ↓
      ├─→ feature:map         │   core:design
      ├─→ feature:profile     │      ↓
      └─→ feature:settings    ├─→ core:domain
                              │      ↓
                              ├─→ core:data
                              │      ↓
                              └─→ core:network
                                     core:database
                                     core:image
                                     core:camera
                                     core:analytics
                                     core:notification
                                     core:common
```

### 重要なルール

1. **Feature → Core**: ✅ 許可
2. **Feature → Feature**: ❌ 禁止
3. **Core → Feature**: ❌ 禁止
4. **Core → Core**: ⚠️ 最小限

---

## 🚀 移行計画

### フェーズ別スケジュール

| フェーズ | 期間 | 内容 | 成果物 |
|---|---|---|---|
| **Phase 1** | Week 1-2 | Core層の構築 | 10個のCoreモジュール |
| **Phase 2** | Week 3-6 | Feature層の構築 | 9個のFeatureモジュール |
| **Phase 3** | Week 7 | appモジュールの構築 | ナビゲーション統合 |
| **Phase 4** | Week 7 | settings.gradle.kts更新 | ビルド設定完了 |
| **Phase 5** | Week 8 | 古いモジュール削除 | 移行完了 |

### 優先順位

#### 最優先 (Week 1-3)
1. core:common, core:design, core:ui
2. core:domain
3. **feature:onboarding** ← すでに実装済み！

#### 高優先度 (Week 4-5)
4. core:network, core:data
5. feature:auth, feature:home

#### 中優先度 (Week 6-7)
6. core:database, core:image, core:camera
7. feature:room, feature:camera, feature:timeline

#### 低優先度 (Week 8以降)
8. core:analytics, core:notification
9. feature:map, feature:profile, feature:settings

---

## 📁 ドキュメント

### 詳細ドキュメント

1. **[モジュール化戦略](./modularization-strategy.md)**
   - 各モジュールの責務
   - 依存関係の詳細
   - モジュール間通信

2. **[移行計画](./migration-plan.md)**
   - フェーズ別の詳細手順
   - build.gradle.kts例
   - チェックリスト

---

## 💡 設計の原則

### 1. 単一責任の原則 (Single Responsibility)
- 各featureモジュールは1つの機能領域を担当
- core:uiはUIコンポーネントのみ、core:domainはビジネスロジックのみ

### 2. 依存関係逆転の原則 (Dependency Inversion)
- Feature層はCore層に依存
- Core層はFeature層を知らない
- core:domainは他のcoreモジュールに依存しない（expect/actualは除く）

### 3. 開放閉鎖の原則 (Open-Closed)
- 新機能追加時は新しいfeatureモジュールを作成
- 既存モジュールの修正を最小限に

### 4. インターフェース分離の原則 (Interface Segregation)
- 各モジュールは必要な依存のみを持つ
- 不要なcoreモジュールには依存しない

---

## 🎨 具体例: feature:onboarding

### ディレクトリ構造

```
feature/onboarding/
├── build.gradle.kts
└── src/
    └── commonMain/
        └── kotlin/
            └── com/yoin/feature/onboarding/
                ├── ui/
                │   ├── SplashScreen.kt
                │   └── OnboardingScreen.kt
                ├── viewmodel/
                │   ├── SplashViewModel.kt
                │   └── SplashContract.kt
                └── di/
                    └── OnboardingModule.kt
```

### 依存関係

```kotlin
// feature/onboarding/build.gradle.kts
dependencies {
    // Core dependencies
    implementation(project(":core:ui"))
    implementation(project(":core:design"))
    implementation(project(":core:domain"))

    // External libraries
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.androidx.navigation.compose)
}
```

### 画面実装

```kotlin
// feature/onboarding/src/commonMain/kotlin/.../SplashScreen.kt
package com.yoin.feature.onboarding.ui

import com.yoin.core.ui.preview.PhonePreview  // core:ui
import com.yoin.core.design.YoinTheme          // core:design
import com.yoin.core.domain.model.*            // core:domain

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onNavigateToAuth: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    // ...
}

@PhonePreview
@Composable
private fun SplashScreenPreview() {
    YoinTheme {
        SplashContent(/*...*/)
    }
}
```

---

## ✅ メリットの詳細

### 1. ビルドパフォーマンス向上

**現在:**
```
composeApp変更 → 全体ビルド (2-3分)
shared変更    → 全体ビルド (2-3分)
```

**移行後:**
```
feature:camera変更 → feature:cameraのみビルド (10-20秒)
core:ui変更       → core:ui + 依存feature (30-60秒)
```

**削減率:** 50-70%

### 2. 並行開発の容易化

**現在:**
```
開発者A: composeApp/ui/screens/camera/CameraScreen.kt
開発者B: composeApp/ui/screens/timeline/TimelineScreen.kt
→ 同じモジュールなので、マージ時にコンフリクト発生の可能性
```

**移行後:**
```
開発者A: feature/camera/
開発者B: feature/timeline/
→ 完全に独立したモジュールなので、コンフリクトなし
```

### 3. テストの容易化

**現在:**
```kotlin
// composeAppの全依存をモックする必要あり
@Test
fun `スプラッシュ画面のテスト`() {
    // すべての依存をセットアップ...
}
```

**移行後:**
```kotlin
// feature:onboardingのみのテスト
@Test
fun `スプラッシュ画面のテスト`() {
    // core:domainのみモック
}
```

### 4. 新機能追加の容易化

**v2.0でミッション機能を追加する場合:**

```bash
# 新しいfeatureモジュールを作成するだけ
mkdir -p feature/mission/src/commonMain/kotlin
# 既存コードに影響なし！
```

---

## ⚠️ 注意点とリスク

### リスク1: 初期の学習コスト
**対策:**
- 詳細なドキュメント整備
- サンプルコード（feature:onboarding）を参考に

### リスク2: ビルド設定の複雑化
**対策:**
- Convention Pluginで共通設定を一元管理
- build.gradle.ktsのテンプレート提供

### リスク3: マイグレーション中の混乱
**対策:**
- フェーズごとにGitタグを作成
- 段階的に移行、各フェーズでテスト実行

---

## 🔄 次のステップ

### 1. レビューとフィードバック
- [ ] このドキュメントをレビュー
- [ ] 懸念事項や質問を収集
- [ ] 必要に応じて調整

### 2. Phase 1の準備
- [ ] core:commonのディレクトリ作成
- [ ] build.gradle.ktsの作成
- [ ] 既存コードの移行開始

### 3. 継続的な改善
- [ ] 各フェーズ完了後に振り返り
- [ ] ドキュメントの更新
- [ ] ベストプラクティスの共有

---

## 📚 参考資料

### Google公式ドキュメント
- [Now in Android - Modularization](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)
- [Guide to app architecture](https://developer.android.com/topic/architecture)
- [Modularization](https://developer.android.com/topic/modularization)

### ベストプラクティス
- [Gradle Build Performance](https://docs.gradle.org/current/userguide/performance.html)
- [Kotlin Multiplatform Best Practices](https://kotlinlang.org/docs/multiplatform-mobile-getting-started.html)

---

## 📝 まとめ

### 提案内容
- 2モジュール → 20モジュール（app 1 + feature 9 + core 10）
- 機能単位で明確に分離
- Feature → Coreの一方向依存

### 主なメリット
- ビルド時間 50-70%削減
- 並行開発の容易化
- テストの容易化
- スケーラビリティ向上

### 移行期間
- **8週間**で段階的に移行
- 既存機能に影響なし
- ロールバック可能

### リスク管理
- 詳細なドキュメント整備
- フェーズごとのテスト
- Gitタグによるロールバック対応

---

**この提案により、Yoinアプリは長期的に保守しやすく、スケーラブルなアーキテクチャへと進化します。**

---

**作成日**: 2024年12月
**作成者**: Claude Code
**最終更新**: 2024年12月
