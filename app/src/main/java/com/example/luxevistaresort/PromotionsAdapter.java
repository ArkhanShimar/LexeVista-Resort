package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PromotionsAdapter extends RecyclerView.Adapter<PromotionsAdapter.PromotionViewHolder> {
    private List<Promotion> promotionList;
    private OnPromotionLongClickListener longClickListener;
    private OnPromotionEditListener editListener;
    private OnPromotionDeleteListener deleteListener;

    public interface OnPromotionLongClickListener {
        void onPromotionLongClick(Promotion promotion);
    }

    public interface OnPromotionEditListener {
        void onPromotionEdit(Promotion promotion);
    }

    public interface OnPromotionDeleteListener {
        void onPromotionDelete(Promotion promotion);
    }

    public PromotionsAdapter(List<Promotion> promotionList, OnPromotionLongClickListener longClickListener) {
        this.promotionList = promotionList;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public PromotionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_promotion, parent, false);
        return new PromotionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromotionViewHolder holder, int position) {
        Promotion promo = promotionList.get(position);
        holder.titleText.setText(promo.title);
        holder.descText.setText(promo.description);
        holder.badgeText.setText(promo.badge);
        holder.validUntilText.setText("Valid Until: " + promo.validUntil);
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(promo.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.editBtn.setOnClickListener(v -> {
            if (editListener != null) editListener.onPromotionEdit(promo);
        });
        holder.deleteBtn.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onPromotionDelete(promo);
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onPromotionLongClick(promo);
            return true;
        });
    }

    public void setOnPromotionEditListener(OnPromotionEditListener listener) { this.editListener = listener; }
    public void setOnPromotionDeleteListener(OnPromotionDeleteListener listener) { this.deleteListener = listener; }

    @Override
    public int getItemCount() {
        return promotionList.size();
    }

    static class PromotionViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descText, badgeText, validUntilText;
        ImageView imageView;
        Button editBtn, deleteBtn;
        PromotionViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.offer_title);
            descText = itemView.findViewById(R.id.offer_description);
            badgeText = itemView.findViewById(R.id.offer_discount_badge);
            validUntilText = itemView.findViewById(R.id.promotion_valid_until);
            imageView = itemView.findViewById(R.id.offer_image);
            editBtn = itemView.findViewById(R.id.btn_edit_promotion);
            deleteBtn = itemView.findViewById(R.id.btn_delete_promotion);
        }
    }
} 