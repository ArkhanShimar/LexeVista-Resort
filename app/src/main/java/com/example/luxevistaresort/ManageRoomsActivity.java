package com.example.luxevistaresort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.luxevistaresort.AdminRoomsAdapter;

public class ManageRoomsActivity extends Activity {
    private RecyclerView recyclerView;
    private AdminRoomsAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private List<Room> fullRoomList = new ArrayList<>();
    private DatabaseReference roomsRef;
    private EditText searchInput;

    private static final String[] ROOM_TAG_OPTIONS = new String[] {
        "Ocean View Suite", "Deluxe Room", "Family Room", "Executive Suite", "Pool Access", "Balcony", "King Bed", "Twin Bed"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_rooms);

        recyclerView = findViewById(R.id.rooms_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminRoomsAdapter(roomList, new AdminRoomsAdapter.OnRoomLongClickListener() {
            @Override
            public void onRoomLongClick(Room room) {
                showEditRoomDialog(room);
            }
        }, new AdminRoomsAdapter.OnRoomDeleteListener() {
            @Override
            public void onRoomDelete(Room room) {
                new AlertDialog.Builder(ManageRoomsActivity.this)
                    .setTitle("Delete Room")
                    .setMessage("Are you sure you want to delete this room?")
                    .setPositiveButton("Delete", (dialog, which) -> roomsRef.child(room.id).removeValue())
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
        recyclerView.setAdapter(adapter);

        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");

        loadRooms();

        FloatingActionButton fab = findViewById(R.id.add_room_fab);
        fab.setOnClickListener(v -> showAddRoomDialog());

        searchInput = findViewById(R.id.search_room_input);
        searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRooms(s.toString());
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void loadRooms() {
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                fullRoomList.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    Room room = roomSnap.getValue(Room.class);
                    if (room != null) {
                        roomList.add(room);
                        fullRoomList.add(room);
                    }
                }
                filterRooms(searchInput.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageRoomsActivity.this, "Failed to load rooms", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterRooms(String query) {
        List<Room> filtered = new ArrayList<>();
        for (Room room : fullRoomList) {
            if (room.name.toLowerCase().contains(query.toLowerCase()) ||
                room.description.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(room);
            }
        }
        adapter.setRoomList(filtered);
    }

    private void showAddRoomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Room");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Room Name");
        layout.addView(nameInput);

        final EditText descInput = new EditText(this);
        descInput.setHint("Description");
        layout.addView(descInput);

        final EditText priceInput = new EditText(this);
        priceInput.setHint("Price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(priceInput);

        final EditText capacityInput = new EditText(this);
        capacityInput.setHint("How many people can stay");
        capacityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(capacityInput);

        final EditText imageNameInput = new EditText(this);
        imageNameInput.setHint("Image Name (e.g., room1)");
        layout.addView(imageNameInput);

        final android.widget.Switch acSwitch = new android.widget.Switch(this);
        acSwitch.setText("AC Room");
        layout.addView(acSwitch);

        // Add checkboxes for tags
        final List<android.widget.CheckBox> tagCheckBoxes = new ArrayList<>();
        LinearLayout tagsLayout = new LinearLayout(this);
        tagsLayout.setOrientation(LinearLayout.VERTICAL);
        tagsLayout.setPadding(0, 16, 0, 0);
        for (String tag : ROOM_TAG_OPTIONS) {
            android.widget.CheckBox cb = new android.widget.CheckBox(this);
            cb.setText(tag);
            tagCheckBoxes.add(cb);
            tagsLayout.addView(cb);
        }
        layout.addView(tagsLayout);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String capacityStr = capacityInput.getText().toString().trim();
            String imageName = imageNameInput.getText().toString().trim();
            boolean isAC = acSwitch.isChecked();
            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty() || imageName.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);
            int capacity = Integer.parseInt(capacityStr);
            List<String> selectedTags = new ArrayList<>();
            for (android.widget.CheckBox cb : tagCheckBoxes) {
                if (cb.isChecked()) selectedTags.add(cb.getText().toString());
            }
            String id = UUID.randomUUID().toString();
            Room room = new Room(id, name, desc, price, imageName, selectedTags, isAC, capacity);
            roomsRef.child(id).setValue(room);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditRoomDialog(Room room) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Room");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText nameInput = new EditText(this);
        nameInput.setText(room.name);
        layout.addView(nameInput);

        final EditText descInput = new EditText(this);
        descInput.setText(room.description);
        layout.addView(descInput);

        final EditText priceInput = new EditText(this);
        priceInput.setText(String.valueOf(room.price));
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(priceInput);

        final EditText capacityInput = new EditText(this);
        capacityInput.setHint("How many people can stay");
        capacityInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        capacityInput.setText(String.valueOf(room.capacity));
        layout.addView(capacityInput);

        final EditText imageNameInput = new EditText(this);
        imageNameInput.setHint("Image Name (e.g., room1)");
        imageNameInput.setText(room.imageUrl);
        layout.addView(imageNameInput);

        final android.widget.Switch acSwitch = new android.widget.Switch(this);
        acSwitch.setText("AC Room");
        acSwitch.setChecked(room.isAC);
        layout.addView(acSwitch);

        // Add checkboxes for tags, pre-check those already in room.tags
        final List<android.widget.CheckBox> tagCheckBoxes = new ArrayList<>();
        LinearLayout tagsLayout = new LinearLayout(this);
        tagsLayout.setOrientation(LinearLayout.VERTICAL);
        tagsLayout.setPadding(0, 16, 0, 0);
        for (String tag : ROOM_TAG_OPTIONS) {
            android.widget.CheckBox cb = new android.widget.CheckBox(this);
            cb.setText(tag);
            if (room.tags != null && room.tags.contains(tag)) cb.setChecked(true);
            tagCheckBoxes.add(cb);
            tagsLayout.addView(cb);
        }
        layout.addView(tagsLayout);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String capacityStr = capacityInput.getText().toString().trim();
            String imageName = imageNameInput.getText().toString().trim();
            boolean isAC = acSwitch.isChecked();
            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || capacityStr.isEmpty() || imageName.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);
            int capacity = Integer.parseInt(capacityStr);
            List<String> selectedTags = new ArrayList<>();
            for (android.widget.CheckBox cb : tagCheckBoxes) {
                if (cb.isChecked()) selectedTags.add(cb.getText().toString());
            }
            Room updatedRoom = new Room(room.id, name, desc, price, imageName, selectedTags, isAC, capacity);
            roomsRef.child(room.id).setValue(updatedRoom);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
} 