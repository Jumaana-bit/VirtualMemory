package com.example.virtualmemory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {

    private TextView timeDateText;
    private ImageView timeIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize UI elements
        timeDateText = findViewById(R.id.timeDateText);
        timeIcon = findViewById(R.id.timeIcon);

        updateTimeAndDate();
        updateIconBasedOnTime();
    }

    private void updateTimeAndDate() {
        Calendar calendar = Calendar.getInstance();

        // Format the time, date, and day
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy");
        String currentTime = timeFormat.format(calendar.getTime());
        String currentDate = dateFormat.format(calendar.getTime());
        String currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, getResources().getConfiguration().locale);

        // Update the UI
        timeDateText.setText("Time: " + currentTime + "\nDate: " + currentDate + "\nDay: " + currentDay);
    }

    private void updateIconBasedOnTime() {
        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay >= 6 && hourOfDay < 18) {
            // It's daytime, show the sun
            timeIcon.setImageResource(R.drawable.sunny);
        } else {
            // It's nighttime, show the moon
            timeIcon.setImageResource(R.drawable.moon);
        }
    }

    // Navigate to Photo Album page
    public void openPhotoAlbumPage(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Navigate to maps
    public void openMapsPage(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        //finish();
    }

    // Navigate to contacts page
    public void openContactsPage(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivity(intent);
    }

    // Navigate to audio page
    public void openAudioPage(View view) {
        Intent intent = new Intent(this, SensorActivity.class);
        startActivity(intent);
    }

    // Navigate to reminders page
    public void openReminderPage(View view) {
        Intent intent = new Intent(this, ReminderActivity.class);
        startActivity(intent);
    }

}

