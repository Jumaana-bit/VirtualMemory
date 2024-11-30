package com.example.virtualmemory;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Map;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    private ContactDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);  // Set the layout for this activity

        recyclerView = findViewById(R.id.recyclerViewFriends);  // Reference to the RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));  // Set the layout manager

        dbHelper = new ContactDatabaseHelper(this);  // Initialize the database helper

        // Manually call onUpgrade to handle the database schema changes
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int currentVersion = db.getVersion();  // Get the current version of the database

        // Check if the current version is different from the target version (e.g., 2)
        /*int targetVersion = 1;
        dbHelper.onUpgrade(db, currentVersion, targetVersion);*/


        // Example of adding a contact to the database (can be triggered by a form or button)
        // You would typically call dbHelper.insertContact(contact) elsewhere in the app when adding new contacts.
        //Contact contact2 = new Contact(0, "Emily", "sister", "emily@gmail.com", "555-5438", "family", "sister.mp3");

        // Insert the example contact
        if (!dbHelper.doesContactExist("John Doe")) {
            Contact contact = new Contact(0, "John Doe", "Friend", "john.doe@example.com", "555-1234", "friends", "audio.mp3");
            dbHelper.insertContact(contact);
        }
        //dbHelper.insertContact(contact2);

        // Load contacts from the database based on a specific category (e.g., "friends")
        loadContacts("friends");  // Load friends' contacts
        //loadContacts("family");
    }

    private void loadContacts(String category) {
        // Retrieve contacts of a specific category from the database
        List<Contact> contacts = dbHelper.getContactsByCategory(category);

        // Initialize the adapter with the contacts list and set it to the RecyclerView
        adapter = new ContactAdapter(this, contacts, contact -> {
            // Handle item click event (you can open a contact details screen here)
            Toast.makeText(ContactActivity.this, "Clicked on: " + contact.getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(adapter);  // Set the adapter to the RecyclerView
    }

    public void openHomePage(View view) {
        // Intent or logic to open the home page
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}

