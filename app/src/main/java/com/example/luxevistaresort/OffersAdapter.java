package com.example.luxevistaresort;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.OfferViewHolder> {
    private List<Offer> offerList;

    public OffersAdapter(List<Offer> offerList) {
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        Offer offer = offerList.get(position);
        holder.titleText.setText(offer.title);
        holder.descText.setText(offer.description);
        holder.badgeText.setText(offer.badge);
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(offer.imageUrl, "drawable", holder.itemView.getContext().getPackageName());
        if (imageResId != 0) {
            holder.imageView.setImageResource(imageResId);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    static class OfferViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, descText, badgeText;
        ImageView imageView;
        OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.offer_title);
            descText = itemView.findViewById(R.id.offer_description);
            badgeText = itemView.findViewById(R.id.offer_discount_badge);
            imageView = itemView.findViewById(R.id.offer_image);
        }
    }
} 