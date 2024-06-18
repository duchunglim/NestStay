package android.myapplication;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrdersViewHolder> {

    private List<String> ordersList;

    public OrderAdapter(List<String> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_grid_item, parent, false);
        return new OrdersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        String order = ordersList.get(position);
//        holder.categoryText.setText(order.title);
        holder.categoryText.setText(order);
        
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), OrderItemActivity.class);
            intent.putExtra("order", order); // Pass any data you want to OrderItemActivity
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText;
        ImageButton categoryImageButton;

        public OrdersViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.categoryText);
            categoryImageButton = itemView.findViewById(R.id.categoryImageButton);
        }
    }
}
