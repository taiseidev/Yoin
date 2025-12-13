---
description: 新機能を追加する際のテンプレート
---

新機能「{{feature_name}}」を追加します。以下の手順で実装してください：

## 実装手順

### 1. Domain層の作成
- `shared/src/commonMain/kotlin/com/yoin/domain/model/` に必要なモデルを定義
- `shared/src/commonMain/kotlin/com/yoin/domain/repository/` にRepositoryインターフェースを定義
- `shared/src/commonMain/kotlin/com/yoin/domain/usecase/` にUseCaseを作成

### 2. Data層の実装
- `shared/src/commonMain/kotlin/com/yoin/data/dto/` にDTOを定義
- `shared/src/commonMain/kotlin/com/yoin/data/repository/` にRepository実装を作成
- 必要に応じてDataSourceを作成

### 3. Presentation層の実装
- `composeApp/src/commonMain/kotlin/ui/screens/` に画面を作成
- ViewModelを作成し、MVIパターンで状態管理
  - Intent: ユーザーアクション
  - State: UI状態
  - Effect: 一時的なイベント
- `navigation/` にナビゲーションを追加

### 4. プラットフォーム固有の実装（必要に応じて）
- `shared/src/commonMain/` に `expect class/fun` を定義
- `shared/src/androidMain/` に Android用の `actual` を実装
- `shared/src/iosMain/` に iOS用の `actual` を実装

### 5. DI設定
- `shared/src/commonMain/kotlin/di/AppModule.kt` に依存関係を追加

### 6. テストコード
- UseCaseのUnit Testを作成
- Repositoryのテストを作成
- 必要に応じてUI Testを作成

## チェックリスト
- [ ] Domain層が他のレイヤーに依存していない
- [ ] MVIパターンに従っている
- [ ] エラーハンドリングが適切
- [ ] コーディング規約に従っている
- [ ] テストコードを作成した
- [ ] ドキュメントを更新した

## 参考
- [アーキテクチャ設計](docs/architecture.md)
- [コーディング規約](docs/coding-standards.md)
- [開発ガイドライン](docs/development-guide.md)
