package com.example.virtualmemory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReminderActivity extends AppCompatActivity {

    private EditText reminderText;
    private TextView selectedDateTimeText;
    private Button datePickerButton, timePickerButton, saveReminderButton;

    // Variables to store the selected date and time
    private int selectedYear, selectedMonth, selectedDay;
    private int selectedHour, selectedMinute;
    private String selectedDate = "";
    private String selectedTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Add this in saveReminder() or onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                // Prompt the user to grant the permission
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                Toast.makeText(this, "Grant exact alarm permission for this feature to work", Toast.LENGTH_LONG).show();
            }
        }

        // Initialize views
        reminderText = findViewById(R.id.reminderText);
        selectedDateTimeText = findViewById(R.id.selectedDateTimeText);
        datePickerButton = findViewById(R.id.datePickerButton);
        timePickerButton = findViewById(R.id.timePickerButton);
        saveReminderButton = findViewById(R.id.saveReminderButton);

        // Set up button listeners
        datePickerButton.setOnClickListener(v -> showDatePickerDialog());
        timePickerButton.setOnClickListener(v -> showTimePickerDialog());
        saveReminderButton.setOnClickListener(v -> saveReminder());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new android.app.DatePickerDialog(this, (view, yearSelected, monthSelected, daySelected) -> {
            selectedYear = yearSelected;
            selectedMonth = monthSelected;
            selectedDay = daySelected;

            String formattedDate = String.format("%02d/%02d/%d", selectedMonth + 1, selectedDay, selectedYear);
            selectedDateTimeText.setText("Selected Date: " + formattedDate);
        }, year, month, day).show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new android.app.TimePickerDialog(this, (view, hourSelected, minuteSelected) -> {
            selectedHour = hourSelected;
            selectedMinute = minuteSelected;

            String amPm = selectedHour >= 12 ? "PM" : "AM";
            int hourIn12HourFormat = (selectedHour > 12) ? selectedHour - 12 : selectedHour;
            String formattedTime = String.format("%02d:%02d %s", hourIn12HourFormat, selectedMinute, amPm);

            String currentText = selectedDateTimeText.getText().toString();
            selectedDateTimeText.setText(currentText + "\nSelected Time: " + formattedTime);
        }, hour, minute, false).show();
    }

    private void saveReminder() {
        String reminder = reminderText.getText().toString().trim();

        if (reminder.isEmpty() || selectedYear == 0 || selectedHour == 0) {
            Toast.makeText(this, "Please enter reminder details and select date/time!", Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, selectedYear);
        calendar.set(Calendar.MONTH, selectedMonth);
        calendar.set(Calendar.DAY_OF_MONTH, selectedDay);
        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
        calendar.set(Calendar.MINUTE, selectedMinute);
        calendar.set(Calendar.SECOND, 0);

        long alarmTimeInMillis = calendar.getTimeInMillis();
        if (alarmTimeInMillis <= System.currentTimeMillis()) {
            Toast.makeText(this, "Selected time is in the past. Please select a valid future time.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MedicationReminderReceiver.class);
        intent.putExtra("reminderText", reminder);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
        }

        Toast.makeText(this, "Reminder Saved: " + reminder, Toast.LENGTH_LONG).show();
    }

    public void openHomePage(View view) {
        // Intent or logic to open the home page
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}
