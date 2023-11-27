package com.ananas.recyclerview.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.room.Room;

import com.ananas.recyclerview.R;
import com.ananas.recyclerview.adapter.Adapter;
import com.ananas.recyclerview.database.Dao;
import com.ananas.recyclerview.database.Database;
import com.ananas.recyclerview.databinding.ActivityMainBinding;
import com.ananas.recyclerview.entity.Upload;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    List<Upload> uploadList;
    Database db;
    Dao dao;
    CompositeDisposable compositeDisposable= new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        db = Room.databaseBuilder(MainActivity.this, Database.class, "content")
                .build();
             //   .allowMainThreadQueries()
        dao = db.dao();
        compositeDisposable.add(dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(MainActivity.this::handleResponse));
        uploadList = new ArrayList<>();

    }
    private void handleResponse(List<Upload> receivedUploadList){
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
      Adapter adapter = new Adapter(receivedUploadList);
        binding.recyclerView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_new) {
            // to Upload Activity
            Intent intent = new Intent(MainActivity.this, UploadActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}