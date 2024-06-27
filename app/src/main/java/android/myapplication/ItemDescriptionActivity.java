package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ItemDescriptionActivity extends AppCompatActivity {
    Button buttonBuyNow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        buttonBuyNow = findViewById(R.id.buttonBuyNow);
        buttonBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDescriptionActivity.this, OrderPaymentActivity.class);
            startActivity(intent);
        });
    }
}