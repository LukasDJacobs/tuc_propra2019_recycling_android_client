package de.tu_clausthal.in.propra.recyclingsystem.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import dagger.android.AndroidInjection;
import de.tu_clausthal.in.propra.recyclingsystem.R;
import de.tu_clausthal.in.propra.recyclingsystem.RecyclerWebservice;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int RC_SCAN_CODE = 1001;

    @Inject
    RecyclerWebservice mWebservice;

    @BindView(R.id.cl_main_activity_root)
    View mRootView;

    @BindView(R.id.tv_response)
    TextView mTvResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidInjection.inject(this);
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
        mWebservice.getCode(code).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() != 200) { // HTTP 200: OK
                    mTvResponse.setText("Error Code " + response.code());
                    mTvResponse.setVisibility(View.VISIBLE);
                }

                if (response.body() == null)
                    return;

                String s = "Response Code: " + response.code() + " " +response.message();
                s += "\n";
                try {
                    s += response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTvResponse.setText(s);
                mTvResponse.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
}
