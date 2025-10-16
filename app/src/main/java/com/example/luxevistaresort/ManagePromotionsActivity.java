package com.example.luxevistaresort;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

public class ManagePromotionsActivity extends Activity {
    private RecyclerView recyclerView;
    private PromotionsAdapter adapter;
    private List<Promotion> promotionList = new ArrayList<>();
    private DatabaseReference promotionsRef;
    private List<Room> roomList = new ArrayList<>();
    private List<Service> serviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_promotions);

        recyclerView = findViewById(R.id.promotions_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PromotionsAdapter(promotionList, new PromotionsAdapter.OnPromotionLongClickListener() {
            @Override
            public void onPromotionLongClick(Promotion promotion) {
                showEditDeleteDialog(promotion);
            }
        });
        recyclerView.setAdapter(adapter);

        promotionsRef = FirebaseDatabase.getInstance().getReference("promotions");
        loadPromotions();
        loadRoomsAndServices();

        FloatingActionButton fab = findViewById(R.id.add_promotion_fab);
        fab.setOnClickListener(v -> showAddPromotionDialog());

        adapter.setOnPromotionEditListener(promo -> showEditPromotionDialog(promo));
        adapter.setOnPromotionDeleteListener(promo -> {
            new AlertDialog.Builder(this)
                .setTitle("Delete Promotion")
                .setMessage("Are you sure you want to delete this promotion?")
                .setPositiveButton("Yes", (dialog, which) -> promotionsRef.child(promo.id).removeValue())
                .setNegativeButton("No", null)
                .show();
        });
    }

    private void loadPromotions() {
        promotionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                promotionList.clear();
                for (DataSnapshot promoSnap : snapshot.getChildren()) {
                    Promotion promo = promoSnap.getValue(Promotion.class);
                    if (promo != null) promotionList.add(promo);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManagePromotionsActivity.this, "Failed to load promotions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadRoomsAndServices() {
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    Room room = roomSnap.getValue(Room.class);
                    if (room != null) roomList.add(room);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serviceList.clear();
                for (DataSnapshot serviceSnap : snapshot.getChildren()) {
                    Service service = serviceSnap.getValue(Service.class);
                    if (service != null) serviceList.add(service);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void showAddPromotionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Promotion");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        final EditText titleInput = new EditText(this);
        titleInput.setHint("Title");
        layout.addView(titleInput);
        final EditText descInput = new EditText(this);
        descInput.setHint("Description");
        layout.addView(descInput);
        final EditText badgeInput = new EditText(this);
        badgeInput.setHint("Badge (e.g. 20% OFF)");
        layout.addView(badgeInput);
        final EditText imageInput = new EditText(this);
        imageInput.setHint("Image Resource Name (e.g. ic_launcher_background)");
        layout.addView(imageInput);
        final EditText validUntilInput = new EditText(this);
        validUntilInput.setHint("Valid Until (YYYY-MM-DD)");
        layout.addView(validUntilInput);
        final Spinner roomSpinner = new Spinner(this);
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getRoomNames());
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(roomAdapter);
        layout.addView(roomSpinner);
        final Spinner serviceSpinner = new Spinner(this);
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getServiceNames());
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(serviceAdapter);
        layout.addView(serviceSpinner);
        builder.setView(layout);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String badge = badgeInput.getText().toString().trim();
            String image = imageInput.getText().toString().trim();
            String roomId = roomSpinner.getSelectedItemPosition() > 0 ? roomList.get(roomSpinner.getSelectedItemPosition() - 1).id : null;
            String serviceId = serviceSpinner.getSelectedItemPosition() > 0 ? serviceList.get(serviceSpinner.getSelectedItemPosition() - 1).id : null;
            String validUntil = validUntilInput.getText().toString().trim();
            if (title.isEmpty() || desc.isEmpty() || badge.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            String id = UUID.randomUUID().toString();
            Promotion promo = new Promotion(id, title, desc, image, badge, roomId, serviceId, validUntil);
            promotionsRef.child(id).setValue(promo);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private List<String> getRoomNames() {
        List<String> names = new ArrayList<>();
        names.add("No Room");
        for (Room r : roomList) names.add(r.name);
        return names;
    }
    private List<String> getServiceNames() {
        List<String> names = new ArrayList<>();
        names.add("No Service");
        for (Service s : serviceList) names.add(s.name);
        return names;
    }

    private void showEditDeleteDialog(Promotion promo) {
        String[] options = {"Edit", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(promo.title)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) showEditPromotionDialog(promo);
                    else if (which == 1) promotionsRef.child(promo.id).removeValue();
                })
                .show();
    }

    private void showEditPromotionDialog(Promotion promo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Promotion");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        final EditText titleInput = new EditText(this);
        titleInput.setText(promo.title);
        layout.addView(titleInput);
        final EditText descInput = new EditText(this);
        descInput.setText(promo.description);
        layout.addView(descInput);
        final EditText badgeInput = new EditText(this);
        badgeInput.setText(promo.badge);
        layout.addView(badgeInput);
        final EditText imageInput = new EditText(this);
        imageInput.setText(promo.imageUrl);
        layout.addView(imageInput);
        final EditText validUntilInputEdit = new EditText(this);
        validUntilInputEdit.setHint("Valid Until (YYYY-MM-DD)");
        validUntilInputEdit.setText(promo.validUntil);
        layout.addView(validUntilInputEdit);
        final Spinner roomSpinner = new Spinner(this);
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getRoomNames());
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roomSpinner.setAdapter(roomAdapter);
        roomSpinner.setSelection(getRoomIndexById(promo.roomId));
        layout.addView(roomSpinner);
        final Spinner serviceSpinner = new Spinner(this);
        ArrayAdapter<String> serviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getServiceNames());
        serviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(serviceAdapter);
        serviceSpinner.setSelection(getServiceIndexById(promo.serviceId));
        layout.addView(serviceSpinner);
        builder.setView(layout);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String title = titleInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String badge = badgeInput.getText().toString().trim();
            String image = imageInput.getText().toString().trim();
            String roomId = roomSpinner.getSelectedItemPosition() > 0 ? roomList.get(roomSpinner.getSelectedItemPosition() - 1).id : null;
            String serviceId = serviceSpinner.getSelectedItemPosition() > 0 ? serviceList.get(serviceSpinner.getSelectedItemPosition() - 1).id : null;
            String validUntil = validUntilInputEdit.getText().toString().trim();
            if (title.isEmpty() || desc.isEmpty() || badge.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            Promotion updatedPromo = new Promotion(promo.id, title, desc, image, badge, roomId, serviceId, validUntil);
            promotionsRef.child(promo.id).setValue(updatedPromo);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
    private int getRoomIndexById(String id) {
        if (id == null) return 0;
        for (int i = 0; i < roomList.size(); i++) if (roomList.get(i).id.equals(id)) return i + 1;
        return 0;
    }
    private int getServiceIndexById(String id) {
        if (id == null) return 0;
        for (int i = 0; i < serviceList.size(); i++) if (serviceList.get(i).id.equals(id)) return i + 1;
        return 0;
    }
} 