package com.example.api;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
   private Button btEncode,btDecode,saveimage;
    private TextView textView;
    private ImageView imageView;
    private String sImage;
    int PICK_FROM_GALLERY = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Retrofit retrofit= new Retrofit.Builder()
                .baseUrl(Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Api api=retrofit.create(Api.class);
        Call<Response>call=api.getBase();
        call.enqueue(new Callback<Response>() {
            @Override
          public void onResponse(Call<Response> call, final retrofit2.Response<Response> response) {
                btEncode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Response responses=response.body();
                        {
                            Log.d(responses.getTitle(), "Title: ");
                            Log.d(responses.getBase64(), "Base64: ");
                            byte[] bytes=Base64.decode(responses.getBase64(),Base64.DEFAULT);
                            sImage= Base64.encodeToString(bytes,Base64.DEFAULT);
                            textView.setText(responses.getTitle());
                            textView.setText(responses.getBase64());
                        }
                    }
                });

            }
            @Override
            public void onFailure(Call<Response> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(),  Toast.LENGTH_SHORT).show();
            }
        });

       btEncode=findViewById(R.id.bt_encode);
        btDecode=findViewById(R.id.bt_decode);
        textView=findViewById(R.id.text_view);
        saveimage=findViewById(R.id.save);
        imageView=findViewById(R.id.image_view);
       btDecode.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                byte[] bytes=Base64.decode(sImage,Base64.DEFAULT);
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);
             //   textView.setText(bitmap);
            }
        });
       saveimage.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               BitmapDrawable drawable=(BitmapDrawable) imageView.getDrawable();
               Bitmap bitmap=drawable.getBitmap();

               saveImageToGallery(bitmap);
         }
       });
    }

    private void saveImageToGallery(Bitmap bitmap) {
       FileOutputStream fos;
        try {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q)
            {
                ContentResolver resolver=getContentResolver();
                ContentValues contentValues=new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"Image"+".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_PICTURES+File.separator+"TestFolder");
                Uri imageuri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
                fos= (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageuri));
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this,"image save",Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {
            Toast.makeText(this, "not saved"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK)
        {
            Uri uri=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                ByteArrayOutputStream stream= new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                byte[] bytes=stream.toByteArray();
                sImage= Base64.encodeToString(bytes,Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
        }