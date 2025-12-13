# コントリビューションガイド

Yoin（余韻）プロジェクトへのコントリビューションをご検討いただき、ありがとうございます！

---

## 開発環境のセットアップ

詳細は [README.md](README.md#セットアップ) を参照してください。

### 必要なツール

- JDK 17以上
- Android Studio Hedgehog以降
- Xcode 15以降（iOS開発の場合）
- Cocoapods（iOS開発の場合）

### 初回セットアップ

```bash
git clone <repository-url>
cd YoinApp
cp .env.example .env.local
# .env.local を編集
./gradlew build
```

---

## ブランチ戦略

### ブランチ種別

| ブランチ | 用途 | 命名規則 |
|---|---|---|
| `main` | 本番環境 | - |
| `develop` | 開発環境 | - |
| `feature/*` | 機能開発 | `feature/login-screen` |
| `fix/*` | バグ修正 | `fix/camera-crash` |
| `refactor/*` | リファクタリング | `refactor/repository-layer` |

### ブランチ運用

```bash
# 新しいブランチを作成
git checkout develop
git pull
git checkout -b feature/my-feature

# 開発
# ...

# コミット
git add .
git commit -m "feat(auth): add Google sign-in"

# プッシュ
git push origin feature/my-feature

# プルリクエストを作成
```

---

## コミットメッセージ規約

[Conventional Commits](https://www.conventionalcommits.org/) に従います。

### フォーマット

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type

| Type | 説明 |
|---|---|
| `feat` | 新機能追加 |
| `fix` | バグ修正 |
| `docs` | ドキュメント更新 |
| `style` | コードフォーマット |
| `refactor` | リファクタリング |
| `test` | テスト追加・修正 |
| `chore` | ビルド設定など |

### 例

```bash
feat(room): add room creation screen
fix(photo): resolve upload failure on slow network
docs(api): add Supabase integration guide
```

---

## プルリクエスト

### 作成前のチェックリスト

- [ ] ビルドが通る (`./gradlew build`)
- [ ] テストが通る (`./gradlew test`)
- [ ] コーディング規約に従っている
- [ ] ドキュメントを更新した（必要に応じて）

### PRテンプレート

```markdown
## 概要
この PR の目的を簡潔に説明

## 変更内容
- 変更点1
- 変更点2

## スクリーンショット（UI変更の場合）
Before / After

## テスト方法
1. xxx画面を開く
2. xxxボタンをタップ

## チェックリスト
- [ ] ビルドが通る
- [ ] テストが通る
- [ ] ドキュメントを更新した
- [ ] UIの確認をした（Android/iOS）
```

---

## コーディング規約

詳細は [docs/coding-standards.md](docs/coding-standards.md) を参照してください。

### 主要なルール

- **命名規則**: クラス `PascalCase`, 関数・変数 `camelCase`, 定数 `UPPER_SNAKE_CASE`
- **アーキテクチャ**: MVI + Clean Architecture
- **State Management**: StateFlow / SharedFlow
- **エラーハンドリング**: Result型でラップ

---

## テスト

### テストの書き方

```kotlin
class GetPhotosUseCaseTest {
    @Test
    fun `should return photos when repository returns success`() = runTest {
        // Given
        val expected = listOf(Photo(id = "1"))
        coEvery { repository.getPhotos(any()) } returns expected

        // When
        val result = useCase("roomId")

        // Then
        assertEquals(expected, result)
    }
}
```

### テスト実行

```bash
# 全テスト
./gradlew allTests

# Android のみ
./gradlew :shared:testDebugUnitTest
```

---

## レビュープロセス

### レビュー観点

1. **アーキテクチャ**: Clean Architecture に従っているか
2. **コーディング規約**: 命名規則、フォーマットが正しいか
3. **エラーハンドリング**: 適切に例外処理されているか
4. **パフォーマンス**: 不要な再コンポーズ、メモリリークがないか
5. **セキュリティ**: 個人情報の扱い、RLS設定が適切か
6. **テスト**: 十分なテストがあるか

### レビュー後のアクション

- ✅ Approve: マージ可能
- 💬 Comment: 質問・提案
- ⚠️ Request Changes: 修正が必要

---

## イシューの報告

### バグ報告

```markdown
## 環境
- OS: Android 14 / iOS 17
- デバイス: Pixel 8 / iPhone 15
- アプリバージョン: 1.0.0

## 再現手順
1. xxx画面を開く
2. xxxボタンをタップ

## 期待される動作
xxxが表示される

## 実際の動作
xxxがクラッシュする

## スクリーンショット
（あれば添付）
```

### 機能リクエスト

```markdown
## 概要
xxx機能がほしい

## 理由
xxxができるようになると便利

## 提案
xxxのように実装してはどうか
```

---

## ドキュメントの更新

### ドキュメントの種類

- **README.md**: プロジェクト概要、セットアップ
- **docs/**: 詳細なドキュメント
- **ADR/**: アーキテクチャの決定記録
- **.claude/**: AIエージェント用コンテキスト

### 更新が必要な場合

- 新機能を追加したとき
- アーキテクチャを変更したとき
- APIを変更したとき
- 環境構築手順が変わったとき

---

## 質問・サポート

- **GitHub Issues**: バグ報告、機能リクエスト
- **GitHub Discussions**: 質問、議論

---

## ライセンス

このプロジェクトは個人開発プロジェクトです。

---

**最終更新**: 2024年12月
