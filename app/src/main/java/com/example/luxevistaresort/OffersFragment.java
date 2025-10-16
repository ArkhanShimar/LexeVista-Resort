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
import android.widget.EditText;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class OffersFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offers, container, false);
        RecyclerView offersRecyclerView = view.findViewById(R.id.offers_recycler_view);
        offersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        offersRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));
        ArrayList<Offer> allOffers = new ArrayList<>();
        ArrayList<Offer> filteredOffers = new ArrayList<>();
        OffersAdapter offersAdapter = new OffersAdapter(filteredOffers);
        offersRecyclerView.setAdapter(offersAdapter);
        DatabaseReference promotionsRef = FirebaseDatabase.getInstance().getReference("promotions");
        promotionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                allOffers.clear();
                filteredOffers.clear();
                for (DataSnapshot promoSnap : snapshot.getChildren()) {
                    Promotion promo = promoSnap.getValue(Promotion.class);
                    if (promo != null) {
                        allOffers.add(new Offer(promo.id, promo.title, promo.description, promo.imageUrl, promo.badge));
                    }
                }
                filteredOffers.addAll(allOffers);
                offersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout attractionsList = view.findViewById(R.id.attractions_list);
        int[] images = {
            R.drawable.sri_lanka_sigiriya,
            R.drawable.sri_lanka_temple_tooth,
            R.drawable.sri_lanka_galle_fort,
            R.drawable.sri_lanka_yala,
            R.drawable.sri_lanka_ella,
            R.drawable.sri_lanka_nuwara_eliya,
            R.drawable.sri_lanka_mirissa,
            R.drawable.sri_lanka_pinnawala,
            R.drawable.sri_lanka_polonnaruwa,
            R.drawable.sri_lanka_anuradhapura
        };
        String[] names = {
            "Sigiriya Rock Fortress",
            "Temple of the Tooth (Kandy)",
            "Galle Fort",
            "Yala National Park",
            "Ella (Nine Arches Bridge)",
            "Nuwara Eliya (Tea Country)",
            "Mirissa Beach",
            "Pinnawala Elephant Orphanage",
            "Polonnaruwa Ancient City",
            "Anuradhapura Sacred City"
        };
        String[] descriptions = {
            "Ancient rock fortress with stunning views and frescoes.",
            "Sacred Buddhist temple housing the relic of the tooth of Buddha.",
            "Historic fort and UNESCO site with colonial architecture.",
            "Wildlife sanctuary famous for leopards and elephants.",
            "Scenic hill town with iconic railway bridge and lush views.",
            "Cool climate, tea plantations, and colonial charm.",
            "Beautiful beach known for whale watching and surfing.",
            "Sanctuary for orphaned and injured elephants.",
            "Ruins of a medieval capital with impressive statues.",
            "Ancient city with stupas, temples, and sacred Bodhi tree."
        };
        for (int i = 0; i < 10; i++) {
            androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(getContext());
            LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardParams.setMargins(0, 0, 0, 24);
            card.setLayoutParams(cardParams);
            card.setCardElevation(8);
            card.setRadius(24);
            card.setCardBackgroundColor(getResources().getColor(R.color.colorCard));
            LinearLayout content = new LinearLayout(getContext());
            content.setOrientation(LinearLayout.VERTICAL);
            content.setPadding(0, 0, 0, 0);
            ImageView img = new ImageView(getContext());
            img.setImageResource(images[i]);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (200 * getResources().getDisplayMetrics().density));
            img.setLayoutParams(imgParams);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            TextView name = new TextView(getContext());
            name.setText(names[i]);
            name.setTextColor(getResources().getColor(R.color.colorPrimary));
            name.setTextSize(18);
            name.setTypeface(null, android.graphics.Typeface.BOLD);
            name.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            name.setPadding(0, 12, 0, 4);
            TextView desc = new TextView(getContext());
            desc.setText(descriptions[i]);
            desc.setTextColor(getResources().getColor(R.color.colorText));
            desc.setTextSize(14);
            desc.setGravity(android.view.Gravity.CENTER_HORIZONTAL);
            desc.setPadding(16, 0, 16, 16);
            content.addView(img);
            content.addView(name);
            content.addView(desc);
            card.addView(content);
            attractionsList.addView(card);
        }
    }
} 