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
import java.util.Arrays;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.LinearLayout;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.app.AlertDialog;
import java.util.UUID;
import android.widget.AdapterView;
import java.util.Collections;
import java.util.Comparator;

public class BookingsFragment extends Fragment {
    private Room selectedRoom = null;
    private static final String[] ROOM_TAG_OPTIONS = new String[] {
        "Ocean View Suite", "Deluxe Room", "Family Room", "Executive Suite", "Pool Access", "Balcony", "King Bed", "Twin Bed"
    };
    private ArrayList<Room> allRooms = new ArrayList<>();
    private ArrayList<Room> filteredRooms = new ArrayList<>();
    private EditText roomSearch;
    private ArrayList<android.widget.CheckBox> tagCheckBoxes;
    private RoomsAdapter roomsAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        RecyclerView roomsRecyclerView = view.findViewById(R.id.rooms_recycler_view);
        roomSearch = view.findViewById(R.id.room_search);

        roomsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        roomsAdapter = new RoomsAdapter(filteredRooms, room -> showBookNowDialog(room));
        roomsRecyclerView.setAdapter(roomsAdapter);
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allRooms.clear();
                filteredRooms.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    Room room = roomSnap.getValue(Room.class);
                    if (room != null) allRooms.add(room);
                }
                filteredRooms.addAll(allRooms);
                roomsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });

        // Search functionality
        LinearLayout tagFilterLayout = view.findViewById(R.id.tag_filter_layout);
        tagCheckBoxes = new ArrayList<>();
        for (String tag : ROOM_TAG_OPTIONS) {
            android.widget.CheckBox cb = new android.widget.CheckBox(getContext());
            cb.setText(tag);
            cb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            cb.setTextColor(getResources().getColor(R.color.colorPrimary));
            cb.setPadding(24, 8, 24, 8);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            cb.setLayoutParams(params);
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(32);
            bg.setStroke(2, getResources().getColor(R.color.colorPrimary));
            bg.setColor(getResources().getColor(android.R.color.transparent));
            cb.setBackground(bg);
            tagCheckBoxes.add(cb);
            tagFilterLayout.addView(cb);
        }
        TextWatcher filterWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRooms();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };
        roomSearch.addTextChangedListener(filterWatcher);
        for (android.widget.CheckBox cb : tagCheckBoxes) {
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> filterRooms());
        }

        Spinner sortPriceSpinner = view.findViewById(R.id.sort_price_spinner);
        sortPriceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortRoomsByPrice(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return view;
    }

    private void filterRooms() {
        String query = roomSearch.getText().toString().toLowerCase();
        ArrayList<String> selectedTags = new ArrayList<>();
        for (android.widget.CheckBox cb : tagCheckBoxes) {
            if (cb.isChecked()) selectedTags.add(cb.getText().toString());
        }
        filteredRooms.clear();
        for (Room room : allRooms) {
            boolean matchesText = room.name.toLowerCase().contains(query) ||
                    room.description.toLowerCase().contains(query) ||
                    (room.tags != null && room.tags.toString().toLowerCase().contains(query));
            boolean matchesTag = selectedTags.isEmpty() || (room.tags != null && !room.tags.isEmpty() && !selectedTags.isEmpty() && !disjoint(room.tags, selectedTags));
            if (matchesText && matchesTag) filteredRooms.add(room);
        }
        Spinner sortPriceSpinner = getView().findViewById(R.id.sort_price_spinner);
        int sortOption = sortPriceSpinner.getSelectedItemPosition();
        sortRoomsByPrice(sortOption);
        roomsAdapter.notifyDataSetChanged();
    }

    private boolean disjoint(java.util.List<String> a, java.util.List<String> b) {
        for (String s : a) if (b.contains(s)) return false;
        return true;
    }

    private void showBookNowDialog(Room room) {
        AlertDialog dialog = new AlertDialog.Builder(getContext()).create();
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_book_room, null);
        TextView roomName = dialogView.findViewById(R.id.dialog_room_name);
        EditText checkInDate = dialogView.findViewById(R.id.dialog_checkin_date);
        EditText checkOutDate = dialogView.findViewById(R.id.dialog_checkout_date);
        Spinner adultsSpinner = dialogView.findViewById(R.id.dialog_adults_spinner);
        Spinner childrenSpinner = dialogView.findViewById(R.id.dialog_children_spinner);
        EditText specialRequests = dialogView.findViewById(R.id.dialog_special_requests);
        Button bookBtn = dialogView.findViewById(R.id.dialog_book_btn);
        roomName.setText(room.name);
        // Setup spinners
        String[] adultsOptions = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adultsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, adultsOptions);
        adultsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adultsSpinner.setAdapter(adultsAdapter);
        String[] childrenOptions = {"0", "1", "2", "3", "4", "5"};
        ArrayAdapter<String> childrenAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, childrenOptions);
        childrenAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        childrenSpinner.setAdapter(childrenAdapter);
        // Date pickers
        checkInDate.setFocusable(false);
        checkInDate.setOnClickListener(v -> showDatePicker(checkInDate));
        checkOutDate.setFocusable(false);
        checkOutDate.setOnClickListener(v -> showDatePicker(checkOutDate));
        bookBtn.setOnClickListener(v -> {
            String checkIn = checkInDate.getText().toString().trim();
            String checkOut = checkOutDate.getText().toString().trim();
            String adults = adultsSpinner.getSelectedItem().toString();
            String children = childrenSpinner.getSelectedItem().toString();
            String note = specialRequests.getText().toString().trim();
            if (TextUtils.isEmpty(checkIn) || TextUtils.isEmpty(checkOut)) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // New validation: check if check-in date is in the past
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                java.util.Date checkInDateObj = sdf.parse(checkIn);
                java.util.Date today = new java.util.Date();
                // Remove time part from today
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(today);
                cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
                cal.set(java.util.Calendar.MINUTE, 0);
                cal.set(java.util.Calendar.SECOND, 0);
                cal.set(java.util.Calendar.MILLISECOND, 0);
                today = cal.getTime();
                if (checkInDateObj.before(today)) {
                    Toast.makeText(getContext(), "Check-in date cannot be in the past", Toast.LENGTH_LONG).show();
                    return;
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Invalid check-in date format", Toast.LENGTH_SHORT).show();
                return;
            }
            String userEmail = getActivity().getSharedPreferences("session", Context.MODE_PRIVATE).getString("user_email", "");
            if (userEmail == null || userEmail.isEmpty()) {
                Toast.makeText(getContext(), "User session error: Please log in again.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
                return;
            }
            String userId = userEmail.replace("@", "_").replace(".", "_"); // fallback if no UID
            DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
            // Check for overlapping bookings for the same room
            bookingsRef.orderByChild("roomName").equalTo(room.name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean overlap = false;
                    for (DataSnapshot bookingSnap : snapshot.getChildren()) {
                        Booking existing = bookingSnap.getValue(Booking.class);
                        if (existing != null) {
                            // Check for overlap
                            if (datesOverlap(checkIn, checkOut, existing.checkInDate, existing.checkOutDate)) {
                                overlap = true;
                                break;
                            }
                        }
                    }
                    if (overlap) {
                        Toast.makeText(getContext(), "This room is already booked for the selected dates.", Toast.LENGTH_LONG).show();
                    } else {
                        String bookingId = UUID.randomUUID().toString();
                        Booking booking = new Booking(bookingId, userId, userEmail, room.name, checkIn, checkOut, Integer.parseInt(adults), Integer.parseInt(children), note);
                        bookingsRef.child(bookingId).setValue(booking);
                        Toast.makeText(getContext(), "Room booked successfully!", Toast.LENGTH_LONG).show();
                        Toast.makeText(getContext(), "Booking reminder will appear on the day of your booking.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(getContext(), "Error checking room availability.", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.setView(dialogView);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (d, w) -> dialog.dismiss());
        dialog.show();
    }

    private void showDatePicker(final EditText target) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        new android.app.DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            String date = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            target.setText(date);
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void sortRoomsByPrice(int sortOption) {
        if (sortOption == 0) {
            // Ascending
            Collections.sort(filteredRooms, Comparator.comparingDouble(room -> room.price));
        } else {
            // Descending
            Collections.sort(filteredRooms, (a, b) -> Double.compare(b.price, a.price));
        }
        roomsAdapter.notifyDataSetChanged();
    }

    private boolean datesOverlap(String start1, String end1, String start2, String end2) {
        // Dates are in format YYYY-MM-DD
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date s1 = sdf.parse(start1);
            java.util.Date e1 = sdf.parse(end1);
            java.util.Date s2 = sdf.parse(start2);
            java.util.Date e2 = sdf.parse(end2);
            return !e1.before(s2) && !e2.before(s1); // overlap if ranges intersect
        } catch (Exception e) {
            return false;
        }
    }
} 