# 002. Supabase をバックエンドに採用

## ステータス

**Accepted** - 2024年12月10日

---

## コンテキスト

Yoin（余韻）には以下のバックエンド機能が必要です：

- **データベース**: ユーザー、ルーム、写真などのデータ管理
- **認証**: Apple/Google/匿名ログイン
- **ストレージ**: 写真の保存
- **サーバー処理**: 現像処理、通知送信
- **Row Level Security**: データアクセス制御

個人開発のため、以下の制約があります：

- サーバー構築・運用のコストを最小限にしたい
- インフラ管理に時間を取られたくない
- 初期は無料枠で始めたい

---

## 決定内容

**Supabase** をメインのバックエンドサービスとして採用する。

### 利用する機能

- **Supabase Auth**: Apple/Google/匿名認証
- **Supabase Database (PostgreSQL)**: 全データ管理
- **Supabase Storage**: 写真保存、CDN配信
- **Edge Functions**: 通知送信、複雑な処理
- **pg_cron**: 現像処理の定期実行
- **Row Level Security (RLS)**: データアクセス制御

### Firebase との役割分担

- **Firebase**: プッシュ通知（FCM）、アナリティクス、クラッシュレポート
- **Supabase**: それ以外の全て

---

## 選択肢

### 1. Firebase

**メリット**:
- Googleの信頼性
- 豊富なドキュメント
- 無料枠が充実

**デメリット**:
- Firestore のクエリ制限
- RLS がない（セキュリティルールが複雑）
- PostgreSQL のような高度なクエリができない
- ストレージの URL 署名が弱い

### 2. AWS (Amplify)

**メリット**:
- 最も豊富な機能
- スケーラビリティ

**デメリット**:
- 複雑すぎる
- 料金体系がわかりにくい
- 設定が煩雑
- 個人開発には過剰

### 3. 自前サーバー (Ktor + Cloud Run)

**メリット**:
- 完全にカスタマイズ可能
- Kotlinで書ける

**デメリット**:
- サーバー構築・運用の手間
- 認証・ストレージを自前で実装
- 初期コストが高い
- 個人開発では現実的ではない

### 4. Supabase（採用）

**メリット**:
- **PostgreSQL**: 高度なクエリ、JOIN、トランザクション
- **RLS**: テーブルレベルのアクセス制御（セキュア）
- **オープンソース**: ロックインされない
- **Kotlin SDK**: supabase-kt が利用可能
- **Edge Functions**: Deno で柔軟に処理を書ける
- **無料枠**: MAU 500人まで無料
- **pg_cron**: 定期処理をDBで完結

**デメリット**:
- Firebase より歴史が浅い
- モバイル向けSDKがまだ発展途上

---

## 結果

### 期待される効果

- **開発効率**: 認証・DB・ストレージがオールインワン
- **セキュリティ**: RLS により、DBレベルでアクセス制御
- **コスト**: 初期は無料、成長しても $25/月 〜
- **拡張性**: PostgreSQL なので複雑なクエリも可能
- **運用負荷**: マネージドサービスで運用不要

### 影響

- Supabase の Kotlin SDK を使用
- Firebase は FCM、Analytics、Crashlytics のみ使用
- 将来、複雑な処理が必要になったら Ktor + Cloud Run を追加可能

---

## 技術詳細

### RLS ポリシー例

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
```

### pg_cron 例

```sql
-- 5分ごとに現像時刻を過ぎた写真を公開
SELECT cron.schedule(
  'develop-photos',
  '*/5 * * * *',
  $$
    UPDATE photos
    SET developed_at = NOW()
    WHERE developed_at IS NULL
      AND development_time <= NOW()
  $$
);
```

---

## 参考資料

- [Supabase 公式](https://supabase.com/)
- [supabase-kt](https://github.com/supabase-community/supabase-kt)
- [Row Level Security](https://supabase.com/docs/guides/auth/row-level-security)
- [Edge Functions](https://supabase.com/docs/guides/functions)

---

**決定者**: 開発者
**最終更新**: 2024年12月10日
