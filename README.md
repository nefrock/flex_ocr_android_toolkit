# flex_ocr_android_toolkit

## ライブラリの使用方法

opencv


## ビルド

### 前準備

### OpenCVのダウンロード

下記からopencvをダウンロードする。バージョン4.5.5のAndroid版を選択する。

https://github.com/opencv/opencv/releases

ダウンロード後は解凍して、適当なところに配備。後でAndroid StudioからImportする。
インポートする時のモジュール名は「:opencv」とする。


### ビルド
リリースビルドをする。

```bash
./gradlew assembleRelease
ls ./flex_ocr_android_toolkit/build/outputs/aar/
```

## Maven

Mavenのリポジトリとしてpublishする。デフォルトの設定ではローカルにpublishする。

```bash
./gradlew publish
ls flex_ocr_android_toolkit/build/repos/releases
```