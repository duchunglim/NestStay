package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ItemDescriptionActivity extends AppCompatActivity {
    Button buttonBuyNow, buttonAddToCart;
    ImageView productImage, iconBack, buttonIncrease, buttonDecrease;
    TextView productName, productDescription, productPrice, textViewQuantity;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        buttonAddToCart = findViewById(R.id.buttonAddToCart);
        buttonIncrease = findViewById(R.id.buttonIncrease);
        buttonDecrease = findViewById(R.id.buttonDecrease);
        textViewQuantity = findViewById(R.id.textViewQuantity);
        iconBack = findViewById(R.id.icon_back);
        productImage = findViewById(R.id.itemImage);
        productName = findViewById(R.id.itemName);
        productDescription = findViewById(R.id.itemDescription);
        productPrice = findViewById(R.id.itemPrice);
        buttonBuyNow = findViewById(R.id.buttonBuyNow);



        String productName = getIntent().getStringExtra("name");
        String productDescription = getIntent().getStringExtra("description");
        String productPrice = getIntent().getStringExtra("price");
        String productImage = getIntent().getStringExtra("image");
        if (getIntent().getStringExtra("quantity") != null) {
            textViewQuantity.setText(getIntent().getStringExtra("quantity"));
        } else {
            textViewQuantity.setText("1");
        }

        this.productName.setText(productName);
        this.productDescription.setText(productDescription);
        this.productPrice.setText(productPrice);

        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();
        DatabaseReference itemRef = mDatabase.child("users").child(userId).child("cart").child(productName);
        //get item quantity from database if item exists in the cart
        itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String existingQuantityStr = dataSnapshot.child("quantity").getValue(String.class);
                    textViewQuantity.setText(existingQuantityStr);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        if (productImage != null && !productImage.isEmpty()) {
            Picasso.get()
                    .load(productImage)
                    .error(R.drawable.placeholder)
                    .into(this.productImage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            // Handle case where image URL is empty or null
            this.productImage.setImageResource(R.drawable.placeholder);
        }

        if (textViewQuantity.getText() != null && textViewQuantity.getText().toString().equals("1")) {
            buttonDecrease.setClickable(false);
        } else {
            buttonDecrease.setClickable(true);
        }

        buttonIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());
            quantity++;
            textViewQuantity.setText(String.valueOf(quantity));
            if (textViewQuantity.getText() != null && textViewQuantity.getText().toString().equals("1")) {
                buttonDecrease.setClickable(false);
            } else {
                buttonDecrease.setClickable(true);
            }
        });

        buttonDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());
            if (quantity > 0) {
                quantity--;
                textViewQuantity.setText(String.valueOf(quantity));
            }
        });

        iconBack.setOnClickListener(v -> {
            finish();
        });

        buttonBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDescriptionActivity.this, OrderPaymentActivity.class);
            startActivity(intent);
        });


        buttonAddToCart.setOnClickListener(v -> {
            // Ensure Firebase Auth is initialized
            if (mAuth != null) {
                if (user != null) {
                    String itemName = productName;
                    String itemDesc = productDescription;
                    String itemPrice = productPrice;
                    String itemImage = productImage;
                    String itemQuantity = textViewQuantity.getText().toString();

                    itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                // Item exists in the cart, update the quantity
                                int newQuantity = Integer.parseInt(itemQuantity);

                                // If the new quantity is zero, remove the item from the cart
                                if (newQuantity == 0) {
                                    itemRef.removeValue().addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ItemDescriptionActivity.this, "Item removed from cart", Toast.LENGTH_SHORT).show();
                                            buttonDecrease.setClickable(false);
                                        } else {
                                            Toast.makeText(ItemDescriptionActivity.this, "Failed to remove item from cart", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    // Update the item quantity
                                    itemRef.child("quantity").setValue(String.valueOf(newQuantity))
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ItemDescriptionActivity.this, "Item quantity updated in cart", Toast.LENGTH_SHORT).show();
                                                    // Make the decrease button clickable since item is in the cart
                                                    buttonDecrease.setClickable(true);
                                                } else {
                                                    Toast.makeText(ItemDescriptionActivity.this, "Failed to update item quantity", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                // Item does not exist in the cart, add new item
                                Map<String, String> itemDetails = new HashMap<>();
                                itemDetails.put("name", itemName);
                                itemDetails.put("description", itemDesc);
                                itemDetails.put("price", itemPrice);
                                itemDetails.put("image", itemImage);
                                itemDetails.put("quantity", itemQuantity);

                                // Add item details to the database
                                itemRef.setValue(itemDetails)
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ItemDescriptionActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
                                                // Make the decrease button clickable since item is now in the cart
                                                buttonDecrease.setClickable(true);
                                            } else {
                                                Toast.makeText(ItemDescriptionActivity.this, "Failed to add item to cart", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle possible errors
                            Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // Handle the case when the user is not logged in
                    Toast.makeText(ItemDescriptionActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle the case when FirebaseAuth is not initialized
                Toast.makeText(ItemDescriptionActivity.this, "FirebaseAuth not initialized", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
    }
}
