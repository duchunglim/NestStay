package android.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemActivity.OrderViewHolder> {
    private List<String> ordersList;

    OrderItemAdapter(List<String> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderItemActivity.OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderItemActivity.OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemActivity.OrderViewHolder holder, int position) {
        String order = ordersList.get(position);
        holder.itemName.setText(order);
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }
}
