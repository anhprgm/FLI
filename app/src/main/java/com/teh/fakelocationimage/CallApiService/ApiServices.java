package com.teh.fakelocationimage.CallApiService;

import com.teh.fakelocationimage.FileMange.Image_Post;
import com.teh.fakelocationimage.models.Location;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
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
    Call<Image_Post> uploadImage(@Part MultipartBody.Part image);

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

    @GET("listlocation.json")
    Call<List<Location>> getListLocation();
}
