package de.tu_clausthal.in.propra.recyclingsystem.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import dagger.android.AndroidInjection;
import de.tu_clausthal.in.propra.recyclingsystem.R;
import de.tu_clausthal.in.propra.recyclingsystem.RecyclerWebservice;
import de.tu_clausthal.in.propra.recyclingsystem.RecyclingObject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int RC_SCAN_CODE = 1001;

    private RecyclingObject mLastObject;

    @Inject
    RecyclerWebservice mWebservice;

    @BindView(R.id.cl_main_activity_root)
    View mRootView;

    @BindView(R.id.tv_response)
    TextView mTvResponse;

    @BindView(R.id.btn_recycle)
    Button mBtnRecycle;

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

    @OnClick(R.id.btn_recycle)
    void onRecycleClicked() {
        /*
        {
	"recyclerID": "FortyTwo",
	"objectID": "3fa06618-9aa8-41b5-a65e-a102f2309d88"
}
         */


        RecyclingObject object = new RecyclingObject();
        object.setRecyclerID("FortyTwo");
        object.setObjectID(mLastObject.getObjectID());

        mWebservice.markRecycled(object).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Snackbar.make(mRootView, response.message(), Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
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

                String s = "Response Code: " + response.code() + " " + response.message();
                s += "\n";
                String body = "";
                try {
                    body = response.body().string();
                    s += body;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTvResponse.setText(s);
                mTvResponse.setVisibility(View.VISIBLE);

                Gson gson = new Gson();

                mLastObject = gson.fromJson(body, RecyclingObject.class);
                mBtnRecycle.setVisibility(View.VISIBLE);

                if (mLastObject.getStatus().equals("verschrottet")) {
                    showWarning();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void showWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Warnung: Dieses Gerät wurde bereits als verschrottet markiert!")
                .setTitle("WARNUNG")
                .setNeutralButton("OK", (dialog, which) -> {});
        builder.create().show();
    }
}
