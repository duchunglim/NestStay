package android.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderPaymentActivity extends AppCompatActivity {
    ImageView backButton;
    RecyclerView recyclerView;
    TextView seeMenu, total, subTotal, deliveryFee, tvAddress, tvPhone, tvName, deliveryTime;
    RadioButton priority, standard;
    Button nextButton;
    LinearLayout addressLayout;
    String name, phone, address, note = "";
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private OrderPaymentAdapter orderPaymentAdapter;
    private List<CartProduct> cartProductList;
    private static final int ADDRESS_REQUEST_CODE = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.itemListRecyclerView);
        backButton = findViewById(R.id.icon_back);
        seeMenu = findViewById(R.id.seeMenu);
        total = findViewById(R.id.total);
        subTotal = findViewById(R.id.subtotal);
        deliveryFee = findViewById(R.id.deliveryFee);
        priority = findViewById(R.id.priorityOption);
        standard = findViewById(R.id.standardOption);
        nextButton = findViewById(R.id.nextButton);
        addressLayout = findViewById(R.id.addressLayout);
        tvAddress = findViewById(R.id.address);
        tvPhone = findViewById(R.id.phone);
        tvName = findViewById(R.id.name);
        deliveryTime = findViewById(R.id.deliveryTime);

        standard.setChecked(true);


        addressLayout.setOnClickListener(v -> {
            Intent intent = new Intent(OrderPaymentActivity.this, AddressProfileActivity.class);
            startActivityForResult(intent, ADDRESS_REQUEST_CODE);
        });


        priority.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                standard.setChecked(false);
                deliveryTime.setText("10-15 phút");
                calculateTotals();
            }
        });

        standard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                priority.setChecked(false);
                deliveryTime.setText("15-30 phút");
                calculateTotals();
            }
        });

        seeMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment_to_load", "orders");
            startActivity(intent);
            finish();
        });

        nextButton.setOnClickListener(v -> {
            if (!priority.isChecked() && !standard.isChecked()) {
                Toast.makeText(this, "Vui lòng chọn phương thức giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name == null || name.isEmpty() || phone == null || phone.isEmpty() || address == null || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            long currentDateTime = System.currentTimeMillis();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy  h:mm a");
            String time = simpleDateFormat.format(currentDateTime);

            String deliveryOption = priority.isChecked() ? "Priority" : "Standard";

            // Modify this part according to your Order class constructor or use a Map if needed
            Order newOrder = new Order(
                    mDatabase.child("orders").push().getKey(),
                    cartProductList,
                    Integer.parseInt(total.getText().toString().replace("đ", "")),
                    deliveryOption,
                    time,
                    name,
                    phone,
                    address,
                    note
            );

            // Update the user's order history in Firebase
            if (newOrder.getOrderId() != null) {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("orders").child(newOrder.getOrderId()).setValue(newOrder)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(OrderPaymentActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("cart").removeValue();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(OrderPaymentActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                        });
            }

            Intent intent = new Intent(this, SuccessActivity.class);
            intent.putExtra("message", "Đặt hàng thành công");
            startActivity(intent);
        });

        backButton.setOnClickListener(v -> finish());

        orderPaymentAdapter = new OrderPaymentAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderPaymentAdapter);

        // Initialize cartProductList
        cartProductList = new ArrayList<>();

        getCartItems();
        calculateTotals();
    }

    private void getCartItems() {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                cartProductList.clear(); // Clear the list before adding new items
                for (DataSnapshot cartItem : task.getResult().getChildren()) {
                    CartProduct cartProduct = cartItem.getValue(CartProduct.class);
                    if (cartProduct != null) {
                        cartProductList.add(cartProduct);
                    }
                }
                Log.d("OrderPaymentActivity", "Cart items fetched: " + cartProductList.size());
                orderPaymentAdapter.updateCartProducts(cartProductList);

                // Calculate subtotal and total
                calculateTotals();
            } else {
                Log.e("OrderPaymentActivity", "Error getting data", task.getException());
            }
        });
    }

    private void calculateTotals() {
        int subtotalValue = 0;
        int fee = 0;

        if (priority.isChecked()) {
            fee = 25000;
        } else if (standard.isChecked()) {
            fee = 14000;
        } else {
            fee = 0;
        }

        // Calculate subtotal
        for (CartProduct product : cartProductList) {
            subtotalValue += Integer.parseInt(product.getPrice()) * Integer.parseInt(product.getQuantity());
        }

        // Calculate total
        int totalValue = subtotalValue + fee;

        // Display subtotal and total in TextViews
        subTotal.setText(subtotalValue+"đ");
        deliveryFee.setText(fee+"đ");
        total.setText(totalValue+"đ");
        nextButton.setText("Thanh toán · " + totalValue + "đ");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d("OrderPaymentActivity", "Activity result received, refreshing cart items...");
            getCartItems();  // Refresh cart items after returning from ItemDescriptionActivity
        }
        if (requestCode == ADDRESS_REQUEST_CODE && resultCode == RESULT_OK && data != null) { // Added null check for data
            AddressProfile selectedProfile = (AddressProfile) data.getSerializableExtra("selectedAddressProfile");
            if (selectedProfile != null) { // Additional null check for selectedProfile
                // set text for name, phone, address
                tvName.setText(selectedProfile.getName());
                tvPhone.setText(selectedProfile.getPhone());
                tvAddress.setText(selectedProfile.getAddress());
                name = selectedProfile.getName();
                phone = selectedProfile.getPhone();
                address = selectedProfile.getAddress();

                tvPhone.setVisibility(View.VISIBLE);
                tvName.setVisibility(View.VISIBLE);
            } else {
                Log.d("OrderPaymentActivity", "No address profile returned");
            }
        }
    }
}

