# flex_ocr_android_toolkit

このライブラリは、[ネフロック](https://nefrock.com)が開発したOCRモデルを、Androidで動かすためのツールキットです。

現状、OCRモデル自体はこのリポジトリに含まれません。このライブラリだけではOCRはできないのでご注意ください。
（サンプルのOCRのモデルは、2022年5月に公開予定です。）

独自のOCRモデルが必要な方はネフロックまでご連絡ください。

TODO: プロジェクトLPへのリンク

## 動作イメージ

TODO: 動画を載せる(gif)

## リポジトリの構成

- flex_ocr_android_toolkit ライブラリ
- app ライブラリを使用するサンプルアプリ

サンプルアプリのように、moduleとして直接インポートするか、
flex_ocr_android_toolkitをaarとしてアーカイブしてからインポートして使ってください。

TODO: mavenリポジトリ化

## 使い方

```Java

// モデルファイルはassets以下にあらかじめ配置しておく。pathにはassetsからの相対パスを指定する。
// モデルの初期化は一度だけ行えばよい
FlexConfig flexConfig = new FlexConfig(this, "custom_models/sample.ptl");
flexConfig.setDetectorModelInputSize(512, 384); //モデルのインプットサイズに合わせる
FlexAPI.shared().init(flexConfig);

// カメラなどからimage(Bitmap or ImageProxy)を取得する
image = ...        
        
HashSet<String> whiteList = new HashSet<>();
whiteList.add("09063108081"); //ハイフン抜きで入れてください
FlexScanOption flexScanOption = new FlexScanOption(whiteList);
final FlexScanResults results = FlexAPI.shared().scan(image, flexScanOption);
```

これらはサンプルアプリの次のクラスに実装してあります。

- モデルの初期化 :arrow_right: [FlexApplicationクラス](./app/src/main/java/com/nefrock/flex/app/FlexApplication.java)
- スキャン処理 :arrow_right: [ReaderActivityクラス](./app/src/main/java/com/nefrock/flex/app/ReaderActivity.java)

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
