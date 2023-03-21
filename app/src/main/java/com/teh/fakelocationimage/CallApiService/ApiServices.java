package com.teh.fakelocationimage.CallApiService;

import com.teh.fakelocationimage.Constants.Constants;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiServices {


    @GET("get_img")
    Call<ResponseBody> getImage();

    @GET("get_img/{id}")
    Call<ResponseBody> getImageById(@Path("id") String id);

    @Multipart
    @POST("post_img")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part image);

    @GET("cut_img/{id}")
    Call<ResponseBody> cutImage(@Path("id") String id);

    @GET("img_output/{id}")
    Call<ResponseBody> getOutput(@Path("id") String id);

    @GET("img_input/{id}")
    Call<ResponseBody> getInput(@Path("id") String id);

    @PUT("update_name_input/{id}")
    Call<ResponseBody> updateNameInput(@Path("id") String id);

    @DELETE("delete_img/{id}")
    Call<ResponseBody> deleteImage(@Path("id") String id);

    @Multipart
    @POST("post_img_bg")
    Call<ResponseBody> uploadImageBackground(MultipartBody.Part image);

    @GET("changes_img/{id_img}/{id_bg}")
    Call<ResponseBody> getChangesImg();


}
