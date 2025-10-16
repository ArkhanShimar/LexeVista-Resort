package com.example.luxevistaresort;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfileActivity extends Activity {
    private EditText nameInput, addressInput, dobInput, passwordInput, confirmPasswordInput;
    private SharedPreferences session, users;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        nameInput = findViewById(R.id.edit_name_input);
        addressInput = findViewById(R.id.edit_address_input);
        dobInput = findViewById(R.id.edit_dob_input);
        passwordInput = findViewById(R.id.edit_password_input);
        confirmPasswordInput = findViewById(R.id.edit_confirm_password_input);
        Button updateBtn = findViewById(R.id.update_profile_button);
        Button cancelBtn = findViewById(R.id.cancel_profile_button);

        session = getSharedPreferences("session", Context.MODE_PRIVATE);
        users = getSharedPreferences("users", Context.MODE_PRIVATE);
        String userUid = getIntent().getStringExtra("user_uid");
        if (userUid != null) {
            // Admin editing another user: fetch from Firebase
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
            usersRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        nameInput.setText(user.name);
                        addressInput.setText(user.address != null ? user.address : "");
                        dobInput.setText(user.dob != null ? user.dob : "");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {}
            });
        } else {
            // Editing own profile: use SharedPreferences
            email = session.getString("user_email", "");
            String saved = users.getString(email, null);
            String name = "", address = "", dob = "";
            if (saved != null) {
                String[] parts = saved.split(":");
                name = parts.length > 0 ? parts[0] : "";
                address = parts.length > 3 ? parts[3] : "";
                dob = parts.length > 4 ? parts[4] : "";
            }
            nameInput.setText(name);
            addressInput.setText(address);
            dobInput.setText(dob);
        }

        dobInput.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfileActivity.this, (view, year1, month1, dayOfMonth) -> {
                dobInput.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
            }, year, month, day);
            datePickerDialog.show();
        });

        updateBtn.setOnClickListener(v -> {
            String newName = nameInput.getText().toString().trim();
            String newAddress = addressInput.getText().toString().trim();
            String newDob = dobInput.getText().toString().trim();
            String newPassword = passwordInput.getText().toString();
            String confirmPassword = confirmPasswordInput.getText().toString();
            if (TextUtils.isEmpty(newName)) {
                Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.isEmpty(newPassword) || !TextUtils.isEmpty(confirmPassword)) {
                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if (userUid != null) {
                // Update in Firebase
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
                usersRef.child(userUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            user.name = newName;
                            user.address = newAddress;
                            user.dob = newDob;
                            if (!TextUtils.isEmpty(newPassword)) user.password = newPassword;
                            usersRef.child(userUid).setValue(user);
                            Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
            } else {
                // Update in SharedPreferences (own profile)
                String old = users.getString(email, null);
                String[] parts = old != null ? old.split(":") : new String[0];
                String isAdmin = parts.length > 2 ? parts[2] : "false";
                String password = (!TextUtils.isEmpty(newPassword)) ? newPassword : (parts.length > 1 ? parts[1] : "");
                String saveString = newName + ":" + password + ":" + isAdmin + ":" + newAddress + ":" + newDob;
                users.edit().putString(email, saveString).apply();
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        cancelBtn.setOnClickListener(v -> finish());
    }
} 