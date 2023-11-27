package com.ananas.recyclerview.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ananas.recyclerview.databinding.RecyclerRowBinding;
import com.ananas.recyclerview.entity.Upload;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<Upload> uploadList ;

    public Adapter(List<Upload> uploadList) {
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
holder.recyclerRowBinding.textView.setText(uploadList.get(position).comment);
String imageBlob = uploadList.get(position).imageBlob;
Bitmap imageBitmap = stringToBitmap(imageBlob);
holder.recyclerRowBinding.imageView.setImageBitmap(imageBitmap);
    }
    private Bitmap stringToBitmap(String imageBlob){
        byte[] amogus = Base64.decode(imageBlob,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(amogus,0, amogus.length);
    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
    RecyclerRowBinding recyclerRowBinding;
    public ViewHolder(RecyclerRowBinding recyclerRowBinding) {
        super(recyclerRowBinding.getRoot());
        this.recyclerRowBinding = recyclerRowBinding;
    }
}
}
