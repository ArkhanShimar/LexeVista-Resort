package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;
import java.util.List;

public class RoomSelectAdapter extends RecyclerView.Adapter<RoomSelectAdapter.RoomViewHolder> {
    public interface OnRoomSelectListener {
        void onRoomSelect(Room room);
    }
    private List<Room> roomList;
    private OnRoomSelectListener selectListener;

    public RoomSelectAdapter(List<Room> roomList, OnRoomSelectListener selectListener) {
        this.roomList = roomList;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room_select, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.nameText.setText(room.name);
        holder.descText.setText(room.description);
        holder.priceText.setText("$" + room.price);
        holder.acStatusText.setText(room.isAC ? "AC" : "Non-AC");
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(room.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.tagsContainer.removeAllViews();
        if (room.tags != null) {
            for (String tag : room.tags) {
                TextView tagView = new TextView(holder.itemView.getContext());
                tagView.setText(tag);
                tagView.setTextSize(12);
                tagView.setPadding(16, 8, 16, 8);
                tagView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.rounded_card));
                tagView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(8, 0, 8, 0);
                tagView.setLayoutParams(params);
                holder.tagsContainer.addView(tagView);
            }
        }
        holder.selectBtn.setOnClickListener(v -> {
            if (selectListener != null) selectListener.onRoomSelect(room);
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descText, priceText, acStatusText;
        ImageView imageView;
        LinearLayout tagsContainer;
        Button selectBtn;
        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.room_name);
            descText = itemView.findViewById(R.id.room_description);
            priceText = itemView.findViewById(R.id.room_price);
            imageView = itemView.findViewById(R.id.room_image);
            acStatusText = itemView.findViewById(R.id.room_ac_status);
            tagsContainer = itemView.findViewById(R.id.room_tags_container);
            selectBtn = itemView.findViewById(R.id.select_room_btn);
        }
    }
} 