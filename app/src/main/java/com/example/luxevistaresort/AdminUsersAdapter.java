package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdminUsersAdapter extends RecyclerView.Adapter<AdminUsersAdapter.UserViewHolder> {
    private List<User> userList;
    private OnUserClickListener clickListener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public AdminUsersAdapter(List<User> userList, OnUserClickListener clickListener) {
        this.userList = userList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.nameText.setText(user.name);
        holder.emailText.setText(user.email);
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onUserClick(user);
        });
        holder.infoIcon.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onUserClick(user);
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, emailText;
        android.widget.ImageView infoIcon;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.user_name);
            emailText = itemView.findViewById(R.id.user_email);
            infoIcon = itemView.findViewById(R.id.user_info_icon);
        }
    }
} 