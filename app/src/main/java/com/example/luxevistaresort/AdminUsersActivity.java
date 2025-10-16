package com.example.luxevistaresort;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminUsersActivity extends Activity {
    private RecyclerView recyclerView;
    private AdminUsersAdapter adapter;
    private List<User> userList = new ArrayList<>();
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users);

        recyclerView = findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminUsersAdapter(userList, this::onUserClick);
        recyclerView.setAdapter(adapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");
        loadUsers();
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    User user = userSnap.getValue(User.class);
                    if (user != null && !user.isAdmin) {
                        userList.add(user);
                    }
                }
                adapter.setUserList(new ArrayList<>(userList));
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(AdminUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onUserClick(User user) {
        Intent intent = new Intent(this, AdminUserDetailsActivity.class);
        intent.putExtra("user_uid", user.email); // We'll update this to use UID if needed
        startActivity(intent);
    }

    private void onUserEdit(User user) {
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("user_uid", user.email); // We'll update this to use UID if needed
        startActivity(intent);
    }

    private void onUserDelete(User user) {
        new AlertDialog.Builder(this)
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete this user?")
            .setPositiveButton("Delete", (dialog, which) -> {
                // Find the user's UID by email (or store UID in User model)
                usersRef.orderByChild("email").equalTo(user.email).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for (DataSnapshot userSnap : snapshot.getChildren()) {
                            userSnap.getRef().removeValue();
                        }
                        Toast.makeText(AdminUsersActivity.this, "User deleted", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(AdminUsersActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
} 