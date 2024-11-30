package com.example.virtualmemory;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {

    private List<MediaItem> mediaItems;
    private WeakReference<Context> contextRef;

    public MediaAdapter(List<MediaItem> mediaItems, Context context) {
        this.mediaItems = mediaItems;
        this.contextRef = new WeakReference<>(context);
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem mediaItem = mediaItems.get(position);
        holder.bind(mediaItem);
    }

    public class MediaViewHolder extends RecyclerView.ViewHolder {
        private ImageView photoImageView;
        private TextView photoLabel;
        private ImageView videoIcon;
        private ImageView deleteIcon;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            photoLabel = itemView.findViewById(R.id.photoLabel);
            videoIcon = itemView.findViewById(R.id.videoIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);

            // Set click listener for delete icon
            deleteIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MediaItem mediaItem = mediaItems.get(position);
                    deleteMediaItem(mediaItem, position);
                }
            });

            // Set click listener to open the video
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    MediaItem mediaItem = mediaItems.get(position);
                    if (mediaItem.isVideo()) {
                        playVideo(mediaItem); // Play video
                    }
                }
            });
        }

        public void bind(MediaItem mediaItem) {
            // Load image using Glide
            Glide.with(itemView.getContext())
                    .load(mediaItem.getPath())  // Image path (already set for images)
                    .into(photoImageView);

            // Set label text
            photoLabel.setText(mediaItem.getLabel());

            // Show or hide video icon based on media type
            if (mediaItem.isVideo()) {
                videoIcon.setVisibility(View.VISIBLE);
            } else {
                videoIcon.setVisibility(View.GONE);
            }
        }
    }

    // Method to play the hardcoded video from assets
    private void playVideo(MediaItem mediaItem) {
        Context context = contextRef.get();
        if (context != null) {
            // Copy the video file from assets to cache directory
            File videoFile = copyAssetToCache(context, "cat.mp4"); // Replace "cat.mp4" with your actual video file name in assets
            if (videoFile != null) {
                Uri contentUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", videoFile);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(contentUri, "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                context.startActivity(intent); // Use context directly
            } else {
                // Handle error (file not found or copying failed)
                // Show a Toast or log the error
            }
        }

}

    private File copyAssetToCache(Context context, String assetFileName) {
        File cacheFile = new File(context.getCacheDir(), assetFileName);
        try (InputStream is = context.getAssets().open(assetFileName);
             FileOutputStream fos = new FileOutputStream(cacheFile)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if an error occurs
        }
        return cacheFile; // Return the copied file
    }


    // Method to delete the media item
    private void deleteMediaItem(MediaItem mediaItem, int position) {
        Context context = contextRef.get();
        if (context instanceof FolderContentActivity) {
            ((FolderContentActivity) context).deletePhoto(mediaItem); // Call delete method in FolderContentActivity
        }

        mediaItems.remove(position);
        notifyItemRemoved(position);
    }
}
