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
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.ananas.recyclerview.database.Dao;
import com.ananas.recyclerview.database.Database;
import com.ananas.recyclerview.databinding.ActivityUploadBinding;
import com.ananas.recyclerview.entity.Upload;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UploadActivity extends AppCompatActivity {
    com.ananas.recyclerview.databinding.ActivityUploadBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Uri selectedImg;
    Bitmap image;
    Database db;
    Dao dao;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        db = Room.databaseBuilder(UploadActivity.this, Database.class, "content")
                .build();
        //TODO sorun çözüyor ancak 2. bootda çöküyor
        // sıkıntılı .allowMainThreadQueries()
        dao = db.dao();
    }

    public void selectImgClicked(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                // izin daha önce sorulmuş  ancak reddedilmiş demek oluyor bu bir kez reddedip sonra tekrar reddedilirse çıkar
                if (ActivityCompat.shouldShowRequestPermissionRationale(UploadActivity.this, Manifest.permission.READ_MEDIA_IMAGES)) {
                   
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
            } else {
                // izin verilmiş anlamına geliyor
                // direkt galeriye gidecez
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        } else {
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
            } else {
                // izin verilmiş anlamına geliyor
                // direkt galeriye gidecez
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intent);
            }
        }
    }

    public void saveClicked(View v) {
        if (selectedImg != null) {
          /*
            * threading 1- Main Thread (UI) görünüm kısmını işler

             * 2- Default Thread (CPU Intensive) ağır ve arka planda yapılan işlerdir
             * 3- IO Thread (Network,Database) sisteme giren çıkan şeylerle ilgilenir
             *

// yöntem 1 dao.insert(content).subscribeOn(Schedulers.io()).subscribe();
    *
       *  disposable tek kullanımlık
        * olay şu, sqlite ön planda çalışmak istemediği için .allowMainThreadQuery sıkıntıya sebep oluyor bizde bu yöntemi kullanacaz
            //? note AndroidSchedulers classı elde etmek için io.reactivex.rxjava3:rxandroid:3.0.0 dependencies e eklenmesi gerek
            //!  mavi yer gerekli değil observeOn methodu keyfe kalmış ister yaz ister yazma
            */
            try {
                String imageBlobLocal;
                if (Build.VERSION.SDK_INT >= 28) {
                    ImageDecoder.Source source = ImageDecoder.createSource(UploadActivity.this.getContentResolver(), selectedImg);
                    Bitmap imageBitmapLocal = ImageDecoder.decodeBitmap(source);
                    imageBlobLocal = bitmapToString(imageBitmapLocal);
                } else {
                    Bitmap imageBitmapLocal = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(), selectedImg);
                    imageBlobLocal = bitmapToString(imageBitmapLocal);
                }
                Upload content = new Upload(binding.commentEditText.getText().toString(),imageBlobLocal);
                compositeDisposable
                        // * ekleme
                        .add(dao.insert(content)
                                // * io threadde işle
                                .subscribeOn(Schedulers.io())
                                // * mainThread de gösterj
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(
                                        this::handleResponse,
                                        throwable -> {
                                            throwable.printStackTrace();
                                            Toast.makeText(this, throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                        }
                                ));
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] amogus = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(amogus, Base64.DEFAULT);
    }

    private void handleResponse() {
        //ne boka yarıyor hiç bilmmiyorum
        Intent intent = new Intent(UploadActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
                        //   binding.imageView.setImageURI(selectedImg);
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                ImageDecoder.Source source = ImageDecoder.createSource(UploadActivity.this.getContentResolver(), selectedImg);
                                image = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(image);

                            } else {
                                image = MediaStore.Images.Media.getBitmap(UploadActivity.this.getContentResolver(), selectedImg);
                                binding.imageView.setImageBitmap(image);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}