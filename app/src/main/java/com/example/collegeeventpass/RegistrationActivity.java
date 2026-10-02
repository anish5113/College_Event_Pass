package com.example.collegeeventpass;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class RegistrationActivity extends AppCompatActivity {

    private TextView tvSelectedEvent, tvDate;
    private EditText etStudentName, etRollNo, etEmail;
    private Button btnPickDate, btnProceed;
    private RadioGroup rgGender;
    private Switch switchFood;
    private String selectedEvent = "";
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        tvSelectedEvent = findViewById(R.id.tvSelectedEvent);
        tvDate = findViewById(R.id.tvDate);
        etStudentName = findViewById(R.id.etStudentName);
        etRollNo = findViewById(R.id.etRollNo);
        etEmail = findViewById(R.id.etEmail);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnProceed = findViewById(R.id.btnProceed);
        rgGender = findViewById(R.id.rgGender);
        switchFood = findViewById(R.id.switchFood);

        selectedEvent = getIntent().getStringExtra("EVENT_NAME");
        if (selectedEvent != null) {
            tvSelectedEvent.setText("Selected Event: " + selectedEvent);
        }

        btnPickDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    RegistrationActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month1 + 1, year1);
                        tvDate.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        btnProceed.setOnClickListener(v -> {
            String name = etStudentName.getText().toString().trim();
            String rollNo = etRollNo.getText().toString().trim();
            String email = etEmail.getText().toString().trim();

            if (name.isEmpty() || rollNo.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Please fill Name, Roll No, and Date", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            RadioButton rbGender = findViewById(selectedGenderId);
            String gender = rbGender != null ? rbGender.getText().toString() : "Not Specified";

            boolean hasFoodPass = switchFood.isChecked();

            Intent intent = new Intent(RegistrationActivity.this, PassActivity.class);
            intent.putExtra("EVENT_NAME", selectedEvent);
            intent.putExtra("STUDENT_NAME", name);
            intent.putExtra("ROLL_NO", rollNo);
            intent.putExtra("EMAIL", email);
            intent.putExtra("DATE", selectedDate);
            intent.putExtra("GENDER", gender);
            intent.putExtra("FOOD_PASS", hasFoodPass);
            startActivity(intent);
        });
    }
}
