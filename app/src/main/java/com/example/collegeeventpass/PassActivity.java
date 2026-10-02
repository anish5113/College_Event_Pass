package com.example.collegeeventpass;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PassActivity extends AppCompatActivity {

    private TextView tvPassEventName, tvPassStudentName, tvPassRollNo, tvPassDate, tvPassGender, tvPassFood;
    private TextView tvUniquePassId, tvTxnId, tvIssuedTime, tvTotalFee;
    private Button btnConfirm, btnShare;
    
    private String eventName, studentName, rollNo, date, gender;
    private boolean foodPass;
    
    private String uniquePassId, txnId, issuedTime, totalFeeStr;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    sendNotification();
                } else {
                    Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass);

        // Bind existing fields
        tvPassEventName = findViewById(R.id.tvPassEventName);
        tvPassStudentName = findViewById(R.id.tvPassStudentName);
        tvPassRollNo = findViewById(R.id.tvPassRollNo);
        tvPassDate = findViewById(R.id.tvPassDate);
        tvPassGender = findViewById(R.id.tvPassGender);
        tvPassFood = findViewById(R.id.tvPassFood);
        
        // Bind new fields
        tvUniquePassId = findViewById(R.id.tvUniquePassId);
        tvTxnId = findViewById(R.id.tvTxnId);
        tvIssuedTime = findViewById(R.id.tvIssuedTime);
        tvTotalFee = findViewById(R.id.tvTotalFee);
        
        btnConfirm = findViewById(R.id.btnConfirm);
        btnShare = findViewById(R.id.btnShare);

        // Get Data from Intent
        Intent intent = getIntent();
        eventName = intent.getStringExtra("EVENT_NAME");
        studentName = intent.getStringExtra("STUDENT_NAME");
        rollNo = intent.getStringExtra("ROLL_NO");
        date = intent.getStringExtra("DATE");
        gender = intent.getStringExtra("GENDER");
        foodPass = intent.getBooleanExtra("FOOD_PASS", false);

        // Generate Identifiers and Timestamps
        long currentTime = System.currentTimeMillis();
        uniquePassId = "PASS-" + (currentTime % 100000) + "-" + (rollNo != null ? rollNo : "000");
        txnId = "TXN-" + (currentTime % 10000);
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault());
        issuedTime = sdf.format(new Date(currentTime));
        
        totalFeeStr = foodPass ? "₹150 (Paid)" : "₹0 (Free Entry)";

        // Set UI Text
        tvUniquePassId.setText("Pass ID: " + uniquePassId);
        tvTxnId.setText("Receipt / Txn ID: " + txnId);
        tvIssuedTime.setText("Issued On: " + issuedTime);
        tvTotalFee.setText("Total Amount: " + totalFeeStr);
        
        tvPassEventName.setText("Event: " + (eventName != null ? eventName : "N/A"));
        tvPassStudentName.setText("Name: " + (studentName != null ? studentName : "N/A"));
        tvPassRollNo.setText("Roll No: " + (rollNo != null ? rollNo : "N/A"));
        tvPassDate.setText("Event Date: " + (date != null ? date : "N/A"));
        tvPassGender.setText("Gender: " + (gender != null ? gender : "N/A"));
        tvPassFood.setText("Food Pass: " + (foodPass ? "Included" : "Not Included"));

        btnConfirm.setOnClickListener(v -> showConfirmDialog());
        btnShare.setOnClickListener(v -> shareOnWhatsApp());
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Pass?")
                .setMessage("Are you sure you want to confirm this pass?")
                .setPositiveButton("OK", (dialog, which) -> checkNotificationPermissionAndSend())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void checkNotificationPermissionAndSend() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                sendNotification();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            sendNotification();
        }
    }

    private void sendNotification() {
        String channelId = "pass_confirmation_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Pass Confirmations",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("College Event Pass")
                .setContentText("Pass Confirmed for " + eventName + "!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify(1, builder.build());
        Toast.makeText(this, "Notification Sent!", Toast.LENGTH_SHORT).show();
    }

    private void shareOnWhatsApp() {
        String shareText = "🎫 *OFFICIAL EVENT PASS RECEIPT* 🎫\n\n" +
                "🔹 *Pass ID:* " + uniquePassId + "\n" +
                "🔹 *Receipt/Txn ID:* " + txnId + "\n" +
                "🔹 *Issued On:* " + issuedTime + "\n\n" +
                "👤 *Attendee Details*\n" +
                "Name: " + studentName + "\n" +
                "Roll No: " + rollNo + "\n" +
                "Gender: " + gender + "\n\n" +
                "📅 *Event Details*\n" +
                "Event: " + eventName + "\n" +
                "Date: " + date + "\n" +
                "Food Pass: " + (foodPass ? "✅ Yes" : "❌ No") + "\n\n" +
                "💰 *Total Amount:* " + totalFeeStr + "\n\n" +
                "⚠️ *Volunteer Instructions:* Please verify the Pass ID and Roll Number at the entry gate.";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        sendIntent.setType("text/plain");
        
        // To specifically target WhatsApp:
        sendIntent.setPackage("com.whatsapp");
        
        try {
            startActivity(sendIntent);
        } catch (Exception e) {
            // Fallback to standard share sheet if WhatsApp is not installed
            sendIntent.setPackage(null);
            startActivity(Intent.createChooser(sendIntent, "Share pass using"));
        }
    }
}
