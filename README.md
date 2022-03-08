# flex_ocr_android_toolkit

## リポジトリの構成

- flex_ocr_android_toolkit ライブラリ
- app ライブラリを使用するサンプルアプリ

## 使い方

サンプルアプリのように、moduleとして直接インポートするか、
flex_ocr_android_toolkitをarrとしてアーカイブしてからインポートして使ってください。

### モデルの初期化方法

モデルは使用する前に1度だけ、初期化してください。
下記はサンプルアプリから抜粋した例です。
```Java
public class FlexApplication extends Application {

    private final static String TAG = FlexApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //モデルファイルはassets以下にあらかじめ配置しておく。pathにはassetsからの相対パスを指定する。
        FlexConfig flexConfig = new FlexConfig(this, "custom_models/sample.ptl");
        flexConfig.setDetectorModelInputSize(512, 384); //モデルのインプットサイズに合わせる
        FlexAPI.shared().init(flexConfig);
    }
}
```

### スキャン方法

スキャンするときは、読み取りたい文字列のホワイトリストを渡すこともできます。

```Java
  HashSet<String> whiteList = new HashSet<>();
  whiteList.add("0000000"); //ハイフン抜きで入れてください
  flexScanOption = new FlexScanOption(whiteList);
  ...

  final FlexScanResults results = FlexAPI.shared().scan(image, flexScanOption);
```

詳細はサンプルアプリの`ReaderActivity`クラスを参照してください。

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
