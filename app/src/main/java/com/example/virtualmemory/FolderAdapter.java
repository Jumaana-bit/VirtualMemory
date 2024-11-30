package com.example.virtualmemory;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.FolderViewHolder> {

    private List<Folder> folderList; // List of folders
    private Context context; // Context for accessing resources and starting activities

    public FolderAdapter(List<Folder> folderList, Context context) {
        this.folderList = folderList; // Initialize the folder list
        this.context = context; // Initialize the context
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the folder item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.folder_item, parent, false);
        return new FolderViewHolder(view); // Create and return a new ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull FolderViewHolder holder, int position) {
        Folder folder = folderList.get(position);
        int imageResId = folder.getImageResId();
        holder.folderImage.setImageResource(imageResId);
        Log.d("FolderAdapter", "Setting image for folder: " + folder.getName() + " with resource ID: " + imageResId);
        holder.folderName.setText(folder.getName());

        holder.itemView.setOnClickListener(v -> {
            ((MainActivity) context).openFolder(folder.getName());
        });
    }

    @Override
    public int getItemCount() {
        return folderList.size(); // Return the total number of folders
    }

    // ViewHolder class to hold references to the views for each folder
    public static class FolderViewHolder extends RecyclerView.ViewHolder {
        ImageView folderImage; // ImageView for the folder image
        TextView folderName; // TextView for the folder name

        public FolderViewHolder(@NonNull View itemView) {
            super(itemView);
            folderImage = itemView.findViewById(R.id.folderImage); // Initialize the ImageView
            folderName = itemView.findViewById(R.id.folderName); // Initialize the TextView
        }
    }
}


