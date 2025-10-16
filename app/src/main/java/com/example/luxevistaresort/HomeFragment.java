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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import android.widget.TextView;
import android.text.Html;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView roomsRecycler = view.findViewById(R.id.home_rooms_recycler);
        RecyclerView servicesRecycler = view.findViewById(R.id.home_services_recycler);
        RecyclerView offersRecycler = view.findViewById(R.id.home_offers_recycler);

        // Rooms
        ArrayList<Room> rooms = new ArrayList<>();
        RoomsAdapter roomsAdapter = new RoomsAdapter(rooms, room -> {}, false);
        roomsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.recycler_item_spacing);
        roomsRecycler.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));
        roomsRecycler.setAdapter(roomsAdapter);
        DatabaseReference roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                rooms.clear();
                for (DataSnapshot roomSnap : snapshot.getChildren()) {
                    Room room = roomSnap.getValue(Room.class);
                    if (room != null) rooms.add(room);
                }
                roomsAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Services
        ArrayList<Service> services = new ArrayList<>();
        ServiceAdapter serviceAdapter = new ServiceAdapter(services, service -> {});
        servicesRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        servicesRecycler.setAdapter(serviceAdapter);
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("services");
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                services.clear();
                for (DataSnapshot serviceSnap : snapshot.getChildren()) {
                    Service service = serviceSnap.getValue(Service.class);
                    if (service != null) services.add(service);
                }
                serviceAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        // Offers
        ArrayList<Offer> offers = new ArrayList<>();
        OffersAdapter offersAdapter = new OffersAdapter(offers);
        offersRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        offersRecycler.addItemDecoration(new HorizontalSpaceItemDecoration(spacingInPixels));
        offersRecycler.setAdapter(offersAdapter);
        DatabaseReference offersRef = FirebaseDatabase.getInstance().getReference("promotions");
        offersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                offers.clear();
                for (DataSnapshot offerSnap : snapshot.getChildren()) {
                    Offer offer = offerSnap.getValue(Offer.class);
                    if (offer != null) offers.add(offer);
                }
                offersAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        TextView aboutResort = view.findViewById(R.id.about_resort_text);
        aboutResort.setText(Html.fromHtml(getString(R.string.about_resort)));

        return view;
    }
} 