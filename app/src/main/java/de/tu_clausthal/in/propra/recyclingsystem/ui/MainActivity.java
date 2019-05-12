package de.tu_clausthal.in.propra.recyclingsystem.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import de.tu_clausthal.in.propra.recyclingsystem.R;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int RC_SCAN_CODE = 1001;

    @BindView(R.id.cl_main_activity_root)
    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.fab_scan_code)
    void onScanCodeClicked() {
        Intent scanCodeActivityIntent = new Intent(this, ScanCodeDialogActivity.class);
        startActivityForResult(scanCodeActivityIntent, RC_SCAN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case RC_SCAN_CODE:
                if (resultCode != RESULT_OK || data == null || !data.hasExtra(ScanCodeDialogActivity.EXTRA_SCAN_RESULT)) { // Something went wrong...
                    Log.e(LOG_TAG, "Error getting code from scanner");
                    break;
                }
                String code = data.getStringExtra(ScanCodeDialogActivity.EXTRA_SCAN_RESULT);
                handleScannedCode(code);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleScannedCode(String code) {
        Snackbar.make(mRootView, code, Snackbar.LENGTH_LONG).show();
    }
}
