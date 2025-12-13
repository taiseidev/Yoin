# Yoin（余韻） - 旅の写真を「現像」で楽しむアプリ

<div align="center">

**旅行中の写真を「見えない状態」で撮影し、旅行後の翌朝に現像される体験**

フィルムカメラの「待つ楽しさ」× 旅仲間との「思い出共有」

</div>

---

## 📱 プロジェクト概要

**Yoin（余韻）** は、旅行の写真撮影にフィルムカメラのような「制限」と「待つ楽しさ」を取り入れ、旅仲間と思い出を共有できるアプリです。

### コンセプト

- **撮影制限**: 1日24枚まで（フィルムカメラのような制約）
- **撮り直し不可**: 一度シャッターを切ったら撮り直せない緊張感
- **現像待ち**: 旅行最終日の翌朝9時に一括現像
- **グループ共有**: 旅仲間全員の写真がタイムラインに表示
- **位置情報**: マップ上で撮影場所を可視化

### 差別化ポイント

| 既存サービス | Yoin の強み |
|---|---|
| Googleフォト / LINEアルバム | 汎用的すぎる → **旅行専用で体験が特化** |
| Instagram | 公開前提 → **クローズドで気軽** |
| BeReal | 日常向け → **旅行特化** |
| Dispo / Gudak | 共有機能が弱い → **グループ共有が核** |
| 旅行記録アプリ | カメラ体験がない → **フィルム体験あり** |

---

## 🛠 技術スタック

| カテゴリ | 技術 | 備考 |
|---|---|---|
| **UI Framework** | Compose Multiplatform | Android/iOS 対応 |
| **共通ロジック** | Kotlin Multiplatform | ビジネスロジック共通化 |
| **データベース** | Supabase (PostgreSQL) | マネージドDB、RLS対応 |
| **認証** | Supabase Auth | Apple/Google/ゲスト対応 |
| **ストレージ** | Supabase Storage | 画像保存、CDN配信 |
| **サーバー処理** | Supabase Edge Functions | 定期処理、通知送信 |
| **定期実行** | Supabase pg_cron | 現像処理のスケジュール |
| **プッシュ通知** | Firebase Cloud Messaging | Android/iOS 両対応 |
| **アナリティクス** | Firebase Analytics | ユーザー行動分析 |
| **クラッシュレポート** | Firebase Crashlytics | エラー追跡 |
| **課金** | RevenueCat | サブスク管理 |
| **決済（Shop）** | Stripe | 物販決済 |

### 主要ライブラリ

- **DI**: Koin
- **通信**: Ktor Client
- **JSON**: Kotlinx Serialization
- **画像**: Coil3
- **ローカルDB**: SQLDelight
- **日時**: kotlinx-datetime

---

## 🏗 プロジェクト構造

```
yoin/
├── composeApp/              # Compose Multiplatform UI
│   ├── commonMain/          # 共通UI
│   ├── androidMain/         # Android固有UI
│   └── iosMain/             # iOS固有UI
│
├── shared/                  # KMPビジネスロジック
│   ├── commonMain/
│   │   ├── domain/          # ドメインモデル、UseCase
│   │   ├── data/            # Repository実装、DataSource
│   │   └── di/              # DI設定
│   ├── androidMain/         # Android固有実装（CameraX等）
│   └── iosMain/             # iOS固有実装（AVFoundation等）
│
├── iosApp/                  # iOSアプリエントリーポイント
│
├── docs/                    # ドキュメント
│   ├── architecture.md      # アーキテクチャ
│   ├── database-schema.md   # DBスキーマ
│   ├── features.md          # 機能仕様
│   └── ...
│
├── supabase/               # Supabaseリソース
│   ├── migrations/         # DBマイグレーション
│   └── functions/          # Edge Functions
│
└── ADR/                    # Architecture Decision Records
```

---

## 🚀 セットアップ

### 必要なツール

| ツール | バージョン | 用途 |
|---|---|---|
| Android Studio | Hedgehog 以降 | IDE |
| Xcode | 15 以降 | iOS開発 |
| JDK | 17 以上 | Kotlin |
| Cocoapods | 最新 | iOS依存管理 |

### 環境構築手順

1. **リポジトリのクローン**
   ```bash
   git clone <repository-url>
   cd YoinApp
   ```

2. **環境変数の設定**
   ```bash
   cp .env.example .env.local
   # .env.local を編集して、各種APIキーを設定
   ```

3. **依存関係のインストール**
   ```bash
   # Android
   ./gradlew build

   # iOS
   cd iosApp
   pod install
   cd ..
   ```

4. **Supabaseのセットアップ**
   - Supabaseプロジェクトを作成
   - `supabase/migrations/` のSQLを実行
   - `.env.local` にSupabase URLとキーを設定

5. **Firebaseのセットアップ**
   - Firebaseプロジェクトを作成
   - `google-services.json` を `composeApp/` に配置 (Android)
   - `GoogleService-Info.plist` を `iosApp/` に配置 (iOS)

### ビルド・実行

#### Android
```bash
# デバッグビルド
./gradlew :composeApp:assembleDebug

# 実行
./gradlew :composeApp:installDebug
```

#### iOS
```bash
# Xcodeで開く
cd iosApp
open iosApp.xcworkspace

# または、Xcode から実行
```

---

## 📚 ドキュメント

詳細なドキュメントは `docs/` ディレクトリを参照してください。

- [アーキテクチャ設計](docs/architecture.md)
- [データベーススキーマ](docs/database-schema.md)
- [機能仕様書](docs/features.md)
- [API連携ガイド](docs/api-integration.md)
- [開発ガイドライン](docs/development-guide.md)
- [コーディング規約](docs/coding-standards.md)

また、プロジェクトの詳細なドキュメントは以下にも保存されています：
- [Obsidianノート](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -)

---

## 🎯 開発フェーズ

### MVP（v1.0）機能

- ✅ 旅行ルーム作成・招待
- ✅ 撮影（1日24枚制限、撮り直し不可）
- ✅ 自動フィルター適用
- ✅ AIフィルタリング（不適切コンテンツ検出）
- ✅ 現像待ち（旅行最終日の翌朝9時）
- ✅ 共有タイムライン
- ✅ マップ表示
- ✅ 写真ダウンロード
- ✅ ゲストモード（5枚まで撮影可能）

### v2.0（予定）

- ミッション機能（撮影お題）
- ベストショット投票
- 各種アワード
- 現像タイミング選択
- フィルター選択
- ルート表示
- フォトブック連携

---

## 🧪 テスト

### テスト実行

```bash
# 全テスト
./gradlew allTests

# Android のみ
./gradlew :shared:testDebugUnitTest

# iOS のみ
./gradlew :shared:iosSimulatorArm64Test
```

### テスト戦略

| 種類 | 対象 | ツール |
|---|---|---|
| Unit Test | UseCase, Repository | kotlin.test, MockK |
| UI Test | Compose UI | Compose UI Testing |
| Integration Test | Supabase連携 | Supabase Local |

---

## 📝 コントリビューション

開発に参加する際は、[CONTRIBUTING.md](CONTRIBUTING.md) を参照してください。

### ブランチ戦略

- `main`: 本番環境
- `develop`: 開発環境
- `feature/*`: 機能開発

### コミットメッセージ規約

```
feat: 新機能追加
fix: バグ修正
docs: ドキュメント更新
style: コードフォーマット
refactor: リファクタリング
test: テスト追加・修正
chore: ビルド設定など
```

---

## 📄 ライセンス

このプロジェクトは個人開発プロジェクトです。

---

## 📮 お問い合わせ

- Twitter: [@yoinapp](https://twitter.com/yoinapp)
- Instagram: [@yoin.app](https://instagram.com/yoin.app)

---

**作成日**: 2024年12月
**目標リリース**: 2025年Q2
