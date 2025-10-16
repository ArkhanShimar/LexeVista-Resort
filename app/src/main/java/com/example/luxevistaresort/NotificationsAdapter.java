package com.example.luxevistaresort;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private List<Notification> notifications;

    public NotificationsAdapter(List<Notification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification n = notifications.get(position);
        holder.title.setText(n.title);
        holder.message.setText(n.message);
        holder.time.setText(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm", n.timestamp));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.notification_title);
            message = v.findViewById(R.id.notification_message);
            time = v.findViewById(R.id.notification_time);
        }
    }
} 