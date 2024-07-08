package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderPaymentActivity extends AppCompatActivity {
    ImageView backButton;
    RecyclerView recyclerView;
    TextView seeMenu, total, subTotal, deliveryFee;
    RadioButton priority, standard;
    Button nextButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private OrderPaymentAdapter orderPaymentAdapter;
    private List<CartProduct> cartProductList;

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

        standard.setChecked(true);

        priority.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                standard.setChecked(false);
                calculateTotals();
            }
        });

        standard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                priority.setChecked(false);
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
            LocalDateTime timestamp = LocalDateTime.now(); // Use LocalDateTime.now() instead of Date
            String deliveryOption = priority.isChecked() ? "Priority" : "Standard";
            Order newOrder = new Order(
                    mDatabase.child("orders").push().getKey(),
                    cartProductList,
                    Double.parseDouble(total.getText().toString().replace("đ", "")),
                    deliveryOption,
                    timestamp
            );

            // Update the user's order history in Firebase
            if (newOrder.getOrderId() != null) {
                mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("orders").child(newOrder.getOrderId()).setValue(newOrder)
                        .addOnSuccessListener(aVoid -> {
                            // Handle success
                            Toast.makeText(OrderPaymentActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                            // Clear the cart after placing the order
                            mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("cart").removeValue();
                            // Redirect to a confirmation page or back to the main activity
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(OrderPaymentActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                        });
            }
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
    }
}

