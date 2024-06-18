package android.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;

public class OrderItemActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<String> ordersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item);

        // Initialize the orders list
        ordersList = new ArrayList<>();
        ordersList.add("Item 1");
        ordersList.add("Item 2");
        ordersList.add("Item 3");
        ordersList.add("Item 4");

        // Initialize the RecyclerView
        recyclerView = findViewById(R.id.recyclerView);

        // Set the layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set the adapter
        recyclerView.setAdapter(new OrderItemAdapter(ordersList));
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
        }
    }
}