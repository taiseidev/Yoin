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

#### 3.1 Figmaデザインの確認（UI実装の場合）
UI実装を行う場合は、まずユーザーにFigmaデザインの有無を確認:
- **質問**: 「この機能のFigmaデザインはありますか？ある場合は、Figma URLまたはノードIDを教えてください」
- ユーザーがFigma情報を提供した場合:
  1. `mcp__figma-desktop__get_design_context`を使用してデザイン情報を取得
  2. 必要に応じて`mcp__figma-desktop__get_screenshot`でスクリーンショットを取得
  3. 取得したデザイン仕様（色、サイズ、レイアウト、コンポーネント構造など）に基づいてUIを実装
- Figmaデザインがない場合:
  - ユーザーと相談してUI仕様を決定してから実装

#### 3.2 画面実装
- `composeApp/src/commonMain/kotlin/ui/screens/` に画面を作成
- ViewModelを作成し、MVIパターンで状態管理
  - Intent: ユーザーアクション
  - State: UI状態
  - Effect: 一時的なイベント
- `navigation/` にナビゲーションを追加
- Figmaから取得した情報に基づいて正確にUIを実装:
  - カラーコード、フォントサイズ、余白などを仕様通りに実装
  - コンポーネントの階層構造をFigmaと一致させる

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
- [ ] UI実装の場合: Figmaデザインに基づいて実装したか確認した

## Figma MCP連携の詳細

### 使用可能なMCPツール

1. **mcp__figma-desktop__get_design_context**
   - デザイン情報（レイアウト、色、サイズ、テキストなど）をコード形式で取得
   - パラメータ:
     - `nodeId`: FigmaノードID（例: "123:456"）
     - `clientLanguages`: "kotlin" を指定
     - `clientFrameworks`: "compose" を指定
     - `artifactType`: "COMPONENT_WITHIN_A_WEB_PAGE_OR_APP_SCREEN" など

2. **mcp__figma-desktop__get_screenshot**
   - デザインのスクリーンショットを取得して視覚的に確認
   - UI要素の配置やデザイン意図を理解するのに役立つ

3. **mcp__figma-desktop__get_variable_defs**
   - Figmaで定義された変数（色、サイズなど）を取得
   - デザインシステムの一貫性を保つために使用

### URL形式の例
- `https://figma.com/design/:fileKey/:fileName?node-id=1-2` → nodeId: `1:2`
- `https://figma.com/design/:fileKey/branch/:branchKey/:fileName` → branchKeyをfileKeyとして使用

## 参考
- [アーキテクチャ設計](docs/architecture.md)
- [コーディング規約](docs/coding-standards.md)
- [開発ガイドライン](docs/development-guide.md)
