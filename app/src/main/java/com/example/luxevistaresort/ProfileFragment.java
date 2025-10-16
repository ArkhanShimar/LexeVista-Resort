package com.example.luxevistaresort;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import android.text.TextUtils;
import android.widget.LinearLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView nameText = view.findViewById(R.id.user_name);
        TextView emailText = view.findViewById(R.id.user_email);
        Button logoutBtn = view.findViewById(R.id.btn_logout);
        Button editProfileBtn = view.findViewById(R.id.btn_edit_profile);
        Button notifBtn = view.findViewById(R.id.btn_notifications);
        LinearLayout bookingHistoryList = view.findViewById(R.id.booking_history_list);
        LinearLayout reservationHistoryList = view.findViewById(R.id.reservation_history_list);
        bookingHistoryList.removeAllViews();
        reservationHistoryList.removeAllViews();

        // Declare shared variables at the top
        SharedPreferences session = getActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String email = session.getString("user_email", "");
        SharedPreferences users = getActivity().getSharedPreferences("users", Context.MODE_PRIVATE);
        String saved = users.getString(email, null);
        String name = "";
        if (saved != null) {
            String[] parts = saved.split(":");
            name = parts[0];
        }
        if (name == null || name.trim().isEmpty()) {
            name = "Guest";
        }
        nameText.setText(name);
        emailText.setText(email);
        
        // Show booking history from Firebase
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
        bookingsRef.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                bookingHistoryList.removeAllViews();
                Context context = getContext();
                if (context == null) return;
                for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                    Booking booking = bookingSnap.getValue(Booking.class);
                    if (booking != null) {
                        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(context);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cardParams.setMargins(0, 0, 0, 16);
                        card.setLayoutParams(cardParams);
                        card.setCardElevation(8);
                        card.setRadius(32);
                        card.setCardBackgroundColor(context.getResources().getColor(R.color.colorCard));
                        LinearLayout content = new LinearLayout(context);
                        content.setOrientation(LinearLayout.VERTICAL);
                        content.setPadding(32, 24, 32, 24);
                        TextView title = new TextView(context);
                        title.setText("Room Booking");
                        title.setTextColor(context.getResources().getColor(R.color.colorText));
                        title.setTextSize(16);
                        title.setTypeface(null, android.graphics.Typeface.BOLD);
                        TextView details = new TextView(context);
                        StringBuilder bookingDetails = new StringBuilder();
                        bookingDetails.append("Room: ").append(booking.roomName).append("\n");
                        bookingDetails.append("Check-in: ").append(booking.checkInDate).append("\n");
                        bookingDetails.append("Check-out: ").append(booking.checkOutDate).append("\n");
                        bookingDetails.append("Adults: ").append(booking.adults).append("\n");
                        bookingDetails.append("Children: ").append(booking.children).append("\n");
                        if (booking.specialRequests != null && !booking.specialRequests.isEmpty()) {
                            bookingDetails.append("Special Requests: ").append(booking.specialRequests).append("\n");
                        }
                        details.setText(bookingDetails.toString().trim());
                        details.setTextColor(context.getResources().getColor(R.color.colorText));
                        details.setTextSize(15);
                        details.setPadding(0, 8, 0, 8);
                        Button cancelBtn = new Button(context);
                        cancelBtn.setText("Cancel");
                        cancelBtn.setTextColor(context.getResources().getColor(android.R.color.white));
                        cancelBtn.setBackgroundResource(R.drawable.rounded_red_button);
                        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        btnParams.topMargin = 16;
                        btnParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        btnParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        cancelBtn.setLayoutParams(btnParams);
                        cancelBtn.setPadding(32, 8, 32, 8);
                        cancelBtn.setOnClickListener(v2 -> {
                            new android.app.AlertDialog.Builder(context)
                                .setTitle("Cancel Booking")
                                .setMessage("Are you sure you want to cancel this booking?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    bookingsRef.child(booking.id).removeValue();
                                    bookingHistoryList.removeView(card);
                                })
                                .setNegativeButton("No", null)
                                .show();
                        });
                        content.addView(title);
                        content.addView(details);
                        content.addView(cancelBtn);
                        card.addView(content);
                        bookingHistoryList.addView(card);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
        
        // Show service reservation history from Firebase
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");
        reservationsRef.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reservationHistoryList.removeAllViews();
                Context context = getContext();
                if (context == null) return;
                for (DataSnapshot reservationSnap : snapshot.getChildren()) {
                    Reservation reservation = reservationSnap.getValue(Reservation.class);
                    if (reservation != null) {
                        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(context);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        cardParams.setMargins(0, 0, 0, 16);
                        card.setLayoutParams(cardParams);
                        card.setCardElevation(8);
                        card.setRadius(32);
                        card.setCardBackgroundColor(context.getResources().getColor(R.color.colorCard));
                        LinearLayout content = new LinearLayout(context);
                        content.setOrientation(LinearLayout.VERTICAL);
                        content.setPadding(32, 24, 32, 24);
                        TextView title = new TextView(context);
                        title.setText("Service Reservation");
                        title.setTextColor(context.getResources().getColor(R.color.colorText));
                        title.setTextSize(16);
                        title.setTypeface(null, android.graphics.Typeface.BOLD);
                        TextView details = new TextView(context);
                        StringBuilder reservationDetails = new StringBuilder();
                        reservationDetails.append("Service: ").append(reservation.serviceName).append("\n");
                        reservationDetails.append("Date: ").append(reservation.date).append("\n");
                        reservationDetails.append("Time: ").append(reservation.time).append("\n");
                        details.setText(reservationDetails.toString().trim());
                        details.setTextColor(context.getResources().getColor(R.color.colorText));
                        details.setTextSize(15);
                        details.setPadding(0, 8, 0, 8);
                        Button cancelBtn = new Button(context);
                        cancelBtn.setText("Cancel");
                        cancelBtn.setTextColor(context.getResources().getColor(android.R.color.white));
                        cancelBtn.setBackgroundResource(R.drawable.rounded_red_button);
                        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        btnParams.topMargin = 16;
                        btnParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        btnParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        cancelBtn.setLayoutParams(btnParams);
                        cancelBtn.setPadding(32, 8, 32, 8);
                        cancelBtn.setOnClickListener(v2 -> {
                            new android.app.AlertDialog.Builder(context)
                                .setTitle("Cancel Reservation")
                                .setMessage("Are you sure you want to cancel this reservation?")
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    reservationsRef.child(reservation.id).removeValue();
                                    reservationHistoryList.removeView(card);
                                })
                                .setNegativeButton("No", null)
                                .show();
                        });
                        content.addView(title);
                        content.addView(details);
                        content.addView(cancelBtn);
                        card.addView(content);
                        reservationHistoryList.addView(card);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        editProfileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(getContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    session.edit().clear().apply();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    getActivity().finish();
                })
                .setNegativeButton("No", null)
                .show();
        });

        notifBtn.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new NotificationsFragment())
                .addToBackStack(null)
                .commit();
        });

        return view;
    }
} 