package android.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReservationHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ReservationAdapter adapter;
    private List<Reservation> reservationList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservation_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reservationList = new ArrayList<>();
        adapter = new ReservationAdapter(getActivity(), reservationList);
        recyclerView.setAdapter(adapter);

        // Load dữ liệu từ Firebase
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid()).child("reservations");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    reservationList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Reservation reservation = snapshot.getValue(Reservation.class);
                        if (reservation != null) {
                            reservation.setId(snapshot.getKey());

                            reservationList.add(reservation);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Xử lý khi có lỗi
                }
            });
        }

        return view;
    }
}

