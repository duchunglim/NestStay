package android.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<Product> productList;
    private String categoryName;
    private ImageButton cartButton;
    private ImageView backButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item);
        categoryName = getIntent().getStringExtra("productCategory");

        backButton = findViewById(R.id.icon_back);
        cartButton = findViewById(R.id.cart_icon);
        cartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, OrderPaymentActivity.class);
            startActivity(intent);
        });
        backButton.setOnClickListener(v -> finish());

        // Set the category name
        TextView categoryTitle = findViewById(R.id.categoryTitle);
        categoryTitle.setText(categoryName);

        // Load the JSON file
        String json = loadJSONFromAsset();
        if (json != null) {
            try {
                // Parse the JSON to find the items that belong to the category and add it to productList
                productList = new ArrayList<>();
                JSONObject obj = new JSONObject(json);
                JSONArray menuArray = obj.getJSONArray("categories");
                for (int i = 0; i < menuArray.length(); i++) {
                    JSONObject category = menuArray.getJSONObject(i);
                    String categoryName = category.getString("name");
                    if (categoryName.equals(this.categoryName)) {
                        JSONArray itemsArray = category.getJSONArray("items");
                        for (int j = 0; j < itemsArray.length(); j++) {
                            JSONObject item = itemsArray.getJSONObject(j);
                            String itemName = item.getString("name");
                            String itemDescription = item.getString("description");
                            int itemPrice = item.getInt("price");
                            String itemImage = item.getString("image");
                            Product product = new Product(itemName, itemImage, itemPrice, itemDescription);
                            productList.add(product);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Initialize the recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(new OrderItemAdapter(productList));

    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("menu.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}