package com.example.luxevistaresort;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
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

import com.example.luxevistaresort.AdminServiceAdapter;

public class ManageServicesActivity extends Activity {
    private RecyclerView recyclerView;
    private AdminServiceAdapter adapter;
    private List<Service> serviceList = new ArrayList<>();
    private DatabaseReference servicesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_services);

        recyclerView = findViewById(R.id.services_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminServiceAdapter(serviceList, new AdminServiceAdapter.OnServiceEditListener() {
            @Override
            public void onServiceEdit(Service service) {
                showEditServiceDialog(service);
            }
        }, new AdminServiceAdapter.OnServiceDeleteListener() {
            @Override
            public void onServiceDelete(Service service) {
                new AlertDialog.Builder(ManageServicesActivity.this)
                    .setTitle("Delete Service")
                    .setMessage("Are you sure you want to delete this service?")
                    .setPositiveButton("Delete", (dialog, which) -> servicesRef.child(service.id).removeValue())
                    .setNegativeButton("Cancel", null)
                    .show();
            }
        });
        recyclerView.setAdapter(adapter);

        servicesRef = FirebaseDatabase.getInstance().getReference("services");

        loadServices();

        FloatingActionButton fab = findViewById(R.id.add_service_fab);
        fab.setOnClickListener(v -> showAddServiceDialog());
    }

    private void loadServices() {
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serviceList.clear();
                for (DataSnapshot serviceSnap : snapshot.getChildren()) {
                    Service service = serviceSnap.getValue(Service.class);
                    if (service != null) serviceList.add(service);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageServicesActivity.this, "Failed to load services", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Service");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText nameInput = new EditText(this);
        nameInput.setHint("Service Name");
        layout.addView(nameInput);

        final EditText descInput = new EditText(this);
        descInput.setHint("Description");
        layout.addView(descInput);

        final EditText priceInput = new EditText(this);
        priceInput.setHint("Price");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(priceInput);

        final EditText imageNameInput = new EditText(this);
        imageNameInput.setHint("Image Name (e.g., service1)");
        layout.addView(imageNameInput);

        builder.setView(layout);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            String imageName = imageNameInput.getText().toString().trim();
            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || imageName.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);
            String id = UUID.randomUUID().toString();
            Service service = new Service(id, name, desc, price, imageName, new ArrayList<>());
            servicesRef.child(id).setValue(service);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditServiceDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Service");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);

        final EditText nameInput = new EditText(this);
        nameInput.setText(service.name);
        layout.addView(nameInput);

        final EditText descInput = new EditText(this);
        descInput.setText(service.description);
        layout.addView(descInput);

        final EditText priceInput = new EditText(this);
        priceInput.setText(String.valueOf(service.price));
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(priceInput);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = nameInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();
            String priceStr = priceInput.getText().toString().trim();
            if (name.isEmpty() || desc.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }
            double price = Double.parseDouble(priceStr);
            Service updatedService = new Service(service.id, name, desc, price, "", new ArrayList<>());
            servicesRef.child(service.id).setValue(updatedService);
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
} 