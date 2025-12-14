# Architecture Decision Records (ADR)

このディレクトリには、Yoin（余韻）プロジェクトにおける重要な技術的決定を記録したドキュメントが含まれています。

---

## ADRとは

Architecture Decision Record (ADR) は、プロジェクトで行われた重要なアーキテクチャや技術選定の決定を記録するドキュメントです。

### 目的

- **決定の背景を残す**: なぜその技術を選んだのか、将来の自分や他の開発者が理解できる
- **議論の過程を記録**: どのような選択肢があったか、何を考慮したか
- **決定の一貫性**: 過去の決定を参照し、一貫性のある技術選定を行う

---

## ADR一覧

| # | タイトル | 日付 | ステータス |
|---|---|---|---|
| [001](001-use-kotlin-multiplatform.md) | Kotlin Multiplatform の採用 | 2024-12-10 | Accepted |
| [002](002-use-supabase-as-backend.md) | Supabase をバックエンドに採用 | 2024-12-10 | Accepted |
| [003](003-use-compose-multiplatform.md) | Compose Multiplatform の採用 | 2024-12-10 | Accepted |
| [004](004-use-mvi-architecture.md) | MVI アーキテクチャの採用 | 2024-12-10 | Accepted |
| [005](005-use-revenuecat-for-subscriptions.md) | RevenueCat による課金管理 | 2024-12-10 | Accepted |

---

## ADRのフォーマット

各ADRは以下の構成で記述します：

```markdown
# [番号]. [タイトル]

## ステータス

Proposed | Accepted | Deprecated | Superseded

## コンテキスト

決定が必要になった背景・状況

## 決定内容

何を決定したか

## 選択肢

検討した他の選択肢

## 結果

この決定により期待される結果、影響

## 参考資料

関連するドキュメント・リンク
```

---

## 新しいADRの作成方法

1. 番号を決める（最新のADR番号 + 1）
2. ファイル名を決める（例: `006-use-xxx.md`）
3. テンプレートに従って記述
4. このREADMEの一覧に追加

---

**最終更新**: 2024年12月
