package com.example.luxevistaresort;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.Arrays;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.recyclerview.widget.GridLayoutManager;
import java.util.UUID;

public class ServicesFragment extends Fragment {
    private ArrayList<String> serviceTypeList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);
        RecyclerView servicesRecyclerView = view.findViewById(R.id.services_recycler_view);
        EditText dateInput = view.findViewById(R.id.service_date);
        EditText timeInput = view.findViewById(R.id.service_time);
        Button reserveBtn = view.findViewById(R.id.reserve_service_btn);

        // Load and display reservation history from Firebase
        SharedPreferences session = getActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String email = session.getString("user_email", "");
        DatabaseReference reservationsRef = FirebaseDatabase.getInstance().getReference("reservations");
        reservationsRef.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<Reservation> reservations = new ArrayList<>();
                for (DataSnapshot reservationSnap : snapshot.getChildren()) {
                    Reservation reservation = reservationSnap.getValue(Reservation.class);
                    if (reservation != null) {
                        reservations.add(reservation);
                    }
                }
                updateReservationHistory(reservations);
                
                // Show reminder if any reservation is for today
                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                boolean hasTodayReservation = false;
                for (Reservation r : reservations) {
                    if (r.date.equals(today)) {
                        hasTodayReservation = true;
                        break;
                    }
                }
                if (hasTodayReservation) {
                    Toast.makeText(getContext(), "Reminder: You have a service reservation today!", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Only use servicesRecyclerView for displaying services
        ArrayList<Service> filteredServices = new ArrayList<>();
        ServiceAdapter serviceAdapter = new ServiceAdapter(filteredServices, service -> {});
        servicesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        servicesRecyclerView.setAdapter(serviceAdapter);
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                filteredServices.clear();
                for (DataSnapshot serviceSnap : snapshot.getChildren()) {
                    Service service = serviceSnap.getValue(Service.class);
                    if (service != null) {
                        filteredServices.add(service);
                    }
                }
                serviceAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Calendar and clock pickers for date and time
        dateInput.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            new android.app.DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                String date = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                dateInput.setText(date);
            }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
        });
        timeInput.setOnClickListener(v -> {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            new android.app.TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {
                String time = String.format(java.util.Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                timeInput.setText(time);
            }, calendar.get(java.util.Calendar.HOUR_OF_DAY), calendar.get(java.util.Calendar.MINUTE), true).show();
        });

        reserveBtn.setOnClickListener(v -> {
            Service selectedService = serviceAdapter.getSelectedService();
            String date = dateInput.getText().toString().trim();
            String time = timeInput.getText().toString().trim();
            if (selectedService == null || TextUtils.isEmpty(date) || TextUtils.isEmpty(time)) {
                Toast.makeText(getContext(), "Please select a service and fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Save reservation to Firebase
            String reservationId = UUID.randomUUID().toString();
            String userEmail = getActivity().getSharedPreferences("session", Context.MODE_PRIVATE).getString("user_email", "");
            String userId = userEmail.replace("@", "_").replace(".", "_"); // fallback if no UID
            Reservation reservation = new Reservation(reservationId, userId, userEmail, selectedService.name, date, time);
            reservationsRef.child(reservationId).setValue(reservation);
            Toast.makeText(getContext(), "Service reserved successfully!", Toast.LENGTH_LONG).show();
            Toast.makeText(getContext(), "Reservation reminder will appear on the day of your reservation.", Toast.LENGTH_SHORT).show();
            dateInput.setText("");
            timeInput.setText("");
        });
        return view;
    }

    private void updateReservationHistory(ArrayList<Reservation> reservations) {
        // This method can be used to update UI if needed
        // For now, we're loading directly in the ValueEventListener
    }
} 