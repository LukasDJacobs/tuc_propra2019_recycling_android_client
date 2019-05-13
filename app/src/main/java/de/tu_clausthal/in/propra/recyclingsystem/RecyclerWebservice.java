package de.tu_clausthal.in.propra.recyclingsystem;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RecyclerWebservice {

    @GET("send_qr/{id}/")
    Call<ResponseBody> getCode(@Path("id") String id);

}
