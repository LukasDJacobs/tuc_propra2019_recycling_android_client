package de.tu_clausthal.in.propra.recyclingsystem.app.modules;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import de.tu_clausthal.in.propra.recyclingsystem.app.AppScope;
import de.tu_clausthal.in.propra.recyclingsystem.RecyclerWebservice;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {

    private static final String API_BASE_URL = "http://api.base/url/";
    private static final long NETWORK_TIMEOUT_SECONDS = 10;

    @Provides
    @AppScope
    RecyclerWebservice provideWebservice(Retrofit retrofit) {
        return retrofit.create(RecyclerWebservice.class);
    }

    @Provides
    @AppScope
    Retrofit provideRetrofit(OkHttpClient client) {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client);

        return builder.build();
    }

    @Provides
    @AppScope
    OkHttpClient provideOkHttpClient(HttpLoggingInterceptor interceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(interceptor)
                .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return builder.build();
    }

    @Provides
    @AppScope
    HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

}
