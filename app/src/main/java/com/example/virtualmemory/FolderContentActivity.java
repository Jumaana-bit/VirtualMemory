package com.example.virtualmemory;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class FolderContentActivity extends AppCompatActivity {

    private List<MediaItem> mediaList;
    private MediaAdapter mediaAdapter;
    private RecyclerView recyclerView;
    private String folderName;
    private EditText editTextLabel;
    private Uri selectedMediaUri;
    private static final int PICK_MEDIA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_content);

        folderName = getIntent().getStringExtra("folderName");
        Log.d("FolderContentActivity", "Folder Name: " + folderName);

        if ("Family".equals(folderName)) {
            copyVideoToFamilyFolderIfNeeded();
        }

        recyclerView = findViewById(R.id.recyclerViewFolder);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Load media (photos and videos) from the specific folder
        File folder = new File(getFilesDir(), folderName);
        mediaList = loadMediaFromFolder(folder);
        Log.d("FolderContentActivity", "Loaded media items: " + mediaList.size());

        editTextLabel = findViewById(R.id.editTextLabel);
        mediaAdapter = new MediaAdapter(mediaList, this);
        recyclerView.setAdapter(mediaAdapter);

        Button addButton = findViewById(R.id.addButtonInFolder);
        addButton.setOnClickListener(v -> openGallery());

        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveMediaWithLabel());
    }

    // Save media (photo or video) to directory with a label
    private void saveMediaToDirectory(Uri mediaUri, String label, String folderName) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(mediaUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            File specificDir = new File(getFilesDir(), folderName);
            if (!specificDir.exists()) {
                specificDir.mkdirs();
                Log.d("FolderContentActivity", "Created folder: " + specificDir.getAbsolutePath());
            }

            // Use the label as part of the filename
            String sanitizedLabel = label.replaceAll("[^a-zA-Z0-9]", "_");
            String extension = mediaUri.toString().contains("video") ? ".mp4" : ".jpg";
            File mediaFile = new File(specificDir, sanitizedLabel + "_" + System.currentTimeMillis() + extension);

            if (extension.equals(".jpg")) {
                // Save photo
                FileOutputStream outputStream = new FileOutputStream(mediaFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } else {
                // Save video
                try (OutputStream output = new FileOutputStream(mediaFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                }
            }
            Log.d("FolderContentActivity", "Saved media: " + mediaFile.getAbsolutePath());

            addMedia(mediaFile.getAbsolutePath(), label, extension.equals(".mp4"));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FolderContentActivity", "Failed to save media: " + e.getMessage());
            Toast.makeText(this, "Failed to save media.", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to load both photos and videos from a specific folder
    private List<MediaItem> loadMediaFromFolder(File folder) {
        List<MediaItem> mediaItems = new ArrayList<>();
        if (folder.exists()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    String filename = file.getName();
                    String label = filename.contains("_") ? filename.substring(0, filename.lastIndexOf("_")) : filename;
                    boolean isVideo = filename.endsWith(".mp4") || filename.endsWith(".avi");
                    mediaItems.add(new MediaItem(file.getAbsolutePath(), label, isVideo));
                }
            }
        }
        return mediaItems;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("*/*"); // Allows picking any media type
        String[] mimeTypes = {"image/*", "video/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, PICK_MEDIA); // Update this constant to handle both media types
    }

    private void saveMediaWithLabel() {
        String label = editTextLabel.getText().toString().trim();

        if (label.isEmpty()) {
            Toast.makeText(this, "Label is required.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedMediaUri != null) {
            Log.d("FolderContentActivity", "Saving media with label: " + label);
            saveMediaToDirectory(selectedMediaUri, label, folderName);

            editTextLabel.setText("");
            editTextLabel.setVisibility(View.GONE);
            findViewById(R.id.saveButton).setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "No media selected.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_MEDIA && resultCode == RESULT_OK && data != null) {
            selectedMediaUri = data.getData();
            String type = getContentResolver().getType(selectedMediaUri);

            // Check if the selected media is an image or video
            boolean isVideo = type != null && type.startsWith("video/");
            if (isVideo) {
                // Show video label input and save option
                editTextLabel.setVisibility(View.VISIBLE);
                editTextLabel.requestFocus();
                Button saveButton = findViewById(R.id.saveButton);
                saveButton.setVisibility(View.VISIBLE);
            } else {
                // Show photo label input and save option
                editTextLabel.setVisibility(View.VISIBLE);
                editTextLabel.requestFocus();
                Button saveButton = findViewById(R.id.saveButton);
                saveButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void addMedia(String path, String label, boolean isVideo) {
        MediaItem mediaItem = new MediaItem(path, label, isVideo);
        mediaList.add(mediaItem);
        mediaAdapter.notifyItemInserted(mediaList.size() - 1);
    }

    private void copyVideoToFamilyFolderIfNeeded() {
        File familyFolder = new File(getFilesDir(), "Family");
        if (!familyFolder.exists()) {
            familyFolder.mkdirs(); // Create the folder if it doesn't exist
        }

        File videoFile = new File(familyFolder, "cat.mp4");
        if (!videoFile.exists()) {
            AssetManager assetManager = getAssets();
            try (InputStream inputStream = assetManager.open("cat.mp4");
                 FileOutputStream outputStream = new FileOutputStream(videoFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                Log.d("FolderContentActivity", "cat.mp4 copied to Family folder.");
            } catch (IOException e) {
                Log.e("FolderContentActivity", "Failed to copy cat.mp4: " + e.getMessage());
            }
        }
    }



    public void deletePhoto(MediaItem mediaItem) {
        File file = new File(mediaItem.getPath());
        if (file.exists()) {
            if (file.delete()) {
                mediaList.remove(mediaItem);
                mediaAdapter.notifyDataSetChanged();
                Toast.makeText(this, "Media item deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete media item.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

