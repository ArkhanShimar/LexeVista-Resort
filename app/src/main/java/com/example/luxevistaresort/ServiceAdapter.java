package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private OnServiceSelectListener selectListener;

    public interface OnServiceSelectListener {
        void onServiceSelected(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceSelectListener selectListener) {
        this.serviceList = serviceList;
        this.selectListener = selectListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.nameText.setText(service.name);
        holder.descText.setText(service.description);
        holder.priceText.setText("LKR " + service.price);
        // Set image
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(service.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
        // Set tags
        holder.tagsContainer.removeAllViews();
        if (service.tags != null) {
            for (String tag : service.tags) {
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
        // Highlight and checkmark
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.selected_card_bg); // create this drawable for highlight
        } else {
            holder.itemView.setBackgroundResource(0);
        }
        holder.itemView.setOnClickListener(v -> {
            int prevSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prevSelected);
            notifyItemChanged(selectedPosition);
            if (selectListener != null) selectListener.onServiceSelected(serviceList.get(selectedPosition));
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public Service getSelectedService() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            return serviceList.get(selectedPosition);
        }
        return null;
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descText, priceText;
        ImageView imageView;
        LinearLayout tagsContainer;
        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.service_name);
            descText = itemView.findViewById(R.id.service_description);
            priceText = itemView.findViewById(R.id.service_price);
            imageView = itemView.findViewById(R.id.service_image);
            tagsContainer = itemView.findViewById(R.id.service_tags_container);
        }
    }
} 