---
description: Yoinのアーキテクチャと技術スタックを説明
---

Yoin（余韻）プロジェクトのアーキテクチャと技術スタックについて、以下の観点から詳しく説明してください：

1. **全体構成**
   - Kotlin Multiplatform + Compose Multiplatformの採用理由
   - Android/iOS のコード共有戦略
   - バックエンドサービス（Supabase, Firebase）の役割分担

2. **レイヤードアーキテクチャ**
   - Presentation層（UI, ViewModel, MVI）
   - Domain層（UseCase, Repository Interface, Model）
   - Data層（Repository Impl, DataSource, DTO）
   - 各レイヤーの責務と依存関係

3. **主要な技術選定の理由**
   - なぜSupabaseを選んだのか
   - なぜMVIパターンを採用したのか
   - RevenueCatとStripeの使い分け

4. **データフロー**
   - 写真アップロードの流れ
   - 現像処理のフロー
   - プッシュ通知の仕組み

参考ドキュメント:
- [アーキテクチャ設計](docs/architecture.md)
- [技術構成（詳細）](/Users/t-z/workspace/obsidian-notes/08_個人開発/Yoin. - 余韻 -/技術構成.md)
- [.claude/context.md](.claude/context.md)
