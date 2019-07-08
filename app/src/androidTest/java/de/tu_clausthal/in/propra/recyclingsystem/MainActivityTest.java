package de.tu_clausthal.in.propra.recyclingsystem;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import de.tu_clausthal.in.propra.recyclingsystem.ui.MainActivity;
import de.tu_clausthal.in.propra.recyclingsystem.ui.ScanCodeDialogActivity;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {

    private MainActivity mMainActivity;
    private MockWebServer mServer;
    private RecyclerWebservice mWebservice;

    @Rule
    public ActivityTestRule<MainActivity> rule  = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void allocateResources() throws IOException {
        mMainActivity = rule.getActivity();
        mServer = new MockWebServer();
        mServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mServer.url("").toString())
                .build();

        mWebservice = retrofit.create(RecyclerWebservice.class);
    }

    @After
    public void releaseResources() throws IOException {
        mMainActivity = null;
        mServer.shutdown();
    }

    @Test
    public void mainActivityIsPresent() {
        assertThat(mMainActivity, notNullValue());
    }

    @Test
    public void mockServerIsPresent() {
        assertThat(mServer, notNullValue());
    }

    @Test
    public void testGetResponse() {
        // Mock up server response
        mServer.enqueue(new MockResponse().setBody("\"{'creatorID': 'sfewrg', 'objectID': '63e5ff54-39f7-4404-a361-7a450c073349', 'objectType': 'rg3333', 'pfand': '33', 'status': True, 'prevhash': 'esgw'}\""));

        // MOck object
        RecyclingObject mockObject = new RecyclingObject();
        mockObject.setCreatorID("sfewrg");
        mockObject.setObjectID("63e5ff54-39f7-4404-a361-7a450c073349");
        mockObject.setObjectType("rg3333");
        mockObject.setPfand(33);
        mockObject.setStatus(true);
        mockObject.setPrevhash("esgw");

        mWebservice.getCode("63e5ff54-39f7-4404-a361-7a450c073349").enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                assertEquals(200, response.code());

                Gson gson = new Gson();
                assertNotNull(response.body());
                String body = "";
                try {
                    body = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                    fail();
                }
                // Cleanup work
                if (body.startsWith("\"")) {
                    body = body.substring(1);
                }
                if (body.endsWith("\"")) {
                    body = body.substring(0, body.lastIndexOf("\""));
                }
                RecyclingObject object = gson.fromJson(body, RecyclingObject.class);

                assertEquals(object, mockObject);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

}
