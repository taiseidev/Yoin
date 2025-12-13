# データベース設計

## 概要

Yoin（余韻）アプリの完全なデータベース設計を定義する。

### 設計方針

| 項目 | 選定 | 備考 |
|---|---|---|
| データベース | Supabase（PostgreSQL） | マネージドDB、RLS対応 |
| 認証 | Supabase Auth | Apple/Google/匿名認証 |
| ストレージ | Supabase Storage | 写真・サムネイル保存 |
| 写真保存期間 | 無料: 3ヶ月 / プレミアム: 無制限 | expires_at で管理 |
| 撮影上限 | ゲスト: 5枚/ルーム, 無料: 24枚/日, プレミアム: 36枚/日 | |
| メンバー数 | 無制限 | - |

---

## テーブル一覧

### コアテーブル（MVP必須）

| # | テーブル名 | 説明 |
|---|---|---|
| 1 | users | ユーザー |
| 2 | user_preferences | ユーザー設定（ダークモード等） |
| 3 | rooms | ルーム |
| 4 | room_members | ルームメンバー |
| 5 | invite_codes | 招待コード |
| 6 | photos | 写真 |
| 7 | photo_downloads | 写真ダウンロード履歴 |
| 8 | daily_photo_counts | 日別撮影数 |
| 9 | guest_photo_counts | ゲスト撮影数（ルーム単位） |
| 10 | monthly_room_creations | 月間ルーム作成数 |
| 11 | filters | フィルター |
| 12 | stamp_settings | 日付スタンプ設定 |

### サブスクリプション

| # | テーブル名 | 説明 |
|---|---|---|
| 13 | subscriptions | サブスクリプション |
| 14 | subscription_history | サブスク変更履歴 |

### Shop・注文

| # | テーブル名 | 説明 |
|---|---|---|
| 15 | products | 商品 |
| 16 | product_variants | 商品バリアント |
| 17 | coupons | クーポン |
| 18 | user_coupons | ユーザークーポン |
| 19 | orders | 注文 |
| 20 | order_items | 注文アイテム |
| 21 | order_item_photos | 注文アイテム写真 |
| 22 | shipping_addresses | 配送先住所 |

### 通知

| # | テーブル名 | 説明 |
|---|---|---|
| 23 | notifications | 通知 |
| 24 | notification_settings | 通知設定 |
| 25 | fcm_tokens | FCMトークン |
| 26 | scheduled_notifications | 予約通知（リマインド等） |

### サポート

| # | テーブル名 | 説明 |
|---|---|---|
| 27 | contact_messages | お問い合わせ |
| 28 | faq_categories | FAQカテゴリ |
| 29 | faq_items | FAQ項目 |

### 統計・ログ

| # | テーブル名 | 説明 |
|---|---|---|
| 30 | user_room_stats | ユーザールーム統計 |
| 31 | app_events | アプリイベントログ |

---

## ER図

```
                            ┌─────────────────┐
                            │     users       │
                            └────────┬────────┘
                                     │
         ┌───────────┬───────────┬───┴───┬───────────┬───────────┐
         │           │           │       │           │           │
         ▼           ▼           ▼       ▼           ▼           ▼
┌─────────────┐┌───────────┐┌────────┐┌────────┐┌──────────┐┌──────────┐
│user_        ││fcm_tokens ││rooms   ││photos  ││orders    ││subscript-│
│preferences  ││           ││        ││        ││          ││ions      │
└─────────────┘└───────────┘└───┬────┘└────────┘└──────────┘└──────────┘
                                │
              ┌─────────────────┼─────────────────┐
              │                 │                 │
              ▼                 ▼                 ▼
      ┌─────────────┐   ┌─────────────┐   ┌─────────────┐
      │room_members │   │invite_codes │   │daily_photo_ │
      │             │   │             │   │counts       │
      └─────────────┘   └─────────────┘   └─────────────┘
```

---

## 主要テーブル定義

### 1. users（ユーザー）

| カラム | 型 | NULL | デフォルト | 説明 |
|---|---|:-:|---|---|
| id | UUID | NO | gen_random_uuid() | 主キー |
| firebase_uid | VARCHAR(128) | YES | - | Firebase Auth UID（ゲストはNULL） |
| email | VARCHAR(255) | YES | - | メールアドレス（ゲストはNULL） |
| display_name | VARCHAR(100) | NO | - | 表示名 |
| avatar_url | TEXT | YES | - | アバター画像URL |
| plan | VARCHAR(20) | NO | 'free' | プラン（guest/free/premium） |
| is_guest | BOOLEAN | NO | FALSE | ゲストユーザーフラグ |
| guest_converted_at | TIMESTAMPTZ | YES | - | ゲストから登録に変換した日時 |
| created_at | TIMESTAMPTZ | NO | NOW() | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | NOW() | 更新日時 |

**plan 値:**
- `guest`: ゲストユーザー（撮影5枚/ルーム、DL不可）
- `free`: 無料プラン（撮影24枚/日、月1ルーム作成、3ヶ月保存）
- `premium`: プレミアム（撮影36枚/日、無制限作成、永久保存）

---

### 3. rooms（ルーム）

| カラム | 型 | NULL | デフォルト | 説明 |
|---|---|:-:|---|---|
| id | UUID | NO | gen_random_uuid() | 主キー |
| name | VARCHAR(100) | NO | - | 旅行名 |
| destination | VARCHAR(100) | YES | - | 目的地 |
| icon_emoji | VARCHAR(10) | YES | '🏔' | ルームアイコン絵文字 |
| start_date | DATE | NO | - | 旅行開始日 |
| end_date | DATE | NO | - | 旅行終了日 |
| status | VARCHAR(20) | NO | 'upcoming' | ステータス |
| development_scheduled_at | TIMESTAMPTZ | YES | - | 現像予定日時 |
| developed_at | TIMESTAMPTZ | YES | - | 実際の現像日時 |
| total_photos | INTEGER | NO | 0 | 総写真枚数（キャッシュ） |
| owner_id | UUID | NO | - | 作成者（FK: users.id） |
| created_at | TIMESTAMPTZ | NO | NOW() | 作成日時 |
| updated_at | TIMESTAMPTZ | NO | NOW() | 更新日時 |

**status 値:**
- `upcoming`: 旅行開始前
- `active`: 旅行中（撮影可能）
- `pending_development`: 旅行終了、現像待ち
- `developed`: 現像済み
- `archived`: アーカイブ済み

---

### 6. photos（写真）

| カラム | 型 | NULL | デフォルト | 説明 |
|---|---|:-:|---|---|
| id | UUID | NO | gen_random_uuid() | 主キー |
| room_id | UUID | NO | - | FK: rooms.id |
| user_id | UUID | NO | - | FK: users.id |
| storage_path | TEXT | NO | - | オリジナル画像パス |
| storage_path_hq | TEXT | YES | - | 高画質画像パス |
| thumbnail_path | TEXT | YES | - | サムネイル画像パス |
| status | VARCHAR(20) | NO | 'pending' | ステータス |
| filter_id | UUID | YES | - | 適用フィルター（FK: filters.id） |
| has_date_stamp | BOOLEAN | NO | TRUE | 日付スタンプあり |
| latitude | DECIMAL(10, 8) | YES | - | 緯度 |
| longitude | DECIMAL(11, 8) | YES | - | 経度 |
| location_name | VARCHAR(200) | YES | - | 場所名 |
| is_visible | BOOLEAN | NO | FALSE | 表示可能フラグ |
| taken_at | TIMESTAMPTZ | NO | - | 撮影日時 |
| expires_at | TIMESTAMPTZ | YES | - | 有効期限 |
| created_at | TIMESTAMPTZ | NO | NOW() | 作成日時 |

---

## Row Level Security (RLS) ポリシー

### users テーブル

```sql
-- ユーザーは自分のデータのみ閲覧可能
CREATE POLICY "Users can view own data"
ON users FOR SELECT
USING (auth.uid() = id);

-- ユーザーは自分のデータのみ更新可能
CREATE POLICY "Users can update own data"
ON users FOR UPDATE
USING (auth.uid() = id);
```

### rooms テーブル

```sql
-- ルームメンバーのみルームを閲覧可能
CREATE POLICY "Room members can view rooms"
ON rooms FOR SELECT
USING (
  id IN (
    SELECT room_id FROM room_members
    WHERE user_id = auth.uid() AND is_active = TRUE
  )
);

-- ルームオーナーのみルームを更新可能
CREATE POLICY "Room owners can update rooms"
ON rooms FOR UPDATE
USING (owner_id = auth.uid());
```

### photos テーブル

```sql
-- ルームメンバーのみ写真を閲覧可能
CREATE POLICY "Room members can view photos"
ON photos FOR SELECT
USING (
  room_id IN (
    SELECT room_id FROM room_members
    WHERE user_id = auth.uid() AND is_active = TRUE
  )
);

-- 写真のアップロードはルームメンバーのみ
CREATE POLICY "Room members can upload photos"
ON photos FOR INSERT
WITH CHECK (
  room_id IN (
    SELECT room_id FROM room_members
    WHERE user_id = auth.uid() AND is_active = TRUE
  )
  AND user_id = auth.uid()
);
```

---

## インデックス

```sql
-- users
CREATE UNIQUE INDEX idx_users_email ON users(email) WHERE email IS NOT NULL;
CREATE INDEX idx_users_plan ON users(plan);

-- rooms
CREATE INDEX idx_rooms_owner_id ON rooms(owner_id);
CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_dates ON rooms(start_date, end_date);
CREATE INDEX idx_rooms_development ON rooms(development_scheduled_at)
  WHERE status = 'pending_development';

-- room_members
CREATE UNIQUE INDEX idx_room_members_unique ON room_members(room_id, user_id);
CREATE INDEX idx_room_members_user ON room_members(user_id) WHERE is_active = TRUE;

-- photos
CREATE INDEX idx_photos_room ON photos(room_id);
CREATE INDEX idx_photos_user ON photos(user_id);
CREATE INDEX idx_photos_status ON photos(status);
CREATE INDEX idx_photos_visible ON photos(room_id) WHERE is_visible = TRUE;
CREATE INDEX idx_photos_expires ON photos(expires_at) WHERE expires_at IS NOT NULL;

-- notifications
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_unread ON notifications(user_id) WHERE is_read = FALSE;
```

---

## 詳細な定義

完全なテーブル定義、カラム定義、制約については、以下のファイルを参照してください：
- [DB設計書（完全版）](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/DB設計/設計書.md)
- [マイグレーションSQL](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/DB設計/001_initial_schema_complete.md)

---

**最終更新**: 2024年12月
