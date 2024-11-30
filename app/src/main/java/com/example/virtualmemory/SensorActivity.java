package com.example.virtualmemory;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

public class SensorActivity extends Activity {

    private TextView responseTextView;
    private SpeechRecognizer speechRecognizer;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;
    private static final int PERMISSION_REQUEST_CODE = 101;
    private long calendarId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Request Calendar permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR},
                    PERMISSION_REQUEST_CODE);
        }

        responseTextView = findViewById(R.id.responseTextView);
        FloatingActionButton askButton = findViewById(R.id.ask_button);

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new SpeechRecognitionListener());

        // Set up button click listener
        askButton.setOnClickListener(v -> startSpeechRecognition());

        // Prepopulate a doctor's appointment for demonstration
        addDoctorAppointment();
    }

    public class SpeechRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            responseTextView.setText("Listening...");
        }

        @Override
        public void onBeginningOfSpeech() {
            responseTextView.setText("Listening...");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            // Can be used to show mic sensitivity changes
        }

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {
            responseTextView.setText("Processing...");
        }

        @Override
        public void onError(int error) {
            responseTextView.setText("Error occurred during recognition: " + error);
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> recognizedText = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (recognizedText != null && !recognizedText.isEmpty()) {
                processQuery(recognizedText.get(0).toLowerCase());
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    }


    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say your query");
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                processQuery(results.get(0).toLowerCase());
            }
        }
    }

    private void addDoctorAppointment() {
        ContentResolver contentResolver = getContentResolver();

        // Find default Google Calendar ID
        Cursor cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                new String[]{CalendarContract.Calendars._ID, CalendarContract.Calendars.IS_PRIMARY},
                CalendarContract.Calendars.IS_PRIMARY + " = ?",
                new String[]{"1"},
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(CalendarContract.Calendars._ID);
                if (idIndex >= 0) {
                    calendarId = cursor.getLong(idIndex);
                }
                cursor.close();
            }

            cursor.close();
        }

        /*if (calendarId != -1) {
            Calendar startTime = Calendar.getInstance();
            startTime.add(Calendar.DAY_OF_MONTH, 1);  // Event tomorrow at 10:00 AM
            startTime.set(Calendar.HOUR_OF_DAY, 10);
            startTime.set(Calendar.MINUTE, 0);

            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, 1);

            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.DTSTART, startTime.getTimeInMillis());
            values.put(CalendarContract.Events.DTEND, endTime.getTimeInMillis());
            values.put(CalendarContract.Events.TITLE, "Doctor's Appointment");
            values.put(CalendarContract.Events.DESCRIPTION, "Your appointment with Dr. Smith.");
            values.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
            if (uri != null) {
                System.out.println("Appointment added");
            } else {
                System.out.println("Could not add appointment");
            }
        } else {
            responseTextView.setText("No Google Calendar found.");
        }*/
    }

    private void checkCalendarForAppointments() {
        if (calendarId == -1) {
            responseTextView.setText("No Google Calendar found.");
            return;
        }

        ContentResolver contentResolver = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        endTime.add(Calendar.DAY_OF_YEAR, 7);

        String selection = CalendarContract.Events.CALENDAR_ID + " = ? AND " +
                CalendarContract.Events.DTSTART + " >= ?";
        String[] selectionArgs = new String[]{
                String.valueOf(calendarId),
                String.valueOf(startTime.getTimeInMillis())
        };

        Cursor cursor = contentResolver.query(
                uri,
                new String[]{CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART},
                selection,
                selectionArgs,
                CalendarContract.Events.DTSTART + " ASC"
        );

        if (cursor != null && cursor.moveToFirst()) {
            // Extract title and event start time
            int titleIndex = cursor.getColumnIndex(CalendarContract.Events.TITLE);
            int dateIndex = cursor.getColumnIndex(CalendarContract.Events.DTSTART);

            if (titleIndex >= 0 && dateIndex >= 0) {
                String title = cursor.getString(titleIndex);
                long eventTimeMillis = cursor.getLong(dateIndex);

                // Convert event time to Calendar and format
                Calendar eventTime = Calendar.getInstance();
                eventTime.setTimeInMillis(eventTimeMillis);

                String eventDate = android.text.format.DateFormat.format("EEE, MMM d, h:mm a", eventTime).toString();

                responseTextView.setText("Next appointment: " + title + " on " + eventDate);
            } else {
                responseTextView.setText("Error retrieving event details.");
            }

            cursor.close();
        } else {
            responseTextView.setText("No appointments in the next 7 days.");
        }
    }

    private void processQuery(String query) {
        if (query.contains("appointment")) {
            checkCalendarForAppointments();
        } else if (query.contains("help")) {
            dialEmergencyNumber();
        } else {
            responseTextView.setText("I didn't understand. Please try again.");
        }
    }


    private void dialEmergencyNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:911"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
