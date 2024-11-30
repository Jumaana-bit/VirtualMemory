package com.example.virtualmemory;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FolderAdapter folderAdapter;
    private List<Folder> folderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createFolders(); // Create folders if they don't exist
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Grid layout with 2 columns

        folderList = loadFolders(); // Load folders into list
        folderAdapter = new FolderAdapter(folderList, this);
        recyclerView.setAdapter(folderAdapter);



    }

    // Method to create main folders
    private void createFolders() {
        createDirectory("Family");
        createDirectory("Friends");
    }

    private void createDirectory(String folderName) {
        File dir = new File(getFilesDir(), folderName);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (created) {
                Log.d("DirectoryCreation", folderName + " directory created");
            } else {
                Log.d("DirectoryCreation", "Failed to create " + folderName + " directory");
            }
        }
    }

    // Load available folders for display
    private List<Folder> loadFolders() {
        List<Folder> folders = new ArrayList<>();
        File[] directories = getFilesDir().listFiles(File::isDirectory);

        for (File directory : directories) {
            String folderName = directory.getName();
            int folderImageResId = getFolderImageResource(folderName); // Get image for folder
            folders.add(new Folder(folderName, folderImageResId));
        }
        return folders;
    }

    // Define images for specific folders (change as needed)
    private int getFolderImageResource(String folderName) {
        switch (folderName) {
            case "Family":
                return R.drawable.family; // Replace with actual image resources
            case "Friends":
                return R.drawable.friends;
            default:
                return R.drawable.folder; // Fallback image
        }
    }

    // Open FolderContentActivity with the selected folder name
    public void openFolder(String folderName) {
        Intent intent = new Intent(this, FolderContentActivity.class);
        intent.putExtra("folderName", folderName);
        startActivity(intent);
    }
}

