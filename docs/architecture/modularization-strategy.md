# モジュール化戦略

## 概要

Yoinアプリを適切な粒度でfeatureモジュールに分割し、保守性・スケーラビリティ・ビルドパフォーマンスを向上させる設計。

## 設計原則

### 1. 単一責任の原則
- 各featureモジュールは単一の機能領域を担当
- 明確な境界を持つ

### 2. 依存関係の逆転
- Feature層はCore層に依存
- Core層はFeature層を知らない

### 3. 独立性
- Featureモジュール間は直接依存しない
- Core層を介して通信

### 4. テスタビリティ
- 各モジュールは独立してテスト可能
- モックやスタブを容易に注入可能

---

## モジュール構成

```
yoin/
├── app/                          # アプリケーションモジュール
│   └── src/
│       ├── commonMain/           # ナビゲーション統合、DI統合
│       ├── androidMain/          # Androidエントリーポイント
│       └── iosMain/              # iOSエントリーポイント
│
├── feature/                      # 機能モジュール
│   ├── onboarding/              # オンボーディング・スプラッシュ
│   ├── auth/                    # 認証
│   ├── home/                    # ホーム画面
│   ├── room/                    # ルーム管理
│   ├── camera/                  # カメラ・撮影
│   ├── timeline/                # タイムライン・写真閲覧
│   ├── map/                     # マップ表示
│   ├── profile/                 # プロフィール
│   └── settings/                # 設定
│
├── core/                         # コアモジュール
│   ├── ui/                      # 共通UIコンポーネント
│   ├── design/                  # デザインシステム
│   ├── data/                    # Repository実装、DataSource
│   ├── domain/                  # UseCase、Repository Interface
│   ├── network/                 # Ktor Client、Supabase Client
│   ├── database/                # SQLDelight
│   ├── analytics/               # Firebase Analytics
│   ├── notification/            # FCM
│   ├── camera/                  # カメラ共通ロジック
│   ├── image/                   # 画像処理
│   └── common/                  # 共通ユーティリティ
│
└── shared/                       # KMP共通ロジック（既存）
    └── src/
        ├── commonMain/
        ├── androidMain/
        └── iosMain/
```

---

## 依存関係グラフ

```
┌─────────────────────────────────────────────────────────────┐
│                        app                                  │
│                  (Navigation Integration)                   │
└────────────────────────────┬────────────────────────────────┘
                             │
          ┌──────────────────┴──────────────────┐
          │                                     │
          ▼                                     ▼
┌─────────────────────────┐         ┌─────────────────────────┐
│    Feature Modules      │         │     Core Modules        │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:onboarding│◄─┼─────────┼──┤   core:ui        │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:auth     │◄─┼─────────┼──┤   core:design    │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:home     │◄─┼─────────┼──┤   core:domain    │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:room     │◄─┼─────────┼──┤   core:data      │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:camera   │◄─┼─────────┼──┤   core:network   │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:timeline │◄─┼─────────┼──┤   core:database  │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:map      │◄─┼─────────┼──┤   core:image     │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:profile  │◄─┼─────────┼──┤   core:camera    │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
│                         │         │                         │
│  ┌──────────────────┐   │         │  ┌──────────────────┐   │
│  │ feature:settings │◄─┼─────────┼──┤  core:analytics  │   │
│  └──────────────────┘   │         │  └──────────────────┘   │
└─────────────────────────┘         │                         │
                                    │  ┌──────────────────┐   │
                                    │  │ core:notification│   │
                                    │  └──────────────────┘   │
                                    │                         │
                                    │  ┌──────────────────┐   │
                                    │  │   core:common    │   │
                                    │  └──────────────────┘   │
                                    └─────────────────────────┘

重要なルール:
1. Featureモジュール同士は直接依存しない
2. Feature → Coreの依存のみ許可
3. Core内の依存は最小限に
```

---

## Featureモジュール詳細

### feature:onboarding

**責務:**
- スプラッシュ画面
- アプリ初期化処理
- オンボーディングフロー（初回起動時）

**画面:**
- SplashScreen
- OnboardingScreen

**依存Core:**
- core:ui
- core:design
- core:domain (InitializeAppUseCase)

---

### feature:auth

**責務:**
- ユーザー認証
- ログイン・サインアップ
- ゲストモード

**画面:**
- LoginScreen
- SignUpScreen
- GuestModePromptScreen

**依存Core:**
- core:ui
- core:design
- core:domain (SignInUseCase, SignUpUseCase, SignInAsGuestUseCase)
- core:network (Supabase Auth)

---

### feature:home

**責務:**
- ルーム一覧表示（参加中・過去）
- ルーム検索・フィルター

**画面:**
- HomeScreen
- RoomListScreen

**依存Core:**
- core:ui
- core:design
- core:domain (GetRoomsUseCase, SearchRoomsUseCase)

---

### feature:room

**責務:**
- ルーム作成・編集
- メンバー招待（QR、リンク）
- ルーム設定

**画面:**
- CreateRoomScreen
- EditRoomScreen
- InviteScreen (QR、リンク生成)
- RoomSettingsScreen

**依存Core:**
- core:ui
- core:design
- core:domain (CreateRoomUseCase, UpdateRoomUseCase, InviteMemberUseCase)
- core:common (QRコード生成)

---

### feature:camera

**責務:**
- カメラUI
- 撮影機能
- 残り枚数表示
- フィルター適用
- 日付スタンプ
- 写真アップロード

**画面:**
- CameraScreen
- PhotoPreviewScreen

**依存Core:**
- core:ui
- core:design
- core:domain (TakePhotoUseCase, UploadPhotoUseCase)
- core:camera (カメラハードウェアアクセス)
- core:image (フィルター、スタンプ)

---

### feature:timeline

**責務:**
- タイムライン表示（時系列）
- 写真詳細表示
- 撮影者・位置情報表示
- ダウンロード機能
- 共有機能

**画面:**
- TimelineScreen
- PhotoDetailScreen

**依存Core:**
- core:ui
- core:design
- core:domain (GetPhotosUseCase, DownloadPhotoUseCase)
- core:image (画像表示最適化)

---

### feature:map

**責務:**
- マップビュー
- 写真ピン表示
- ピンタップ・サムネイル表示

**画面:**
- MapScreen

**依存Core:**
- core:ui
- core:design
- core:domain (GetPhotosWithLocationUseCase)
- core:common (地図ライブラリ統合)

---

### feature:profile

**責務:**
- ユーザープロフィール表示
- プロフィール編集
- 統計情報（撮影枚数など）

**画面:**
- ProfileScreen
- EditProfileScreen

**依存Core:**
- core:ui
- core:design
- core:domain (GetUserProfileUseCase, UpdateProfileUseCase)
- core:image (アバター画像処理)

---

### feature:settings

**責務:**
- 設定画面
- アカウント設定
- 通知設定
- プライバシー設定
- ログアウト

**画面:**
- SettingsScreen
- AccountSettingsScreen
- NotificationSettingsScreen
- PrivacySettingsScreen

**依存Core:**
- core:ui
- core:design
- core:domain (UpdateSettingsUseCase, LogoutUseCase)
- core:notification (通知設定管理)

---

## Coreモジュール詳細

### core:ui

**責務:**
- 共通UIコンポーネント
- レイアウトコンポーネント
- Previewアノテーション

**提供:**
- Button, TextField, Card, Dialog, etc.
- ScreenLayout, ListItem, etc.
- @PhonePreview, @ComprehensivePreview, etc.

**依存:**
- core:design

---

### core:design

**責務:**
- デザインシステム
- テーマ（Color, Typography, Shape）
- デザイントークン

**提供:**
- YoinTheme
- Color Palette
- Typography
- Spacing, Elevation

**依存:**
- なし

---

### core:domain

**責務:**
- ビジネスロジック（UseCase）
- Repository Interface
- Domain Models

**提供:**
- UseCases (ビジネスロジック)
- Repository Interfaces
- Domain Models (User, Room, Photo, etc.)

**依存:**
- core:common

**注意:**
- データ層に依存しない（Dependency Inversion）
- プラットフォーム非依存

---

### core:data

**責務:**
- Repository実装
- DataSource実装
- DTO ↔ Domain Model変換

**提供:**
- Repository Implementations
- RemoteDataSource, LocalDataSource
- Mapper (DTO ↔ Domain Model)

**依存:**
- core:domain
- core:network
- core:database
- core:common

---

### core:network

**責務:**
- Ktor Client設定
- Supabase Client設定
- API呼び出しラッパー

**提供:**
- SupabaseClient
- KtorClient
- API Endpoints

**依存:**
- core:common

---

### core:database

**責務:**
- SQLDelight設定
- ローカルDB操作
- キャッシュ管理

**提供:**
- Database Queries
- Cache Management

**依存:**
- core:domain (Domain Models)

---

### core:analytics

**責務:**
- Firebase Analytics統合
- イベントトラッキング
- ユーザープロパティ設定

**提供:**
- AnalyticsLogger
- Event Definitions

**依存:**
- core:common

---

### core:notification

**責務:**
- FCM統合
- プッシュ通知処理
- 通知設定管理

**提供:**
- NotificationManager
- FCM Token Management

**依存:**
- core:common

---

### core:camera

**責務:**
- カメラハードウェアアクセス
- プラットフォーム固有のカメラ実装

**提供:**
- expect/actual Camera API
- CameraX (Android)
- AVFoundation (iOS)

**依存:**
- core:common

---

### core:image

**責務:**
- 画像処理
- フィルター適用
- リサイズ・圧縮
- 日付スタンプ

**提供:**
- ImageProcessor
- Filter Implementations
- Image Utils

**依存:**
- core:common

---

### core:common

**責務:**
- 共通ユーティリティ
- Extension Functions
- 定数定義

**提供:**
- Result型
- Extension Functions
- Constants
- Logger

**依存:**
- なし

---

## モジュール間通信

### Feature間の通信方法

Featureモジュール間は直接依存しないため、以下の方法で通信：

#### 1. Navigation引数
```kotlin
// feature:home から feature:room へ遷移
navController.navigate("room/create")
```

#### 2. SharedViewModel（appモジュール）
```kotlin
// app/src/commonMain/kotlin/navigation/SharedState.kt
class SharedNavigationState {
    val selectedRoomId = MutableStateFlow<String?>(null)
}
```

#### 3. Deep Link
```kotlin
// URIベースのナビゲーション
navController.navigate("yoin://room/{roomId}")
```

---

## ビルドパフォーマンス最適化

### 並列ビルド

```kotlin
// gradle.properties
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
```

### モジュール分離のメリット

1. **変更の影響範囲が限定**
   - feature:cameraを変更しても、他のfeatureは再ビルド不要

2. **並列ビルド**
   - 独立したfeatureモジュールは並列でビルド可能

3. **インクリメンタルビルド**
   - 変更したモジュールのみ再ビルド

---

## マイグレーション計画

現在の構成から新しい構成への移行手順は、別ドキュメント [migration-plan.md](./migration-plan.md) を参照。

---

## 参考資料

- [Now in Android - Modularization](https://github.com/android/nowinandroid/blob/main/docs/ModularizationLearningJourney.md)
- [Guide to app architecture - Android](https://developer.android.com/topic/architecture)
- [Modularization - Android](https://developer.android.com/topic/modularization)

---

**作成日**: 2024年12月
**最終更新**: 2024年12月
