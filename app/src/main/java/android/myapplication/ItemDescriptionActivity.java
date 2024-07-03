package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class ItemDescriptionActivity extends AppCompatActivity {
    Button buttonBuyNow, buttonAddToCart;
    ImageView productImage, iconBack, buttonIncrease, buttonDecrease;
    TextView productName, productDescription, productPrice, textViewQuantity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

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
        }
        else {
            textViewQuantity.setText("1");
        }



        this.productName.setText(productName);
        this.productDescription.setText(productDescription);
        this.productPrice.setText(productPrice);

        if (productImage != null && !productImage.isEmpty()) {
            Picasso.get()
                    .load(productImage)
                    .error(R.drawable.placeholder) // Placeholder image if loading fails
                    .into(this.productImage, new Callback() {
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
            // Handle case where image URL is empty or null
            this.productImage.setImageResource(R.drawable.placeholder);
        }

        buttonIncrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());
            quantity++;
            textViewQuantity.setText(String.valueOf(quantity));
        });

        buttonDecrease.setOnClickListener(v -> {
            int quantity = Integer.parseInt(textViewQuantity.getText().toString());
            if (quantity > 1) {
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
            //Add item to realtime database


            // Show a toast message indicating the item has been added to cart
            Toast.makeText(ItemDescriptionActivity.this, "Item added to cart", Toast.LENGTH_SHORT).show();
        });

    }
}