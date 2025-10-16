package com.example.luxevistaresort;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button roomsBtn = findViewById(R.id.btn_manage_rooms);
        Button servicesBtn = findViewById(R.id.btn_manage_services);
        Button promotionsBtn = findViewById(R.id.btn_manage_promotions);
        Button usersBtn = findViewById(R.id.btn_view_users);
        Button logoutBtn = findViewById(R.id.btn_logout);
        Button sendNotifBtn = findViewById(R.id.btn_send_notification);

        roomsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageRoomsActivity.class));
        });
        servicesBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageServicesActivity.class));
        });
        promotionsBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, ManagePromotionsActivity.class));
        });
        usersBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminUsersActivity.class));
        });
        logoutBtn.setOnClickListener(v -> {
            SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
            session.edit().clear().apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
        sendNotifBtn.setOnClickListener(v -> showSendNotificationDialog());
    }

    private void showSendNotificationDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_send_notification, null);
        EditText titleInput = dialogView.findViewById(R.id.notif_title);
        EditText messageInput = dialogView.findViewById(R.id.notif_message);
        Spinner userSpinner = dialogView.findViewById(R.id.notif_user_spinner);

        List<String> userIds = new ArrayList<>();
        userIds.add("All Guests");
        // Fetch user IDs from Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String email = userSnap.child("email").getValue(String.class);
                    if (email != null) {
                        String userId = email.replace("@", "_").replace(".", "_");
                        userIds.add(userId);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(AdminDashboardActivity.this, android.R.layout.simple_spinner_item, userIds);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                userSpinner.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        new AlertDialog.Builder(this)
            .setTitle("Send Notification")
            .setView(dialogView)
            .setPositiveButton("Send", (d, w) -> {
                String title = titleInput.getText().toString().trim();
                String message = messageInput.getText().toString().trim();
                String selectedUser = userSpinner.getSelectedItem().toString();
                sendNotificationToFirebase(title, message, selectedUser.equals("All Guests") ? null : selectedUser);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void sendNotificationToFirebase(String title, String message, String userId) {
        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("notifications");
        String notifId = notifRef.push().getKey();
        Notification notif = new Notification(notifId, title, message, System.currentTimeMillis());
        if (userId == null) {
            notifRef.child("all").child(notifId).setValue(notif);
        } else {
            notifRef.child(userId).child(notifId).setValue(notif);
        }
        Toast.makeText(this, "Notification sent!", Toast.LENGTH_SHORT).show();
    }
} 