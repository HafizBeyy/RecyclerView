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

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<Upload> uploadArrayList;
    Adapter adapter;
    Database db;
    Dao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        db = Room.databaseBuilder(MainActivity.this, Database.class, "content")
                .allowMainThreadQueries()
                .build();
        dao = db.dao();
        uploadArrayList = new ArrayList<>();
        getDatabaseData();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(uploadArrayList);
        binding.recyclerView.setAdapter(adapter);
    }

    private void getDatabaseData() {
List<String> comments = dao.getComment();
List<String> images = dao.getImage();
        for (int i = 0; i < comments.size() ; i++) {
String comment = comments.get(i);
String imageuri = images.get(i);
Upload upload = new Upload(comment,imageuri);
      uploadArrayList.add(upload);
        }
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

}