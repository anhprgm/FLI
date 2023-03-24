package com.teh.fakelocationimage.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ortiz.touchview.TouchImageView;
import com.teh.fakelocationimage.CallApiService.ApiServices;
import com.teh.fakelocationimage.Constants.Constants;
import com.teh.fakelocationimage.FileMange.FileUtils;
import com.teh.fakelocationimage.FileMange.Image_Post;
import com.teh.fakelocationimage.databinding.ActivityEditImageBinding;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditImageActivity extends AppCompatActivity {
    private ActivityEditImageBinding binding;
    private String path = "";
    private float startX, startY;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.API_LINK)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ApiServices api = retrofit.create(ApiServices.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        moveForeGround();
        getImage();
        binding.removeBg.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            RemoveBg();
        });
        binding.backGroundImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImageBg.launch(intent);
        });
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.foreGround.setMinZoom(0.1f);
        binding.foreGround.setMaxZoom(5f);
    }

    private void getImage() {
        binding.addImg.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri uri = result.getData().getData();
                        binding.foreGround.setImageURI(uri);
                        path = FileUtils.getPath(this, uri);
                        Log.d("haaa", path);
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> pickImageBg = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
//                        Log.d("haaa", path);
                        Uri uri = result.getData().getData();
                        binding.bgImg.setImageURI(uri);
                        path = FileUtils.getPath(this, uri);
                        Log.d("haaa", path);
                    }
                }
            }
    );
    @SuppressLint("ClickableViewAccessibility")
    private void moveForeGround() {
        binding.foreGround.setOnTouchImageViewListener(() -> {

            float curZoom = binding.foreGround.getCurrentZoom();
            Log.d("curZoom", "onTouchImageViewListener: " + curZoom);

        });
        binding.foreGround.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = view.getX() - motionEvent.getRawX();
                    startY = view.getY() - motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    view.setX(motionEvent.getRawX() + startX);
                    view.setY(motionEvent.getRawY() + startY);
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                default:
                    return false;
            }
            return true;
        });


    }

    private void RemoveBg() {
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call< Image_Post> call = api.uploadImage(body);
        call.enqueue(new Callback<Image_Post>() {
            @Override
            public void onResponse(@NonNull Call<Image_Post> call, @NonNull Response<Image_Post> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    Log.d("haaa", "onResponse: " + response.body().getId());
                    Call<ResponseBody> call2 = api.cutImage(response.body().getId());
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("onResponse", "onResponse: " + response.raw());
                                assert response.body() != null;
                                saveFileToDevice(response.body());
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        }


                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditImageActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("onFailure", "onFailure:" + t);
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call<Image_Post> call, @NonNull Throwable t) {
                Toast.makeText(EditImageActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onFailure", "onFailure:" + t);
            }
        });

    }
    private void saveFileToDevice(ResponseBody body) {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "image.jpg");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
//                long fileSize = body.contentLength();

                inputStream = body.byteStream();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    outputStream = Files.newOutputStream(file.toPath());
                }

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    assert outputStream != null;
                    outputStream.write(fileReader, 0, read);

                }
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                binding.foreGround.setImageBitmap(bitmap);
                assert outputStream != null;
                outputStream.flush();


            } catch (IOException e) {
                // Handle IO exception
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            // Handle IO exception
        }
    }
    // combine image
    private void combineImage(){
        Bitmap bitmap = Bitmap.createBitmap(binding.bgImg.getWidth(), binding.bgImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        binding.bgImg.draw(canvas);
        //get position of imageView
        float x = binding.foreGround.getX();
        float y = binding.foreGround.getY();
        //get size of imageView
//        float width = binding.foreGround.getWidth();
//        float height = binding.foreGround.getHeight();
        //combine image
        Bitmap foreGround = ((BitmapDrawable) binding.foreGround.getDrawable()).getBitmap();
        canvas.drawBitmap(foreGround, x, y, null);
        binding.foreGround.setImageBitmap(bitmap);

    }
}