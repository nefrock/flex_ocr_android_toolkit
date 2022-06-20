package com.nefrock.flex.app;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.AudioAttributes;
import android.media.Image;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.nefrock.flex_ocr_android_toolkit.api.FlexExitCode;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResultType;
import com.nefrock.flex_ocr_android_toolkit.api.FlexScanResults;
import com.nefrock.flex_ocr_android_toolkit.api.v1.DetectorKind;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexAPI;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;
import com.nefrock.flex_ocr_android_toolkit.api.v1.ModelConfig;
import com.nefrock.flex_ocr_android_toolkit.api.v1.OnScanListener;
import com.nefrock.flex_ocr_android_toolkit.api.v1.RecognizerKind;
import com.nefrock.flex_ocr_android_toolkit.util.ImageUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReaderActivity extends AppCompatActivity {

    /*** Fixed values ***/
    private static final String TAG = ReaderActivity.class.getSimpleName();
    private final int REQUEST_CODE_FOR_PERMISSIONS = 200;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA"};

    /*** Views ***/
    private PreviewView previewView;
    private OverlayView overlayView;

    /*** For CameraX ***/
    private Camera camera = null;
    private Preview preview = null;
    private ImageAnalysis imageAnalysis = null;
    private ExecutorService cameraExecutor = Executors.newSingleThreadExecutor();
//    private final Size resolutionSize = new Size(2448, 3264);
//    private final Size resolutionSize = new Size(768, 1024);
    private final Size resolutionSize = new Size(288, 352);


    private FlexScanOption flexScanOption;

    private SoundPool soundPool;
    private int sound;
    private SoundPool oneUpPool;
    private int oneUpSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        previewView = findViewById(R.id.previewView);
        overlayView = findViewById(R.id.overlayView);
        overlayView.setResolutionSize(resolutionSize);

        FrameLayout frameLayout = findViewById(R.id.frameLayout);
        LayoutParams layout = frameLayout.getLayoutParams();
        double ratio = ((double) resolutionSize.getHeight()) / ((double) resolutionSize.getWidth());
        int height = (int) (layout.width * ratio);
        layout.height = height;
        frameLayout.setLayoutParams(layout);

        if (checkPermissions()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_FOR_PERMISSIONS);
        }

        // HashSet<String> whiteList = new HashSet<>();
        // whiteList.add("00000000000"); //ハイフン抜きで入れてください
        // flexScanOption = new FlexScanOption(whiteList);
        // サンプルではホワイトリストを設定しない
        flexScanOption = new FlexScanOption();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                // USAGE_MEDIA
                // USAGE_GAME
                .setUsage(AudioAttributes.USAGE_GAME)
                // CONTENT_TYPE_MUSIC
                // CONTENT_TYPE_SPEECH, etc.
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        soundPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(10)
                .build();
        oneUpPool = new SoundPool.Builder()
                .setAudioAttributes(audioAttributes)
                // ストリーム数に応じて
                .setMaxStreams(10)
                .build();

        try {
            sound = soundPool.load(this.getAssets().openFd("sounds/coin1.wav"), 1);
            oneUpSound = oneUpPool.load(this.getAssets().openFd("sounds/oneup.wav"), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSupportedResolutionSizes() {

    }

    private void startCamera() {
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        Context context = this;
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    preview = new Preview.Builder().setTargetResolution(resolutionSize).build();
                    imageAnalysis = new ImageAnalysis.Builder()
                            .setTargetResolution(resolutionSize)
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build();


                    imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                        @Override
                        @SuppressLint("UnsafeOptInUsageError")
                        public void analyze(@NonNull ImageProxy image) {
                            Image mediaImage = image.getImage();
                            if (mediaImage == null) {
                                image.close();
                                return;
                            }
                            Bitmap bitmap = ImageUtils.imageToToBitmap(image.getImage(), 90);
                            FlexAPI.shared().scan(bitmap, flexScanOption, new OnScanListener<FlexScanResults>() {

                                long nbDetected = 0;

                                @Override
                                public void onScan(FlexScanResults results) {
                                    if (results.getExitCode() != FlexExitCode.DONE) {
                                        image.close();
                                        return;
                                    }
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(results.hasResult(FlexScanResultType.TELEPHONE_NUMBER)) {
                                                soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1);
                                            }
//                                            if(results.hasResult(FlexScanResultType.TELEPHONE_NUMBER)) {
//                                                if(nbDetected == 10) {
//                                                    oneUpPool.play(oneUpSound, 1.0f, 1.0f, 1, 0, 2f);
//                                                    nbDetected = 0;
//                                                } else {
////                                                    soundPool.play(sound, 1.0f, 1.0f, 0, 0, 1);
//                                                    nbDetected += 1;
//                                                }
//                                            }
                                            overlayView.drawScanResult(results);
                                            image.close();

                                        }
                                    });
                                }
                            });
                        }
                    });
                    CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                    cameraProvider.unbindAll();
                    camera = cameraProvider.bindToLifecycle((LifecycleOwner) context, cameraSelector, preview, imageAnalysis);
                    preview.setSurfaceProvider(previewView.getSurfaceProvider());
                } catch (Exception e) {
                    Log.e(TAG, "[startCamera] Use case binding failed", e);
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_FOR_PERMISSIONS) {
            if (checkPermissions()) {
                startCamera();
            } else {
                Log.i(TAG, "[onRequestPermissionsResult] Failed to get permissions");
                this.finish();
            }
        }
    }

    public void onClick(View view){
        Log.d(this.getClass().getName(), "onclick");
        this.startCamera();
    }
}