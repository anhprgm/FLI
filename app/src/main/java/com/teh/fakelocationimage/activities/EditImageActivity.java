package com.teh.fakelocationimage.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;
import com.teh.fakelocationimage.CallApiService.ApiServices;
import com.teh.fakelocationimage.Constants.Constants;
import com.teh.fakelocationimage.FileMange.FileUtils;
import com.teh.fakelocationimage.FileMange.Image_Post;
import com.teh.fakelocationimage.R;
import com.teh.fakelocationimage.databinding.ActivityEditImageBinding;

import java.io.File;
import java.io.FileOutputStream;
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
//                        Uri uri = result.getData().getData();
//                        binding.foreGround.setImageURI(uri);
//                        path = FileUtils.getPath(this, uri);
//                        Log.d("haaa", path);
                        // truyền uri ra bên ngoài
                        Uri uri = result.getData().getData();
                        binding.bgImg.setImageURI(uri);
                        path = FileUtils.getPath(this, uri);
                        Log.d("haaa", path);
                    }
                }
            }
    );
    private void moveForeGround() {
        GestureDetector gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                float DeltaX = distanceX;
                float DeltaY =  distanceY;
                Log.d("onScroll", "DeltaX: " + DeltaX + " DeltaY: " + DeltaY);
                //smoth move
                ObjectAnimator objectAnimatorX = ObjectAnimator.ofFloat(binding.foreGround, "x", binding.foreGround.getX() - DeltaX);
                ObjectAnimator objectAnimatorY = ObjectAnimator.ofFloat(binding.foreGround, "y", binding.foreGround.getY() - DeltaY);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimatorX, objectAnimatorY);
                animatorSet.setDuration(0);
                animatorSet.start();
                Log.d("onScroll", "X: " + binding.foreGround.getX() + " Y: " + binding.foreGround.getY());
                return true;
            }
        });
//        binding.foreGround.setOnTouchImageViewListener(() -> {
//           float zoom = binding.foreGround.getCurrentZoom();
//           //make imageView wrapContent
//            ViewGroup.LayoutParams layoutParams = binding.foreGround.getLayoutParams();
//            layoutParams.width = (int) (binding.foreGround.getDrawable().getIntrinsicWidth() * zoom);
//            layoutParams.height = (int) (binding.foreGround.getDrawable().getIntrinsicHeight() * zoom);
//            binding.foreGround.setLayoutParams(layoutParams);
//            Log.d("onTouchImageViewListener", "zoom: " + zoom);
//
//
//
//        });
        binding.foreGround.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
        binding.resultBtn.setOnClickListener(v -> {
            combineImage();
        });

    }

    private void RemoveBg() {
        File file = new File(path);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        Call< Image_Post> call = api.uploadImage(body);
        call.enqueue(new Callback<Image_Post>() {
            @Override
            public void onResponse(Call<Image_Post> call, Response<Image_Post> response) {
                if (response.isSuccessful()) {
                    Log.d("haaa", "onResponse: " + response.body().getId());
                    Call<ResponseBody> call2 = api.cutImage(response.body().getId());
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Log.d("onResponse", "onResponse: " + response.raw());
                                saveFileToDevice(response.body());
                                binding.progressBar.setVisibility(View.GONE);
                            }
                        }


                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(EditImageActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("onFailure", "onFailure:" + t.toString());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Image_Post> call, Throwable t) {
                Toast.makeText(EditImageActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("onFailurexx", "onFailure:" + t.toString());
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
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    outputStream = Files.newOutputStream(file.toPath());
                }

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;
                }
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                binding.foreGround.setImageBitmap(bitmap);
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

    private void combineImage(){
        Bitmap bitmap = Bitmap.createBitmap(binding.bgImg.getWidth(), binding.bgImg.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        binding.bgImg.draw(canvas);
        //get position of imageView
        float x = binding.foreGround.getX();
        float y = binding.foreGround.getY();
        //get size of imageView
        float width = binding.foreGround.getWidth();
        float height = binding.foreGround.getHeight();
        //combine image
        Bitmap foreGround = ((BitmapDrawable) binding.foreGround.getDrawable()).getBitmap();
        canvas.drawBitmap(foreGround, x, y, null);
        binding.foreGround.setImageBitmap(bitmap);

//        binding.foreGround.draw(canvas);
//        binding.foreGround.setImageBitmap(bitmap);
    }
}