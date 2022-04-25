package com.nefrock.flex.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import com.nefrock.flex_ocr_android_toolkit.api.v1.FlexScanOption;

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
    private final Size resolutionSize = new Size(2448, 3264);
    private FlexScanOption flexScanOption;

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

//                            final FlexScanResults results = FlexAPI_V1.shared().scan(image, flexScanOption);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
//                                    overlayView.drawScanResult(results);
                                }
                            });
                            image.close();
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
}