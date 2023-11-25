package com.ananas.recyclerview.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.ananas.recyclerview.database.Dao;
import com.ananas.recyclerview.database.Database;
import com.ananas.recyclerview.databinding.ActivityUploadBinding;
import com.ananas.recyclerview.entity.Upload;
import com.google.android.material.snackbar.Snackbar;

import java.io.InputStream;

public class UploadActivity extends AppCompatActivity {
    com.ananas.recyclerview.databinding.ActivityUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri selectedImg;
    Database db;
    Dao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        db = Room.databaseBuilder(UploadActivity.this, Database.class,"content").build();
        dao = db.dao();
    }

    public void selectImgClicked(View v) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // izin daha önce sorulmuş  ancak reddedilmiş
                if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, Manifest.permission.READ_MEDIA_IMAGES)) {
                    // açıklama yapmak gerekirse yani izin mecbursa snackbar oluşturma
                    Snackbar.make(v, "Permission for take pics from gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                } else {
                    // direkt isteme
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }else{
                // izin verilmiş anlamına geliyor
                // direkt galeriye gidecez
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        }else {
            if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // açıklama yapmak gerekirse yani izin mecbursa snackbar oluşturma
                    Snackbar.make(v, "Permission for take pics from gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                } else {
                    // direkt isteme
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }else{
                // izin verilmiş anlamına geliyor
                // direkt galeriye gidecez
                Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        }
    }

    public void saveClicked(View v) {
if (selectedImg!=null){
    byte[] imageBytes = uriToByteArray(selectedImg);
Upload content = new Upload(binding.commentEditText.getText().toString(),imageBytes);
dao.insert(content);
}



    }

    public void registerLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // intent data taken from result
                    Intent intent = result.getData();
                    if (intent != null) {
                        selectedImg = intent.getData();
                        binding.imageView.setImageURI(selectedImg);

                    }
                }
            }
        });
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (result) {
                    // intent goes to gallery to get image
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intent);
                } else {
                    Toast.makeText(UploadActivity.this, "İzin alınamadı", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
private byte[] uriToByteArray(Uri imageUri){
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            if (inputStream!=null){
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            inputStream.close();
            return bytes;
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
}
}