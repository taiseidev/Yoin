# Preview Annotations

このディレクトリには、Compose Multiplatformで使用する共通のPreviewアノテーションが含まれています。

## 使用可能なアノテーション

### @PhonePreview
- **用途**: 基本的なスマートフォンサイズでのPreview
- **サイズ**: 360dp × 800dp
- **使用例**: 単一の状態を確認したい場合

```kotlin
@PhonePreview
@Composable
private fun MyScreenPreview() {
    MyScreen()
}
```

### @LightAndDarkPreview
- **用途**: ライトモードとダークモードの両方で表示確認
- **サイズ**: 360dp × 800dp (各モード)
- **使用例**: テーマ対応を確認したい場合

```kotlin
@LightAndDarkPreview
@Composable
private fun MyScreenPreview() {
    MaterialTheme {
        MyScreen()
    }
}
```

### @FontScalePreview
- **用途**: 異なるフォント表示シナリオでの確認
- **バリエーション**: Normal, Large, Extra Large (各360×800)
- **使用例**: フォントサイズを変更するユーザー向けのレイアウト確認

```kotlin
@FontScalePreview
@Composable
private fun MyScreenPreview() {
    MyScreen()
}
```

### @ComprehensivePreview
- **用途**: 包括的な確認（様々なデバイスサイズと向き）
- **バリエーション**:
  - Phone - Portrait (360×800)
  - Phone - Landscape (800×360)
  - Small Phone (320×640)
  - Large Phone (412×915)
  - Tablet (768×1024)
- **使用例**: リリース前の最終確認

```kotlin
@ComprehensivePreview
@Composable
private fun MyScreenPreview() {
    MaterialTheme {
        MyScreen()
    }
}
```

## ベストプラクティス

1. **開発中は@PhonePreviewを使用**
   - 最も一般的なケースを素早く確認

2. **UIコンポーネント作成時は@FontScalePreviewも確認**
   - テキストが多い画面では特に重要

3. **リリース前は@ComprehensivePreviewで最終確認**
   - 様々な環境での表示を網羅的にチェック

4. **カスタムアノテーションの追加も可能**
   - プロジェクト固有のニーズに応じて拡張可能

## 注意事項

- Previewはビルド時に実行されないため、実際のデバイスでの動作確認も必要です
- フォントスケールの大きい設定では、レイアウトが崩れないよう注意してください
- ダークモード対応する場合は、MaterialThemeで適切に包む必要があります
