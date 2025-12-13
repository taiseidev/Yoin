# 改善版モジュール化戦略

## 概要

domain, data層を独立したモジュールとして分離し、より明確な責任分離とテスタビリティを実現する設計。

---

## 改善された構成

```
yoin/
├── app/                          # アプリケーションモジュール
│
├── feature/                      # 機能モジュール（UI層）
│   ├── onboarding/              # オンボーディング
│   ├── auth/                    # 認証
│   ├── home/                    # ホーム画面
│   ├── room/                    # ルーム管理
│   ├── camera/                  # カメラ・撮影
│   ├── timeline/                # タイムライン
│   ├── map/                     # マップ
│   ├── profile/                 # プロフィール
│   └── settings/                # 設定
│
├── domain/                       # ドメイン層（機能ごとに分離）
│   ├── auth/                    # 認証ドメイン
│   ├── room/                    # ルームドメイン
│   ├── photo/                   # 写真ドメイン
│   ├── user/                    # ユーザードメイン
│   └── common/                  # 共通ドメイン
│
├── data/                         # データ層（機能ごとに分離）
│   ├── auth/                    # 認証データソース
│   ├── room/                    # ルームデータソース
│   ├── photo/                   # 写真データソース
│   ├── user/                    # ユーザーデータソース
│   └── local/                   # ローカルDB
│
└── core/                         # コアモジュール（インフラ層）
    ├── ui/                      # 共通UI
    ├── design/                  # デザインシステム
    ├── network/                 # Ktor, Supabase
    ├── database/                # SQLDelight
    ├── analytics/               # Analytics
    ├── notification/            # FCM
    ├── camera/                  # カメラHW
    ├── image/                   # 画像処理
    └── common/                  # 共通ユーティリティ
```

---

## モジュール数

- **app**: 1モジュール
- **feature**: 9モジュール
- **domain**: 5モジュール（機能ごと）
- **data**: 5モジュール（機能ごと）
- **core**: 9モジュール（インフラ）

**合計: 29モジュール**

---

## 依存関係グラフ

```
                    app
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
   feature:*    feature:*    feature:*
        │            │            │
        └────────────┼────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
   domain:auth  domain:room  domain:photo
        │            │            │
        └────────────┼────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
    data:auth    data:room    data:photo
        │            │            │
        └────────────┼────────────┘
                     │
        ┌────────────┼────────────┐
        │            │            │
        ▼            ▼            ▼
  core:network core:database core:image
```

### 依存ルール

1. **feature → domain → data → core**（一方向）
2. feature同士は依存しない
3. domain同士は依存しない（domain:commonは除く）
4. data同士は依存しない（data:localは除く）

---

## Domainモジュール詳細

### domain:auth
**責務:** 認証ドメインロジック

**提供:**
- Models: `User`, `AuthToken`, `GuestUser`
- Repository Interface: `AuthRepository`
- UseCases:
  - `SignInWithAppleUseCase`
  - `SignInWithGoogleUseCase`
  - `SignInAsGuestUseCase`
  - `SignOutUseCase`
  - `GetCurrentUserUseCase`

**依存:**
- domain:common

---

### domain:room
**責務:** ルーム管理ドメインロジック

**提供:**
- Models: `Room`, `RoomMember`, `RoomSettings`
- Repository Interface: `RoomRepository`
- UseCases:
  - `CreateRoomUseCase`
  - `GetRoomsUseCase`
  - `UpdateRoomUseCase`
  - `InviteMemberUseCase`
  - `JoinRoomUseCase`

**依存:**
- domain:common
- domain:user（User参照のため）

---

### domain:photo
**責務:** 写真ドメインロジック

**提供:**
- Models: `Photo`, `Filter`, `DevelopmentStatus`
- Repository Interface: `PhotoRepository`
- UseCases:
  - `TakePhotoUseCase`
  - `UploadPhotoUseCase`
  - `GetPhotosUseCase`
  - `DownloadPhotoUseCase`
  - `DevelopPhotosUseCase`

**依存:**
- domain:common
- domain:room（Room参照のため）

---

### domain:user
**責務:** ユーザープロフィールドメインロジック

**提供:**
- Models: `UserProfile`, `UserSettings`, `UserStatistics`
- Repository Interface: `UserRepository`
- UseCases:
  - `GetUserProfileUseCase`
  - `UpdateProfileUseCase`
  - `GetUserStatisticsUseCase`

**依存:**
- domain:common

---

### domain:common
**責務:** 共通ドメイン定義

**提供:**
- Result型
- DomainException
- 共通Value Objects
- 共通インターフェース

**依存:**
- なし

---

## Dataモジュール詳細

### data:auth
**責務:** 認証データアクセス

**提供:**
- Repository実装: `AuthRepositoryImpl`
- DataSource:
  - `AuthRemoteDataSource`（Supabase Auth）
  - `AuthLocalDataSource`（Token保存）
- DTO: `UserDto`, `AuthTokenDto`
- Mapper: `UserMapper`

**依存:**
- domain:auth
- core:network
- data:local

---

### data:room
**責務:** ルームデータアクセス

**提供:**
- Repository実装: `RoomRepositoryImpl`
- DataSource:
  - `RoomRemoteDataSource`（Supabase DB）
  - `RoomLocalDataSource`（キャッシュ）
- DTO: `RoomDto`, `RoomMemberDto`
- Mapper: `RoomMapper`

**依存:**
- domain:room
- core:network
- data:local

---

### data:photo
**責務:** 写真データアクセス

**提供:**
- Repository実装: `PhotoRepositoryImpl`
- DataSource:
  - `PhotoRemoteDataSource`（Supabase Storage）
  - `PhotoLocalDataSource`（キャッシュ）
- DTO: `PhotoDto`
- Mapper: `PhotoMapper`

**依存:**
- domain:photo
- core:network
- core:image
- data:local

---

### data:user
**責務:** ユーザーデータアクセス

**提供:**
- Repository実装: `UserRepositoryImpl`
- DataSource:
  - `UserRemoteDataSource`（Supabase DB）
  - `UserLocalDataSource`（キャッシュ）
- DTO: `UserProfileDto`
- Mapper: `UserProfileMapper`

**依存:**
- domain:user
- core:network
- data:local

---

### data:local
**責務:** ローカルデータベース

**提供:**
- SQLDelight Database
- Queries
- Cache Management

**依存:**
- core:database
- core:common

---

## Coreモジュール詳細

### core:ui
**責務:** 共通UIコンポーネント

**依存:**
- core:design

---

### core:design
**責務:** デザインシステム

**依存:**
- なし

---

### core:network
**責務:** ネットワーク層

**提供:**
- SupabaseClient
- KtorClient

**依存:**
- core:common

---

### core:database
**責務:** SQLDelight設定

**依存:**
- core:common

---

### core:analytics
**責務:** Firebase Analytics

**依存:**
- core:common

---

### core:notification
**責務:** FCM

**依存:**
- core:common

---

### core:camera
**責務:** カメラハードウェア

**依存:**
- core:common

---

### core:image
**責務:** 画像処理

**依存:**
- core:common

---

### core:common
**責務:** 共通ユーティリティ

**依存:**
- なし

---

## Featureモジュール詳細

### feature:auth
**責務:** 認証UI

**依存:**
- core:ui
- core:design
- domain:auth

---

### feature:home
**責務:** ホームUI

**依存:**
- core:ui
- core:design
- domain:room

---

### feature:camera
**責務:** カメラUI

**依存:**
- core:ui
- core:design
- domain:photo
- core:camera

---

## メリット

### 1. 明確な責任分離
- **Feature**: UI・ナビゲーション
- **Domain**: ビジネスロジック
- **Data**: データアクセス
- **Core**: インフラストラクチャ

### 2. テスタビリティ向上
```kotlin
// domain:authのテスト（data層に依存しない）
class SignInUseCaseTest {
    @Test
    fun `正常にサインインできる`() {
        val mockRepository = mockk<AuthRepository>()
        val useCase = SignInUseCase(mockRepository)
        // ...
    }
}
```

### 3. 並行開発の容易化
- 認証機能: `domain:auth` + `data:auth` + `feature:auth`
- ルーム機能: `domain:room` + `data:room` + `feature:room`
- → 完全に独立して開発可能

### 4. ビルドパフォーマンス
- domain層の変更: feature層のみ再ビルド
- data層の変更: domain層・feature層は再ビルド不要

---

## 比較: 旧構成 vs 改善版

### 旧構成（提案1）
```
core:domain     ← すべてのドメインロジック
core:data       ← すべてのデータアクセス
```
**問題:**
- 1つのモジュールが肥大化
- 機能追加時に影響範囲が広い

### 改善版
```
domain:auth, domain:room, domain:photo, domain:user
data:auth, data:room, data:photo, data:user
```
**メリット:**
- 機能ごとに分離
- 影響範囲が限定
- テストが容易

---

## ディレクトリ構造例

### domain:auth
```
domain/auth/
├── build.gradle.kts
└── src/
    └── commonMain/
        └── kotlin/
            └── com/yoin/domain/auth/
                ├── model/
                │   ├── User.kt
                │   ├── AuthToken.kt
                │   └── GuestUser.kt
                ├── repository/
                │   └── AuthRepository.kt
                └── usecase/
                    ├── SignInUseCase.kt
                    ├── SignOutUseCase.kt
                    └── GetCurrentUserUseCase.kt
```

### data:auth
```
data/auth/
├── build.gradle.kts
└── src/
    └── commonMain/
        └── kotlin/
            └── com/yoin/data/auth/
                ├── repository/
                │   └── AuthRepositoryImpl.kt
                ├── datasource/
                │   ├── AuthRemoteDataSource.kt
                │   └── AuthLocalDataSource.kt
                ├── dto/
                │   ├── UserDto.kt
                │   └── AuthTokenDto.kt
                └── mapper/
                    └── UserMapper.kt
```

---

**作成日**: 2024年12月
**最終更新**: 2024年12月
