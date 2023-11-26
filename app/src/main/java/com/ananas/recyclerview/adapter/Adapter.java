package com.ananas.recyclerview.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ananas.recyclerview.databinding.RecyclerRowBinding;
import com.ananas.recyclerview.entity.Upload;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    ArrayList<Upload> uploadArrayList ;

    public Adapter(ArrayList<Upload> uploadArrayList) {
        this.uploadArrayList = uploadArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new ViewHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
holder.recyclerRowBinding.imageView.setImageURI(Uri.parse(uploadArrayList.get(position).imageuri));
holder.recyclerRowBinding.textView.setText(uploadArrayList.get(position).comment);
    }

    @Override
    public int getItemCount() {
        return uploadArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
    RecyclerRowBinding recyclerRowBinding;
    public ViewHolder(RecyclerRowBinding recyclerRowBinding) {
        super(recyclerRowBinding.getRoot());
        this.recyclerRowBinding = recyclerRowBinding;
    }
}
}
