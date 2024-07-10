package android.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class HomeFragment extends Fragment {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ViewPager2 viewPager2;
    private ImageView previousButton, nextButton;
    private Button historyButton;
    private int[] adImages = {
            R.drawable.adver1, // Replace with your actual drawable resource IDs
            R.drawable.adver2,
            R.drawable.adver3,
            R.drawable.adver4,
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Khởi tạo các phần tử UI trong fragment_home.xml liên kết top_nav
        TextView userNameTextView = view.findViewById(R.id.menu_top_nav).findViewById(R.id.user_name);
        ImageView cartIcon = view.findViewById(R.id.menu_top_nav).findViewById(R.id.cart_icon);
        TextView cartCount = view.findViewById(R.id.menu_top_nav).findViewById(R.id.cart_badge);
        ImageView profileImage = view.findViewById(R.id.menu_top_nav).findViewById(R.id.profile_image);
        Button history = view.findViewById(R.id.history);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình LoginActivity và đóng Fragment hiện tại
                Intent intent = new Intent(getActivity(), HistoryActivity.class);
                startActivity(intent);
            }
        });


        // Hiển thị số lượng sản phẩm trong giỏ hàng
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart");
            cartRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        cartCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    } else {
                        cartCount.setText("0");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi đọc dữ liệu
                }
            });
        }

        cartIcon.setOnClickListener(v -> {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("cart");


            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                        // Cart is not empty
                        Intent intent = new Intent(getContext(), OrderPaymentActivity.class);
                        startActivity(intent);
                    } else {
                        // Cart is empty
                        Toast.makeText(getContext(), "Chưa có sản phẩm trong giỏ hàng.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle possible errors
                    Toast.makeText(getContext(), "Failed to check cart. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });
        });

        historyButton = view.findViewById(R.id.historyButton);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), HistoryActivity.class);
                startActivity(intent);
            }
        });

        viewPager2 = view.findViewById(R.id.ad_background);
        AdImageAdapter adapter = new AdImageAdapter(getContext(), adImages);
        viewPager2.setAdapter(adapter);

        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);


        // Lấy thông tin người dùng từ Firebase Realtime Database
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String name = dataSnapshot.child("name").getValue(String.class);
                        String profileImageUrl = dataSnapshot.child("profileImage").getValue(String.class);

                        // Hiển thị thông tin người dùng
                        userNameTextView.setText(name);
                        // Hiển thị ảnh đại diện
                        if (profileImageUrl != null && !profileImageUrl.isEmpty() && isAdded()) {
                            Glide.with(requireActivity())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.meme) // Ảnh mặc định
                                    .into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Xử lý lỗi đọc dữ liệu
                }
            });
        }

        // Handle previous button click
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager2.getCurrentItem();
                if (currentItem > 0) {
                    viewPager2.setCurrentItem(currentItem - 1, true);
                }
            }
        });

        // Handle next button click
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPager2.getCurrentItem();
                if (currentItem < adImages.length - 1) {
                    viewPager2.setCurrentItem(currentItem + 1, true);
                }
            }
        });

        // Auto-scroll functionality
        autoScrollImages();

        return view;
    }

    private void autoScrollImages() {
        final int scrollInterval = 3000; // 3 seconds
        viewPager2.postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % adImages.length;
                viewPager2.setCurrentItem(nextItem, true);
                viewPager2.postDelayed(this, scrollInterval);
            }
        }, scrollInterval);
    }
}
