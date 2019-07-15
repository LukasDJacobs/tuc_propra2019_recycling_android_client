package de.tu_clausthal.in.propra.recyclingsystem.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;

import javax.crypto.KeyGenerator;
import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

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
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ALIAS = "Recycler_App_Keys";
    private static final String PREF_PUB_KEY = "RECYCLER_PUBLIC_KEY";
    private static final String PREF_PRIV_KEY = "RECYCLER_PRIVATE_KEY";

    private static final int RC_SCAN_CODE = 1001;

    private RecyclingObject mLastObject;
    private PrivateKey mPrivateKey;
    private RSAPublicKey mPublicKey;

    public boolean test_200_returned = false;

    @Inject
    RecyclerWebservice mWebservice;

    @BindView(R.id.cl_main_activity_root)
    View mRootView;

    @BindView(R.id.tv_response)
    TextView mTvResponse;

    @BindView(R.id.btn_recycle)
    Button mBtnRecycle;

    @BindView(R.id.cl_object_found)
    ConstraintLayout mClObjectFound;

    @BindView(R.id.tv_object_id)
    TextView mTvObjectId;

    @BindView(R.id.tv_created_by)
    TextView mTvCreatedBy;

    @BindView(R.id.tv_device_type)
    TextView mTvDeviceType;

    @BindView(R.id.tv_deposit)
    TextView mTvDeposit;

    @BindView(R.id.tv_device_status)
    TextView mTvDeviceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidInjection.inject(this);
        ButterKnife.bind(this);
        loadKeys();
    }

    private byte[] stringToByteArray(String s) {
        String[] split = s.substring(1, s.length()-1).split(", ");
        byte[] array = new byte[split.length];
        for (int i = 0; i < split.length; i++) {
            array[i] = Byte.parseByte(split[i]);
        }

        return array;
    }

    private void loadKeys() {
        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
        if (prefs.contains(PREF_PUB_KEY) && prefs.contains(PREF_PRIV_KEY)) {
            // Load keys
            String pubKeyString = prefs.getString(PREF_PUB_KEY, "");
            String privKeyString = prefs.getString(PREF_PRIV_KEY, "");
            byte[] pubKeyBytes = stringToByteArray(pubKeyString);
            byte[] privKeyBytes = stringToByteArray(privKeyString);

            try {
                KeyFactory factory = KeyFactory.getInstance("RSA");

                PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(privKeyBytes);
                mPrivateKey = factory.generatePrivate(keySpecPKCS8);

                X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(pubKeyBytes);
                mPublicKey = (RSAPublicKey) factory.generatePublic(keySpecX509);
            } catch (NoSuchAlgorithmException e) {
                Log.e(LOG_TAG, "No such algorithm", e);
            } catch (InvalidKeySpecException e) {
                Log.e(LOG_TAG, "Invalid key", e);
            }
        } else {
            // Generate keys
            try {
                KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
                keygen.initialize(512);
                KeyPair keyPair = keygen.generateKeyPair();

                mPrivateKey = keyPair.getPrivate();
                mPublicKey = (RSAPublicKey) keyPair.getPublic();

                // Save keys
                String privKeyString = Arrays.toString(mPrivateKey.getEncoded());
                String pubKeyString = Arrays.toString(mPublicKey.getEncoded());

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(PREF_PRIV_KEY, privKeyString);
                editor.putString(PREF_PUB_KEY, pubKeyString);
                editor.apply();

            } catch (NoSuchAlgorithmException e) {
                Log.e(LOG_TAG, "No such algorithm", e);
            }
        }
    }

    @OnClick(R.id.fab_scan_code)
    void onScanCodeClicked() {
        Intent scanCodeActivityIntent = new Intent(this, ScanCodeDialogActivity.class);
        startActivityForResult(scanCodeActivityIntent, RC_SCAN_CODE);
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes) {
            builder.append(String.format("%02X", b));
        }
        return builder.toString().toLowerCase();
    }

    @OnClick(R.id.btn_recycle)
    void onRecycleClicked() {
        // ID: 3fa06618-9aa8-41b5-a65e-a102f2309d88

        mBtnRecycle.setEnabled(false);

        RecyclingObject object = new RecyclingObject();
        object.setCreatorID("3fa06618-9aa8-41b5-a65e-a102f2309d88");
        object.setObjectID(mLastObject.getObjectID());
        object.setStatus(true);
        object.setPfand(mLastObject.getPfand());
        object.setObjectType(mLastObject.getObjectType());

        mWebservice.markRecycled(object).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String body = response.body().string();

                    // Cleanup work
                    if (body.startsWith("\"")) {
                        body = body.substring(1);
                    }
                    if (body.endsWith("\"")) {
                        body = body.substring(0, body.lastIndexOf("\""));
                    }
                    body = body.replaceAll("\\\\", "");

                    Gson gson = new Gson();
                    RecyclingObject responseObject = gson.fromJson(body, RecyclingObject.class);
                    Signature sig = Signature.getInstance("SHA256withRSA");
                    sig.initSign(mPrivateKey);
                    sig.update(body.getBytes(StandardCharsets.UTF_8));
                    byte[] sigBytes = sig.sign();
                    String sigString = bytesToHexString(sigBytes);
                    responseObject.setHash(sigString);

                    mWebservice.markRecycled(responseObject).enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.code() == 200) {
                                Snackbar.make(mRootView, "Gerät als verschrottet markiert!", Snackbar.LENGTH_LONG).show();
                                mTvDeviceStatus.setText("Verschrottet");
                            } else {
                                mBtnRecycle.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (SignatureException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
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

    protected void handleScannedCode(String code) {
        mWebservice.getCode(code).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() != 200) { // HTTP 200: OK
                    Log.e(LOG_TAG, "Error Code " + response.code());
                }

                test_200_returned = true;

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

                // Cleanup work
                if (body.startsWith("\"")) {
                    body = body.substring(1);
                }
                if (body.endsWith("\"")) {
                    body = body.substring(0, body.lastIndexOf("\""));
                }

                Gson gson = new Gson();

                mLastObject = gson.fromJson(body, RecyclingObject.class);
                mBtnRecycle.setVisibility(View.VISIBLE);

                if (mLastObject.getStatus()) { // True = Bereits verschrottet
                    showWarning();
                }

                showObject(mLastObject);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void showObject(RecyclingObject object) {
        mTvObjectId.setText(object.getObjectID());
        mTvCreatedBy.setText(object.getCreatorID());
        mTvDeviceType.setText(object.getObjectType());
        mTvDeposit.setText(Float.toString(object.getPfand()));

        if (object.getStatus()) {
            mTvDeviceStatus.setText("Verschrottet");
            mBtnRecycle.setEnabled(false);
        } else {
            mTvDeviceStatus.setText("In Benutzung");
            mBtnRecycle.setEnabled(true);
        }

        mClObjectFound.setVisibility(View.VISIBLE);
        for (int i = 0; i < mClObjectFound.getChildCount(); i++) {
            View v = mClObjectFound.getChildAt(i);
            v.setVisibility(View.VISIBLE);
        }
    }

    private void showWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Warnung: Dieses Gerät wurde bereits als verschrottet markiert!")
                .setTitle("WARNUNG")
                .setNeutralButton("OK", (dialog, which) -> {});
        builder.create().show();
    }
}
