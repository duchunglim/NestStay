package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class OrderPaymentActivity extends AppCompatActivity {
    ImageView backButton;
    RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private OrderPaymentAdapter orderPaymentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_payment);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.itemListRecyclerView);
        backButton = findViewById(R.id.icon_back);
        backButton.setOnClickListener(v -> finish());

        orderPaymentAdapter = new OrderPaymentAdapter(new ArrayList<>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderPaymentAdapter);

        getCartItems();
    }

    private void getCartItems() {
        mDatabase.child("users").child(mAuth.getCurrentUser().getUid()).child("cart").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<CartProduct> cartProductList = new ArrayList<>();
                for (DataSnapshot cartItem : task.getResult().getChildren()) {
                    CartProduct cartProduct = cartItem.getValue(CartProduct.class);
                    if (cartProduct != null) {
                        cartProductList.add(cartProduct);
                    }
                }
                Log.d("OrderPaymentActivity", "Cart items fetched: " + cartProductList.size());
                orderPaymentAdapter.updateCartProducts(cartProductList);
            } else {
                Log.e("OrderPaymentActivity", "Error getting data", task.getException());
            }
        });
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
