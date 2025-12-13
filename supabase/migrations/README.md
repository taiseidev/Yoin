# Supabase Migrations

このディレクトリには、Yoin（余韻）アプリのデータベーススキーマを定義するマイグレーションファイルが含まれています。

---

## マイグレーション一覧

| ファイル名 | 説明 | 日付 |
|---|---|---|
| `001_initial_schema.sql` | 初期スキーマ（全テーブル作成） | 2024-12-10 |

---

## 実行方法

### ローカル環境（Supabase CLI）

```bash
# Supabase CLIのインストール
npm install -g supabase

# ローカル環境の起動
supabase start

# マイグレーション実行
supabase db reset  # 全マイグレーションを実行

# または、特定のマイグレーション
supabase migration up
```

### Supabase Dashboard（本番環境）

1. [Supabase Dashboard](https://supabase.com/dashboard) にログイン
2. プロジェクトを選択
3. `Database` → `Migrations` に移動
4. マイグレーションファイルの内容をコピー&ペースト
5. `Run` をクリック

---

## マイグレーションファイルの構成

### 001_initial_schema.sql

以下のテーブルを作成します：

#### コアテーブル
- `users` - ユーザー情報
- `user_preferences` - ユーザー設定
- `rooms` - ルーム（旅行）
- `room_members` - ルームメンバー
- `invite_codes` - 招待コード
- `photos` - 写真
- `photo_downloads` - 写真ダウンロード履歴
- `daily_photo_counts` - 日別撮影数
- `guest_photo_counts` - ゲスト撮影数
- `monthly_room_creations` - 月間ルーム作成数
- `filters` - フィルター
- `stamp_settings` - 日付スタンプ設定

#### サブスクリプション
- `subscriptions` - サブスクリプション
- `subscription_history` - サブスク変更履歴

#### Shop・注文
- `products` - 商品
- `product_variants` - 商品バリアント
- `coupons` - クーポン
- `user_coupons` - ユーザークーポン
- `orders` - 注文
- `order_items` - 注文アイテム
- `order_item_photos` - 注文アイテム写真
- `shipping_addresses` - 配送先住所

#### 通知
- `notifications` - 通知
- `notification_settings` - 通知設定
- `fcm_tokens` - FCMトークン
- `scheduled_notifications` - 予約通知

#### サポート
- `contact_messages` - お問い合わせ
- `faq_categories` - FAQカテゴリ
- `faq_items` - FAQ項目

#### 統計・ログ
- `user_room_stats` - ユーザールーム統計
- `app_events` - アプリイベントログ

---

## Row Level Security (RLS)

各テーブルにRLSポリシーが設定されています：

- **users**: 自分のデータのみ閲覧・更新可能
- **rooms**: ルームメンバーのみ閲覧可能、オーナーのみ更新可能
- **photos**: ルームメンバーのみ閲覧・アップロード可能
- **subscriptions**: 自分のデータのみ閲覧・更新可能

詳細は `001_initial_schema.sql` の RLS セクションを参照してください。

---

## 関数

### generate_invite_code()

招待コードを生成する関数。

```sql
SELECT generate_invite_code();
-- 例: 'A3B7X-9K2M5'
```

### update_room_total_photos()

ルームの総写真枚数を更新するトリガー関数。

---

## 注意事項

1. **本番環境での実行前にバックアップを取る**
2. **マイグレーションの順序を守る**（001 → 002 → ...）
3. **RLSポリシーを確認する**（セキュリティ上重要）
4. **インデックスが正しく作成されているか確認する**

---

## トラブルシューティング

### エラー: "relation already exists"

すでにテーブルが存在する場合のエラー。`DROP TABLE IF EXISTS` を使用するか、既存のテーブルを削除してから再実行してください。

### エラー: "permission denied for table xxx"

RLSポリシーが原因の可能性があります。Service Roleキーを使用しているか確認してください。

---

## 詳細ドキュメント

完全なスキーマ定義は以下を参照してください：
- [DB設計書（プロジェクト内）](../docs/database-schema.md)
- [DB設計書（詳細版）](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/DB設計/設計書.md)
- [マイグレーションSQL（完全版）](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/DB設計/001_initial_schema_complete.md)

---

**最終更新**: 2024年12月
