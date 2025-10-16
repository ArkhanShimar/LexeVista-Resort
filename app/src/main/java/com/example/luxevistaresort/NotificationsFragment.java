package com.example.luxevistaresort;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;

public class NotificationsFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<Notification> notifications = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = view.findViewById(R.id.notifications_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationsAdapter(notifications);
        recyclerView.setAdapter(adapter);

        String userId = getActivity().getSharedPreferences("session", getContext().MODE_PRIVATE)
            .getString("user_email", "").replace("@", "_").replace(".", "_");

        DatabaseReference notifRef = FirebaseDatabase.getInstance().getReference("notifications");
        notifRef.child("all").addValueEventListener(new NotificationListener());
        notifRef.child(userId).addValueEventListener(new NotificationListener());

        return view;
    }

    private class NotificationListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            notifications.clear();
            for (DataSnapshot snap : snapshot.getChildren()) {
                Notification n = snap.getValue(Notification.class);
                if (n != null) notifications.add(n);
            }
            Collections.sort(notifications, (a, b) -> Long.compare(b.timestamp, a.timestamp));
            Collections.reverse(notifications);
            adapter.notifyDataSetChanged();
        }
        @Override
        public void onCancelled(@NonNull DatabaseError error) {}
    }
} 