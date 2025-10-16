package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat;

import java.util.List;

public class AdminServiceAdapter extends RecyclerView.Adapter<AdminServiceAdapter.ServiceViewHolder> {
    private List<Service> serviceList;
    private OnServiceEditListener editListener;
    private OnServiceDeleteListener deleteListener;

    public interface OnServiceEditListener {
        void onServiceEdit(Service service);
    }

    public interface OnServiceDeleteListener {
        void onServiceDelete(Service service);
    }

    public AdminServiceAdapter(List<Service> serviceList, OnServiceEditListener editListener, OnServiceDeleteListener deleteListener) {
        this.serviceList = serviceList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service_admin, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.nameText.setText(service.name);
        holder.descText.setText(service.description);
        holder.priceText.setText("LKR " + service.price);
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(service.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
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
        holder.editBtn.setOnClickListener(v -> {
            if (editListener != null) editListener.onServiceEdit(service);
        });
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onServiceDelete(service);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView nameText, descText, priceText;
        ImageView imageView;
        LinearLayout tagsContainer;
        ImageView editBtn, deleteBtn;
        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            nameText = itemView.findViewById(R.id.service_name);
            descText = itemView.findViewById(R.id.service_description);
            priceText = itemView.findViewById(R.id.service_price);
            imageView = itemView.findViewById(R.id.service_image);
            tagsContainer = itemView.findViewById(R.id.service_tags_container);
            editBtn = itemView.findViewById(R.id.btn_edit_service);
            deleteBtn = itemView.findViewById(R.id.btn_delete_service);
        }
    }
} 