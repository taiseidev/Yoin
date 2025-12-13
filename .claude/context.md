# Yoin（余韻） - プロジェクトコンテキスト

このファイルは、AIエージェント（Claude Code）がYoinプロジェクトを理解するための統合コンテキストです。

---

## プロジェクト概要

### アプリ名
**Yoin（余韻）** - 旅の写真を「現像」で楽しむアプリ

### コンセプト
旅行中の写真を「見えない状態」で撮影し、旅行後の翌朝に現像される体験を提供します。フィルムカメラの「待つ楽しさ」と旅仲間との「思い出共有」を組み合わせたアプリです。

### 核となる機能
1. **撮影制限**: 1日24枚まで（撮り直し不可）
2. **現像待ち**: 旅行最終日の翌朝9時に一括現像
3. **グループ共有**: 旅仲間全員の写真がタイムラインに表示
4. **位置情報**: マップ上で撮影場所を可視化
5. **ゲストモード**: 未登録でも5枚まで撮影可能

---

## 技術スタック

### フロントエンド
- **UI Framework**: Compose Multiplatform
- **言語**: Kotlin Multiplatform
- **アーキテクチャ**: MVI + Clean Architecture
- **DI**: Koin
- **画像処理**: GPUImage (Android) / CoreImage (iOS)

### バックエンド
- **データベース**: Supabase (PostgreSQL)
- **認証**: Supabase Auth (Apple/Google/匿名)
- **ストレージ**: Supabase Storage
- **サーバー処理**: Supabase Edge Functions + pg_cron
- **通知**: Firebase Cloud Messaging
- **課金**: RevenueCat
- **決済**: Stripe

### ライブラリ
- Ktor Client（通信）
- Kotlinx Serialization（JSON）
- Coil3（画像読み込み）
- SQLDelight（ローカルDB）
- kotlinx-datetime（日時処理）

---

## プロジェクト構造

```
YoinApp/
├── composeApp/              # UI層（Compose Multiplatform）
│   ├── commonMain/          # 共通UI
│   ├── androidMain/         # Android固有UI
│   └── iosMain/             # iOS固有UI
├── shared/                  # ビジネスロジック層（KMP）
│   ├── commonMain/
│   │   ├── domain/          # ドメインモデル、UseCase
│   │   ├── data/            # Repository実装、DataSource
│   │   └── di/              # DI設定
│   ├── androidMain/         # Android固有実装
│   └── iosMain/             # iOS固有実装
├── iosApp/                  # iOSエントリーポイント
├── docs/                    # ドキュメント
├── supabase/               # Supabaseリソース
│   ├── migrations/         # DBマイグレーション
│   └── functions/          # Edge Functions
└── ADR/                    # Architecture Decision Records
```

---

## アーキテクチャ

### レイヤー構成

```
Presentation (UI, ViewModel, MVI)
      ↓
Domain (UseCase, Repository Interface, Model)
      ↓
Data (Repository Impl, DataSource, DTO)
```

### 依存関係の方向
- Presentation → Domain ← Data
- Domain層は他のレイヤーに依存しない（Dependency Inversion）

### 状態管理
- **MVI Pattern**: Intent → State の単方向データフロー
- **StateFlow**: UIの状態を保持
- **SharedFlow**: 一時的なイベント（トースト表示など）

---

## 主要なドメインモデル

### User（ユーザー）
```kotlin
data class User(
    val id: String,
    val email: String?,
    val displayName: String,
    val plan: UserPlan,  // guest, free, premium
    val isGuest: Boolean
)
```

### Room（ルーム）
```kotlin
data class Room(
    val id: String,
    val name: String,
    val destination: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val status: RoomStatus,  // upcoming, active, pending_development, developed
    val developmentScheduledAt: Instant?,
    val ownerId: String
)
```

### Photo（写真）
```kotlin
data class Photo(
    val id: String,
    val roomId: String,
    val userId: String,
    val storagePath: String,
    val latitude: Double?,
    val longitude: Double?,
    val isVisible: Boolean,  // 現像済みか
    val takenAt: Instant
)
```

---

## 重要な制約・ビジネスルール

### 撮影制限
- **ゲストユーザー**: ルーム全体で5枚まで
- **無料ユーザー**: 1日24枚まで
- **プレミアムユーザー**: 1日36枚まで
- **撮り直し**: 完全不可（シャッターを切ったら削除できない）

### ルーム作成制限
- **ゲストユーザー**: 作成不可
- **無料ユーザー**: 月1回まで
- **プレミアムユーザー**: 無制限

### 写真保存期間
- **無料ユーザー**: 3ヶ月
- **プレミアムユーザー**: 無制限

### 現像タイミング
- **MVP**: 旅行最終日の翌朝9時（固定）
- **v2.0**: ユーザーが選択可能（即時/翌朝/カスタム）

### AIフィルタリング
- 不適切コンテンツ（NSFW）→ 自動削除 + 枚数返却
- 真っ暗/真っ白/極度のブレ → 自動返却 + 通知
- 軽微なブレ/指の写り込み → そのまま保存（フィルムの味）

---

## データベース設計

### 主要テーブル
- **users**: ユーザー情報
- **rooms**: ルーム（旅行）
- **room_members**: ルームメンバー
- **photos**: 写真
- **daily_photo_counts**: 日別撮影数カウント
- **guest_photo_counts**: ゲスト撮影数カウント
- **subscriptions**: サブスクリプション
- **orders**: Shop注文

### Row Level Security (RLS)
- ルームメンバーのみ、そのルームの写真を閲覧可能
- ユーザーは自分のデータのみ更新可能
- すべてのアクセスはSupabase AuthのJWTで認証

詳細: [docs/database-schema.md](docs/database-schema.md)

---

## 開発フェーズ

### 現在のステータス
- ✅ 企画・設計完了
- ✅ ドキュメント整備中
- ⏳ 開発環境構築中
- ⏳ MVP開発準備中

### MVP（v1.0）機能
- ルーム管理（作成・招待・参加）
- カメラ・撮影（制限付き）
- AIフィルタリング
- 現像機能（翌朝9時固定）
- タイムライン・マップ表示
- 写真ダウンロード
- ゲストモード

### v2.0以降
- ミッション機能
- ベストショット投票
- アワード
- 現像タイミング選択
- フィルター選択
- フォトブック連携

---

## 環境変数

必要な環境変数（`.env.local`）:

```bash
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=xxx
FIREBASE_PROJECT_ID=xxx
REVENUECAT_API_KEY_ANDROID=goog_xxx
REVENUECAT_API_KEY_IOS=appl_xxx
STRIPE_PUBLISHABLE_KEY=pk_test_xxx
GOOGLE_MAPS_API_KEY=AIzaSyXXX
```

---

## コーディング規約

### 命名規則
- クラス・インターフェース: `PascalCase`
- 関数・変数: `camelCase`
- 定数: `UPPER_SNAKE_CASE`
- プライベートStateFlow: `_camelCase`

### Composable関数
- `PascalCase`
- Modifierは第一引数
- State Hoisting を基本とする

### アーキテクチャ
- MVI Pattern（Intent → State）
- Clean Architecture（Presentation → Domain ← Data）
- Repository Pattern（Domain定義、Data実装）
- UseCase パターン（1つの責務）

詳細: [docs/coding-standards.md](docs/coding-standards.md)

---

## よくある質問

### Q: 新しい画面を追加する際の手順は？

A:
1. `domain/` に必要なモデル・UseCaseを定義
2. `data/` にRepositoryを実装
3. `composeApp/commonMain/ui/screens/` に画面を作成
4. ViewModelを作成し、MVIパターンで状態管理
5. `navigation/` にナビゲーションを追加

### Q: Supabaseとの通信でエラーが起きた場合は？

A:
1. `Result<T>` 型でラップして返す
2. Repository層でtry-catchし、エラーをハンドリング
3. ViewModel で Result.Success / Result.Error に応じてUIを更新
4. ローカルDBにキャッシュがあれば、それを表示

### Q: Android/iOS固有の処理はどこに書く？

A:
1. `shared/commonMain/` に `expect class` を定義
2. `shared/androidMain/` に `actual class` を実装（Android）
3. `shared/iosMain/` に `actual class` を実装（iOS）

例: カメラ、通知、画像処理など

### Q: テストはどのレベルで書く？

A:
- **Unit Test**: UseCase, Repository（70%）
- **Integration Test**: Supabase連携（20%）
- **UI Test**: Compose UI（10%）

---

## 関連ドキュメント

- [README.md](../README.md) - プロジェクト概要とセットアップ
- [docs/architecture.md](../docs/architecture.md) - アーキテクチャ詳細
- [docs/database-schema.md](../docs/database-schema.md) - DB設計
- [docs/features.md](../docs/features.md) - 機能仕様
- [docs/api-integration.md](../docs/api-integration.md) - API連携ガイド
- [docs/development-guide.md](../docs/development-guide.md) - 開発ガイド
- [docs/coding-standards.md](../docs/coding-standards.md) - コーディング規約

Obsidianノート:
- [技術構成](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/技術構成.md)
- [機能仕様書](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/機能仕様書.md)
- [タスク管理](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/タスク管理.md)

---

## AIエージェントへの指示

### コード生成時の注意点
1. **Clean Architectureを遵守**: Domain層を他のレイヤーに依存させない
2. **MVIパターンを使用**: Intent → ViewModel → State の流れ
3. **Kotlinらしいコードを書く**: データクラス、拡張関数、スコープ関数を活用
4. **エラーハンドリングを忘れない**: Result型でラップ、try-catchで安全に
5. **テストコードも作成**: 主要なUseCaseとRepositoryはテストを書く

### ファイル作成時の注意点
1. **適切なディレクトリに配置**: アーキテクチャ規約に従う
2. **パッケージ名を正しく**: `com.yoin.domain.model` など
3. **命名規則を守る**: PascalCase, camelCase を使い分け

### レビュー観点
- アーキテクチャに沿っているか
- ビジネスルールを守っているか
- パフォーマンスに問題ないか
- セキュリティリスクはないか

---

**最終更新**: 2024年12月
**プロジェクト開始**: 2024年12月
**目標リリース**: 2025年Q2
