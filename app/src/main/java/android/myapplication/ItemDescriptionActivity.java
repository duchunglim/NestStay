package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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

        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String price = getIntent().getStringExtra("price");
        String image = getIntent().getStringExtra("image");
        String quantity = getIntent().getStringExtra("quantity");

        productName.setText(name);
        productDescription.setText(description);
        productPrice.setText(price + "đ");

        if (quantity != null) {
            textViewQuantity.setText(quantity);
        } else {
            textViewQuantity.setText("1");
        }

        if (image != null && !image.isEmpty()) {
            Picasso.get()
                    .load(image)
                    .error(R.drawable.placeholder) // Placeholder image if loading fails
                    .into(productImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Image loaded successfully
                        }

                        @Override
                        public void onError(Exception e) {
                            // Handle error (e.g., log, show placeholder)
                            e.printStackTrace();
                        }
                    });
        } else {
            productImage.setImageResource(R.drawable.placeholder);
        }

        if (mAuth != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                String itemName = name;

                DatabaseReference itemRef = mDatabase.child("users").child(userId).child("cart").child(itemName);
                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String existingQuantityStr = dataSnapshot.child("quantity").getValue(String.class);
                            textViewQuantity.setText(existingQuantityStr);
                            buttonDecrease.setEnabled(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ItemDescriptionActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ItemDescriptionActivity.this, "FirebaseAuth not initialized", Toast.LENGTH_SHORT).show();
        }

        buttonDecrease.setEnabled(false);

        buttonIncrease.setOnClickListener(v -> {
            int quantity1 = Integer.parseInt(textViewQuantity.getText().toString());
            quantity1++;
            textViewQuantity.setText(String.valueOf(quantity1));
            buttonDecrease.setEnabled(true);
        });

        buttonDecrease.setOnClickListener(v -> {
            if (mAuth != null) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    String itemName = name;

                    DatabaseReference itemRef = mDatabase.child("users").child(userId).child("cart").child(itemName);
                    itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                buttonDecrease.setEnabled(true);
                                int quantity = Integer.parseInt(textViewQuantity.getText().toString());
                                if (quantity > 0) {
                                    quantity--;
                                    textViewQuantity.setText(String.valueOf(quantity));
                                }
                            } else {
                                int quantity = Integer.parseInt(textViewQuantity.getText().toString());
                                if (quantity > 1) {
                                    quantity--;
                                    textViewQuantity.setText(String.valueOf(quantity));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ItemDescriptionActivity.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ItemDescriptionActivity.this, "FirebaseAuth not initialized", Toast.LENGTH_SHORT).show();
            }
        });

        iconBack.setOnClickListener(v -> finish());

        buttonAddToCart.setOnClickListener(v -> {
            FirebaseUser user = mAuth.getCurrentUser();
            if (mAuth != null && user != null) {
                String userId = user.getUid();
                String itemName = name;
                String itemDesc = description;
                String itemPrice = price;
                String itemImage = image;
                String itemQuantity = textViewQuantity.getText().toString();

                DatabaseReference itemRef = mDatabase.child("users").child(userId).child("cart").child(itemName);

                itemRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            int newQuantity = Integer.parseInt(itemQuantity);

                            if (newQuantity == 0) {
                                itemRef.removeValue().addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ItemDescriptionActivity.this, "Đã xóa món ăn", Toast.LENGTH_SHORT).show();
                                        buttonDecrease.setEnabled(false);
                                    } else {
                                        Toast.makeText(ItemDescriptionActivity.this, "Bỏ món thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                    setResult(RESULT_OK); // Set result to notify the calling activity
                                    finish();
                                });
                            } else {
                                itemRef.child("quantity").setValue(String.valueOf(newQuantity))
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                                buttonDecrease.setEnabled(true);
                                            } else {
                                                Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show();
                                            }
                                            setResult(RESULT_OK); // Set result to notify the calling activity
                                            finish();
                                        });
                            }
                        } else {
                            Map<String, String> itemDetails = new HashMap<>();
                            itemDetails.put("name", itemName);
                            itemDetails.put("description", itemDesc);
                            itemDetails.put("price", itemPrice);
                            itemDetails.put("image", itemImage);
                            itemDetails.put("quantity", itemQuantity);

                            itemRef.setValue(itemDetails)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn thành công", Toast.LENGTH_SHORT).show();
                                            buttonDecrease.setEnabled(true);
                                        } else {
                                            Toast.makeText(ItemDescriptionActivity.this, "Thêm món ăn thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                        setResult(RESULT_OK);
                                        finish();
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ItemDescriptionActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(ItemDescriptionActivity.this, "User not logged in or FirebaseAuth not initialized", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
