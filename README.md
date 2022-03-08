# flex_ocr_android_toolkit


## サンプルアプリのビルド方法

### OpenCVのダウンロード

下記からOpenCVをダウンロードする。バージョン4.5.5のAndroid版を選択する。

https://github.com/opencv/opencv/releases

zipファイルを解凍し、中に入っているsdkディレクトリを、このリポジトリのトップに配置する。
ディレクトリ配置は下記のようになる。

```bash
ls .

app
flex_ocr_android_toolkit
opencv
...
```


### モデルファイルの配置

モデルファイルを「app」モジュールのassetsとして配置する。
配置する場所は、`custom_models/sample.ptl`とする

### ビルド
Android Studio、もしくはgradlewでビルドする。

## ライブラリ（flex_ocr_android_toolkit）のみビルドする方法

```bash
./gradlew assembleRelease
ls ./flex_ocr_android_toolkit/build/outputs/aar/
```
