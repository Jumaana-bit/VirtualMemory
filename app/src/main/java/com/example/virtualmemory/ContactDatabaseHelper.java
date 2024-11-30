package com.example.virtualmemory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ContactDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "contacts_db";
    private static final int DB_VERSION = 4;

    private static final String TABLE_CONTACTS = "contacts";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_RELATIONSHIP = "relationship";
    private static final String COL_EMAIL = "email";
    private static final String COL_PHONE = "phone_number";
    private static final String COL_CATEGORY = "category";
    private static final String COL_AUDIO= "voice_memo_path";


    public ContactDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_CONTACTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_RELATIONSHIP + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PHONE + " TEXT, " +
                COL_CATEGORY + " TEXT, " +
                COL_AUDIO + "TEXT)";  // Ensure voice_memo_path is created in the table
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // This will add the new column if the version is below 4 (adjust the version number as needed)
        db.execSQL("ALTER TABLE " + TABLE_CONTACTS + " ADD COLUMN " + COL_AUDIO + " TEXT;");

    }


    // Insert contact into database (with voice memo path)
    public long insertContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_NAME, contact.getName());
            values.put(COL_RELATIONSHIP, contact.getRelationship());
            values.put(COL_EMAIL, contact.getEmail());
            values.put(COL_PHONE, contact.getPhoneNumber());
            values.put(COL_CATEGORY, contact.getCategory());
            values.put(COL_AUDIO, contact.getVoiceMemoPath());  // Add voice memo path

            return db.insert(TABLE_CONTACTS, null, values);
        } finally {
            // Close the database after use
            db.close();
        }
    }

    public boolean doesContactExist(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT 1 FROM contacts WHERE name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{name});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }


    // Get contacts by category
    public List<Contact> getContactsByCategory(String category) {
        List<Contact> contacts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CONTACTS,
                    null,
                    COL_CATEGORY + " = ?",
                    new String[]{category},
                    null,
                    null,
                    COL_NAME + " ASC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int columnIndexId = cursor.getColumnIndex(COL_ID);
                    int columnIndexName = cursor.getColumnIndex(COL_NAME);
                    int columnIndexRelationship = cursor.getColumnIndex(COL_RELATIONSHIP);
                    int columnIndexEmail = cursor.getColumnIndex(COL_EMAIL);
                    int columnIndexPhone = cursor.getColumnIndex(COL_PHONE);
                    int columnIndexCategory = cursor.getColumnIndex(COL_CATEGORY);
                    int columnIndexVoiceMemoPath = cursor.getColumnIndex("voice_memo_path");

                    long id = (columnIndexId >= 0) ? cursor.getLong(columnIndexId) : -1;
                    String name = (columnIndexName >= 0) ? cursor.getString(columnIndexName) : "";
                    String relationship = (columnIndexRelationship >= 0) ? cursor.getString(columnIndexRelationship) : "";
                    String email = (columnIndexEmail >= 0) ? cursor.getString(columnIndexEmail) : "";
                    String phoneNumber = (columnIndexPhone >= 0) ? cursor.getString(columnIndexPhone) : "";
                    String contactCategory = (columnIndexCategory >= 0) ? cursor.getString(columnIndexCategory) : "";
                    String voiceMemoPath = (columnIndexVoiceMemoPath >= 0) ? cursor.getString(columnIndexVoiceMemoPath) : null;

                    contacts.add(new Contact(id, name, relationship, email, phoneNumber, contactCategory, voiceMemoPath));
                }
            }
        } finally {
            // Close cursor and database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return contacts;
    }

}

