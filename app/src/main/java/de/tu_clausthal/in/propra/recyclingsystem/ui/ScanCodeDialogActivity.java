package de.tu_clausthal.in.propra.recyclingsystem.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import com.otaliastudios.cameraview.Audio;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Mode;
import com.otaliastudios.cameraview.PictureResult;

import butterknife.BindView;
import butterknife.ButterKnife;

import de.tu_clausthal.in.propra.recyclingsystem.R;

public class ScanCodeDialogActivity extends AppCompatActivity {

    public static final String EXTRA_SCAN_RESULT = "SCAN_RESULT";

    @BindView(R.id.camera)
    CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_code_dialog);
        ButterKnife.bind(this);
        cameraView.setAudio(Audio.OFF); // Sonst muss die RECORD_AUDIO permission geholt werden
        cameraView.setMode(Mode.PICTURE);
        cameraView.setFacing(Facing.BACK);
        cameraView.setPlaySounds(false);
        cameraView.setLifecycleOwner(this);

        // Add a listener
        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                ScanCodeDialogActivity.this.onPictureTaken(result);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Take a picture in a second
        Handler handler = new Handler();
        handler.postDelayed(() -> cameraView.takePicture(), 1000);
    }

    private void onPictureTaken(@NonNull PictureResult result) {
        result.toBitmap(this::scanBitmapForCode);
    }

    private void scanBitmapForCode(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionBarcodeDetectorOptions options =
                (new FirebaseVisionBarcodeDetectorOptions.Builder())
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();
        FirebaseVisionBarcodeDetector detector =
                FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        detector.detectInImage(image).addOnSuccessListener(firebaseVisionBarcodes -> {
            if (firebaseVisionBarcodes.size() == 0) {
                cameraView.takePicture(); // Retry
            } else {
                String s = firebaseVisionBarcodes.get(0).getRawValue();
                Intent i = new Intent();
                i.putExtra(EXTRA_SCAN_RESULT, s);
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }
}
