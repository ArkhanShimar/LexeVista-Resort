package com.example.luxevistaresort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class AdminUserDetailsActivity extends Activity {
    private String userUid;
    private User user;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_details);

        userUid = getIntent().getStringExtra("user_uid");
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadUser();
    }

    private void loadUser() {
        if (userUid == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        usersRef.orderByChild("email").equalTo(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    user = userSnap.getValue(User.class);
                    userUid = userSnap.getKey();
                    break;
                }
                setupViews();
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminUserDetailsActivity.this, "Failed to load user", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setupViews() {
        TextView nameText = findViewById(R.id.detail_user_name);
        TextView emailText = findViewById(R.id.detail_user_email);
        TextView addressText = findViewById(R.id.detail_user_address);
        TextView dobText = findViewById(R.id.detail_user_dob);
        Button editBtn = findViewById(R.id.btn_edit_user);
        Button deleteBtn = findViewById(R.id.btn_delete_user);
        LinearLayout bookingHistoryList = findViewById(R.id.detail_booking_history_list);
        LinearLayout reservationHistoryList = findViewById(R.id.detail_reservation_history_list);

        if (user != null) {
            nameText.setText(user.name);
            emailText.setText(user.email);
            addressText.setText(user.address != null ? user.address : "");
            dobText.setText(user.dob != null ? user.dob : "");
        }

        // Load and show booking history from Firebase
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        bookingsRef.orderByChild("userEmail").equalTo(user.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookingHistoryList.removeAllViews();
                Toast.makeText(AdminUserDetailsActivity.this, "Found " + snapshot.getChildrenCount() + " bookings for " + user.email, Toast.LENGTH_SHORT).show();
                for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                    Booking booking = bookingSnap.getValue(Booking.class);
                    if (booking != null) {
                        LinearLayout barLayout = new LinearLayout(AdminUserDetailsActivity.this);
                        barLayout.setOrientation(LinearLayout.VERTICAL);
                        barLayout.setPadding(32, 24, 32, 24);
                        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        barParams.setMargins(0, 0, 0, 24);
                        barLayout.setLayoutParams(barParams);
                        barLayout.setBackgroundResource(R.drawable.rounded_card); // Use a drawable with bg color and rounded corners

                        TextView roomLabel = new TextView(AdminUserDetailsActivity.this);
                        roomLabel.setText("Room:");
                        roomLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        roomLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView roomValue = new TextView(AdminUserDetailsActivity.this);
                        roomValue.setText(booking.roomName);
                        roomValue.setTextColor(getResources().getColor(R.color.colorText));

                        TextView checkInLabel = new TextView(AdminUserDetailsActivity.this);
                        checkInLabel.setText("Check-in:");
                        checkInLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        checkInLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView checkInValue = new TextView(AdminUserDetailsActivity.this);
                        checkInValue.setText(booking.checkInDate);
                        checkInValue.setTextColor(getResources().getColor(R.color.colorText));

                        TextView checkOutLabel = new TextView(AdminUserDetailsActivity.this);
                        checkOutLabel.setText("Check-out:");
                        checkOutLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        checkOutLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView checkOutValue = new TextView(AdminUserDetailsActivity.this);
                        checkOutValue.setText(booking.checkOutDate);
                        checkOutValue.setTextColor(getResources().getColor(R.color.colorText));

                        TextView adultsLabel = new TextView(AdminUserDetailsActivity.this);
                        adultsLabel.setText("Adults:");
                        adultsLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        adultsLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView adultsValue = new TextView(AdminUserDetailsActivity.this);
                        adultsValue.setText(String.valueOf(booking.adults));
                        adultsValue.setTextColor(getResources().getColor(R.color.colorText));

                        TextView childrenLabel = new TextView(AdminUserDetailsActivity.this);
                        childrenLabel.setText("Children:");
                        childrenLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        childrenLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView childrenValue = new TextView(AdminUserDetailsActivity.this);
                        childrenValue.setText(String.valueOf(booking.children));
                        childrenValue.setTextColor(getResources().getColor(R.color.colorText));

                        barLayout.addView(roomLabel);
                        barLayout.addView(roomValue);
                        barLayout.addView(checkInLabel);
                        barLayout.addView(checkInValue);
                        barLayout.addView(checkOutLabel);
                        barLayout.addView(checkOutValue);
                        barLayout.addView(adultsLabel);
                        barLayout.addView(adultsValue);
                        barLayout.addView(childrenLabel);
                        barLayout.addView(childrenValue);

                        if (booking.specialRequests != null && !booking.specialRequests.isEmpty()) {
                            TextView specialLabel = new TextView(AdminUserDetailsActivity.this);
                            specialLabel.setText("Special Requests:");
                            specialLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                            specialLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                            TextView specialValue = new TextView(AdminUserDetailsActivity.this);
                            specialValue.setText(booking.specialRequests);
                            specialValue.setTextColor(getResources().getColor(R.color.colorText));
                            barLayout.addView(specialLabel);
                            barLayout.addView(specialValue);
                        }
                        bookingHistoryList.addView(barLayout);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminUserDetailsActivity.this, "Failed to load bookings: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        // Load and show reservation history from Firebase
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");
        reservationsRef.orderByChild("userEmail").equalTo(user.email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reservationHistoryList.removeAllViews();
                Toast.makeText(AdminUserDetailsActivity.this, "Found " + snapshot.getChildrenCount() + " reservations for " + user.email, Toast.LENGTH_SHORT).show();
                for (DataSnapshot reservationSnap : snapshot.getChildren()) {
                    Reservation reservation = reservationSnap.getValue(Reservation.class);
                    if (reservation != null) {
                        LinearLayout barLayout = new LinearLayout(AdminUserDetailsActivity.this);
                        barLayout.setOrientation(LinearLayout.VERTICAL);
                        barLayout.setPadding(32, 24, 32, 24);
                        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        barParams.setMargins(0, 0, 0, 24);
                        barLayout.setLayoutParams(barParams);
                        barLayout.setBackgroundResource(R.drawable.rounded_card); // Use a drawable with bg color and rounded corners

                        TextView serviceLabel = new TextView(AdminUserDetailsActivity.this);
                        serviceLabel.setText("Service:");
                        serviceLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        serviceLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView serviceValue = new TextView(AdminUserDetailsActivity.this);
                        serviceValue.setText(reservation.serviceName);
                        serviceValue.setTextColor(getResources().getColor(R.color.colorText));

                        TextView dateLabel = new TextView(AdminUserDetailsActivity.this);
                        dateLabel.setText("Date:");
                        dateLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        dateLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView dateValue = new TextView(AdminUserDetailsActivity.this);
                        dateValue.setText(reservation.date);
                        dateValue.setTextColor(getResources().getColor(R.color.colorText));

                        TextView timeLabel = new TextView(AdminUserDetailsActivity.this);
                        timeLabel.setText("Time:");
                        timeLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                        timeLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                        TextView timeValue = new TextView(AdminUserDetailsActivity.this);
                        timeValue.setText(reservation.time);
                        timeValue.setTextColor(getResources().getColor(R.color.colorText));

                        barLayout.addView(serviceLabel);
                        barLayout.addView(serviceValue);
                        barLayout.addView(dateLabel);
                        barLayout.addView(dateValue);
                        barLayout.addView(timeLabel);
                        barLayout.addView(timeValue);

                        reservationHistoryList.addView(barLayout);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminUserDetailsActivity.this, "Failed to load reservations: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("user_uid", userUid);
            startActivity(intent);
        });
        deleteBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    usersRef.child(userUid).removeValue();
                    Toast.makeText(this, "User deleted", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
        });
    }
} 