---
description: 実装差分からコミットを作成
---

現在の実装差分を分析してコミットを作成します。

## 実行手順

### 1. 差分の確認
- `git status` で未追跡ファイルとステージングエリアの状態を確認
- `git diff` で変更内容を確認（ステージング済みと未ステージング両方）
- `git log --oneline -20` で最近のコミットメッセージを確認し、リポジトリのコミットスタイルを理解

### 2. 変更内容の分析
すべての変更内容を分析して、以下を判断:
- 変更の性質（新機能、既存機能の改善、バグ修正、リファクタリング、テスト、ドキュメントなど）
- 影響範囲（Domain層、Data層、Presentation層、Platform固有コードなど）
- ビジネスロジックへの影響
- アーキテクチャへの影響

### 3. コミットメッセージの作成
以下のガイドラインに従ってコミットメッセージを作成:
- **簡潔で明確**: 変更の「why（なぜ）」に焦点を当てる
- **日本語**: このプロジェクトは日本語でコミットメッセージを書く
- **1-2文程度**: 長すぎず、必要な情報を含む
- **プレフィックス**: 必要に応じて使用（feat:, fix:, refactor:, docs: など）

### 4. コミットの作成
- シークレット情報を含む可能性のあるファイル（.env、credentials.jsonなど）は除外
- 関連する未追跡ファイルをステージングエリアに追加
- HEREDOCを使用してコミットメッセージを渡す:
  ```bash
  git commit -m "$(cat <<'EOF'
  コミットメッセージ
  EOF
  )"
  ```
- **重要**: コミットメッセージには「🤖 Generated with [Claude Code]」や「Co-Authored-By: Claude」などの署名を含めない

### 5. 確認
- コミット完了後に `git status` で成功を確認
- 必要に応じて `git log -1` で作成されたコミットを表示

## Pre-commit hookの処理

コミットがpre-commit hookの変更により失敗した場合、1度だけリトライします。成功したがhookによってファイルが変更された場合:
- 作者を確認: `git log -1 --format='%an %ae'`
- プッシュされていないか確認: `git status` で「Your branch is ahead」を確認
- 両方がtrueの場合: コミットをamend
- それ以外: 新しいコミットを作成（他の開発者のコミットはamendしない）

## 重要な注意事項

- ✅ 必ずgit diff/statusで差分を確認してからコミット
- ✅ コーディング規約とアーキテクチャに従っているか確認
- ✅ ビジネスルールに違反していないか確認
- ❌ git configを更新しない
- ❌ 破壊的なgitコマンド（force push、hard resetなど）は実行しない
- ❌ フックをスキップしない（--no-verify、--no-gpg-signなど）
- ❌ main/masterへのforce pushは実行しない
- ❌ ユーザーが明示的に依頼していない限り、リモートへのpushは実行しない
- ❌ 変更がない場合は空のコミットを作成しない

## コミットメッセージの例

```
feat: ルーム作成画面を実装

Domain層にRoomモデルとCreateRoomUseCaseを追加し、
Presentation層でMVIパターンに従ってルーム作成UIを実装。
```

```
fix: ゲストユーザーの撮影制限が正しく動作しない問題を修正

GuestPhotoCountRepositoryで日付の比較ロジックが誤っていたため、
LocalDateの比較方法を修正。
```

```
refactor: Repository層のエラーハンドリングを統一

すべてのRepositoryでResult型を使用するように統一し、
エラーメッセージを適切にログ出力するように変更。
```

## 参考

- [コーディング規約](docs/coding-standards.md)
- [アーキテクチャ設計](docs/architecture.md)
- [開発ガイドライン](docs/development-guide.md)
