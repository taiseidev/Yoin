# 001. Kotlin Multiplatform の採用

## ステータス

**Accepted** - 2024年12月10日

---

## コンテキスト

Yoin（余韻）は Android と iOS の両プラットフォームで提供する必要があります。クロスプラットフォーム開発において、以下の課題がありました：

- Android と iOS でコードを別々に書くと、開発・保守コストが2倍になる
- ビジネスロジックの整合性を保つ必要がある
- 個人開発のため、リソースが限られている

---

## 決定内容

**Kotlin Multiplatform (KMP)** を採用し、共通のビジネスロジックを書く。

### 共有するコード

- **Domain層**: UseCase, Repository Interface, Model
- **Data層**: Repository実装、DataSource
- **DI**: Koin による依存性注入
- **ネットワーク**: Ktor Client
- **ローカルDB**: SQLDelight

### プラットフォーム固有のコード

- **カメラ**: CameraX (Android) / AVFoundation (iOS)
- **通知**: FCM (Android) / APNs (iOS)
- **画像処理**: GPUImage (Android) / CoreImage (iOS)

---

## 選択肢

### 1. Native開発（Android + iOS別々）

**メリット**:
- プラットフォームの機能をフル活用
- パフォーマンスが最高

**デメリット**:
- 開発コストが2倍
- ビジネスロジックの整合性維持が困難
- 個人開発では現実的ではない

### 2. React Native

**メリット**:
- JavaScriptで書ける
- 大きなエコシステム

**デメリット**:
- Kotlinの知識が活かせない
- TypeScriptとKotlinの両方を学ぶ必要がある
- ネイティブパフォーマンスに劣る

### 3. Flutter

**メリット**:
- 高速な開発
- 美しいUI

**デメリット**:
- Dartという新しい言語を学ぶ必要がある
- Androidのエコシステムと連携しにくい
- Kotlinの知識が活かせない

### 4. Kotlin Multiplatform（採用）

**メリット**:
- ビジネスロジックを共有できる
- Kotlinの知識を活かせる
- ネイティブパフォーマンス
- UIは各プラットフォームのベストプラクティスに従える
- Compose Multiplatform でUIも共有可能（将来）

**デメリット**:
- まだ比較的新しい技術
- エコシステムが React Native や Flutter より小さい

---

## 結果

### 期待される効果

- **開発効率**: ビジネスロジックを一度書くだけで済む
- **保守性**: 共通コードは1箇所で管理
- **一貫性**: Android/iOS で同じロジックが動作
- **パフォーマンス**: ネイティブコードにコンパイルされる
- **学習コスト**: Kotlinのみで開発可能

### 影響

- Android開発の経験が活かせる
- iOSの一部機能は `expect/actual` で個別実装が必要
- Compose Multiplatform でUIも共有できる（すでに採用済み）

---

## 参考資料

- [Kotlin Multiplatform 公式](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [ADR 003: Compose Multiplatform の採用](003-use-compose-multiplatform.md)

---

**決定者**: 開発者
**最終更新**: 2024年12月10日
