package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;

public class RoomsAdapter extends RecyclerView.Adapter<RoomsAdapter.RoomViewHolder> {
    private List<Room> roomList;
    private OnBookNowClickListener bookNowClickListener;
    private boolean showBookNowButton = true;

    public interface OnBookNowClickListener {
        void onBookNowClick(Room room);
    }

    public RoomsAdapter(List<Room> roomList, OnBookNowClickListener bookNowClickListener) {
        this(roomList, bookNowClickListener, true);
    }

    public RoomsAdapter(List<Room> roomList, OnBookNowClickListener bookNowClickListener, boolean showBookNowButton) {
        this.roomList = roomList;
        this.bookNowClickListener = bookNowClickListener;
        this.showBookNowButton = showBookNowButton;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.nameText.setText(room.name);
        holder.descText.setText(room.description);
        holder.priceText.setText("LKR " + room.price);
        // Load image from drawable using the image name stored in room.imageUrl
        String imageName = room.imageUrl != null ? room.imageUrl.trim() : "";
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(imageName, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
        // Show AC/Non-AC only if set
        if (room.isAC) {
            holder.acStatusText.setVisibility(View.VISIBLE);
            holder.acStatusText.setText("AC");
        } else {
            holder.acStatusText.setVisibility(View.GONE);
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
        holder.capacityText.setText("Max: " + room.capacity + (room.capacity == 1 ? " person" : " persons"));
        if (showBookNowButton) {
            holder.bookNowButton.setVisibility(View.VISIBLE);
            holder.bookNowButton.setOnClickListener(v -> {
                if (bookNowClickListener != null) bookNowClickListener.onBookNowClick(room);
            });
        } else {
            holder.bookNowButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void setRoomList(List<Room> newList) {
        this.roomList = newList;
        notifyDataSetChanged();
    }

    static class RoomViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descText, priceText, acStatusText, capacityText;
        ImageView imageView;
        LinearLayout tagsContainer;
        Button bookNowButton;
        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.room_name);
            descText = itemView.findViewById(R.id.room_description);
            priceText = itemView.findViewById(R.id.room_price);
            imageView = itemView.findViewById(R.id.room_image);
            acStatusText = itemView.findViewById(R.id.room_ac_status);
            tagsContainer = itemView.findViewById(R.id.room_tags_container);
            bookNowButton = itemView.findViewById(R.id.btn_book_now);
            capacityText = itemView.findViewById(R.id.room_capacity);
        }
    }
} 